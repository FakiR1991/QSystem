/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model.postponed;

import java.util.LinkedList;
import javax.swing.DefaultListModel;
import org.apache.commons.collections.CollectionUtils;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.controller.Executer;

/**
 *
 * @author zaikov
 */
public class QMovedToBankList extends DefaultListModel {
    
    private QMovedToBankList() { }
    
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
    
    public QCustomer getByExtId(long extId) {
        for (Object object : toArray()) {
            QCustomer c = (QCustomer)object;
            if (extId == c.getExtId()) {
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