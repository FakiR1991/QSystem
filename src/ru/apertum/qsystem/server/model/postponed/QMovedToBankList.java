/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model.postponed;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import org.apache.commons.collections.CollectionUtils;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.controller.Executer;

/**
 *
 * @author zaikov
 */
public class QMovedToBankList extends DefaultListModel {
    
    /**
     * Таймер по которому будем выгонять временных отложенных
     */
    private Timer timerOut;
    private final int HOUR_IN_MILLIS = 60 * 60 * 1000;
    
    private QMovedToBankList() {
        if (QConfig.cfg().isServer()) {
            //каждый час проверяем надо ли почистить список людей отправленных на оплату
            timerOut = new Timer(HOUR_IN_MILLIS, (ActionEvent e) -> {
                Executer.MOVED_TO_BANK_TASK_LOCK.lock();
                try {
                    final ArrayList<QCustomer> forDel = new ArrayList<>();
                    for (QCustomer customer : getMovedToBankCustomers()) {
                        //если прошло больше трёх часов с тех пор как его отправили оплачивать, то удаляем
                        if ( (System.currentTimeMillis() - customer.getStandTime().getTime()) > 3 * HOUR_IN_MILLIS ) {
                            
                            QLog.l().logger().debug("Удаляем по таймеру из списка ушедших на оплату кастомера №" + customer.getPrefix() + customer.getNumber());
                            
                            //добавляем кастомера в список на удаление
                            forDel.add(customer);
                            //добавляем запись в таблицу clients
                            //состояние не сохранится в БД, потому что user у текущего кастомера равен null
                            //т.к. мы завершили работу с ним [сделали setUser(null)] и проставили перед этим stateIn = 10
                            customer.setState(CustomerState.STATE_DEAD_AFTER_PAYMENT);
                            
                        }
                    }
                    forDel.stream().forEach((qCustomer) -> {
                        removeElement(qCustomer);
                    });
                } catch (Exception ex) {
                    throw new ServerException("Ошибка при удалении кастомера из списка ушедших на оплату по таймеру " + ex);
                } finally {
                    Executer.MOVED_TO_BANK_TASK_LOCK.unlock();
                }
            });
            timerOut.start();
        }
    }
    
    public QMovedToBankList loadMovedToBankList(LinkedList<QCustomer> customers) {
        Executer.MOVED_TO_BANK_TASK_LOCK.lock();
        try {
            clear();
            for (QCustomer cust : customers) {
                addElement(cust);
            }
        } finally {
            Executer.MOVED_TO_BANK_TASK_LOCK.unlock();
        }
        return this;
    }
    
    public LinkedList<QCustomer> getMovedToBankCustomers() {
        final LinkedList<QCustomer> list = new LinkedList<>();
        CollectionUtils.addAll(list, elements());
        return list;
    }
    
    /**
     * Может вернуть NULL если не нашлось
     * @param id
     * @return Вернёт объект QCustomer которому соответствует id или null, если не найдено.
     */
    public QCustomer getById(long id) {
        for (Object object : toArray()) {
            QCustomer c = (QCustomer)object;
            if (id == c.getId()) {
                return c;
            }
        }
        return null;
    }
    
    public static QMovedToBankList getInstance() {
        return QMovedToPaymentListHolder.INSTANCE;
    }

    private static class QMovedToPaymentListHolder {
        private static final QMovedToBankList INSTANCE = new QMovedToBankList();
    }
    
}