/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model;

import java.util.LinkedList;
import java.util.Objects;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import ru.apertum.qsystem.server.Spring;

/**
 *
 * @author zaikov
 */
public class QTerminalList extends ATListModel<QTerminal> {
    public QTerminalList() {
        super();
    }
    
    @Override
    protected LinkedList<QTerminal> load() {
        DetachedCriteria criteria = DetachedCriteria.forClass(QTerminal.class);
        criteria.addOrder(Order.asc("unitId"));
        criteria.addOrder(Order.asc("name"));
        final LinkedList<QTerminal> terminals = new LinkedList<>(
            Spring.getInstance().getHt().findByCriteria(criteria)
        );
        return terminals;
    }
    
    public static QTerminalList getInstance() {
        return QTerminalList.QTerminalListHolder.INSTANCE;
    }
    
    private static class QTerminalListHolder {
        private static final QTerminalList INSTANCE = new QTerminalList();
    }
    
    public boolean hasByIp(String ip) {
        return getItems().stream().anyMatch((item) -> ( Objects.equals(ip, item.getIp()) ));
    }
}