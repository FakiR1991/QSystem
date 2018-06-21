/*
 * Copyright (C) 2018 Apertum Project LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.server.model;

import java.util.LinkedList;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import ru.apertum.qsystem.server.Spring;

/**
 *
 * @author zaikov
 */
public class QUnitList extends ATListModel<QUnit> {

    public QUnitList() {
        super();
    }
    
    public static QUnitList getInstance() {
        return QUnitListHolder.INSTANCE;
    }

    @Override
    protected LinkedList<QUnit> load() {
        DetachedCriteria criteria = DetachedCriteria.forClass(QUnit.class);
        criteria.addOrder(Order.asc("id"));
        final LinkedList<QUnit> units = new LinkedList<>(
            Spring.getInstance().getHt().findByCriteria(criteria)
        );
        return units;
    }
    
    private static class QUnitListHolder {
        private static final QUnitList INSTANCE = new QUnitList();
    }
}