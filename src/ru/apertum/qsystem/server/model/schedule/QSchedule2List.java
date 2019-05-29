/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model.schedule;

import java.util.LinkedList;
import java.util.Objects;
import javax.swing.ComboBoxModel;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.model.ATListModel;

/**
 *
 * @author zaikov
 */
public class QSchedule2List extends ATListModel<QSchedule2> implements ComboBoxModel {
    private QSchedule2List() {
        super();
    }
    
    public static QSchedule2List getInstance() {
        return QSchedule2ListHolder.INSTANCE;
    }

    private static class QSchedule2ListHolder {

        private static final QSchedule2List INSTANCE = new QSchedule2List();
    }

    @Override
    protected LinkedList<QSchedule2> load() {
        return new LinkedList<>(Spring.getInstance().getHt().
                findByCriteria(DetachedCriteria.forClass(QSchedule2.class).
                        setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)));
    }
    private QSchedule2 selected;

    @Override
    public void setSelectedItem(Object anItem) {
        selected = (QSchedule2)anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }
    
    /**
     * Получить расписание по типу услуги и ИД отделения
     * @param scheduleId ИД плана работы который нужно вернуть
     * @return Возвращает расписание по ИД. Если не найдено, то возвращает null.
     */
    public QSchedule2 getSchedule(Long scheduleId) {
        for (Object object : getItems()) {
            QSchedule2 sch = (QSchedule2)object;
            if (Objects.equals(sch.getId(), scheduleId)) {
                return sch;
            }
        }
        return null;
    }
}