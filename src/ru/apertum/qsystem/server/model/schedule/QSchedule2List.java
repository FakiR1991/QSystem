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
     * @param unitId - идентификатор отделения
     * @param type - СПиАО или СЦ
     * @return Возвращает расписание для выбранного отделения и типа услуги. Если не найдено, то возвращает null.
     */
    public QSchedule2 getSchedule(Integer unitId, Integer type) {
        if (unitId == null || type == null) {
            return null;
        }
        for (Object object : getItems()) {
            QSchedule2 sch = (QSchedule2)object;
            if (Objects.equals(sch.getUnitId(), unitId) && Objects.equals(sch.getType(), type)) {
                return sch;
            }
        }
        return null;
    }
}