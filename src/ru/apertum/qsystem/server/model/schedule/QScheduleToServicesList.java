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
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.model.ATListModel;

/**
 *
 * @author zaikov
 */
public class QScheduleToServicesList extends ATListModel<QScheduleToServices> implements ComboBoxModel {
    private QScheduleToServicesList() {
        super();
    }
    
    public static QScheduleToServicesList getInstance() {
        return QScheduleToServicesListHolder.INSTANCE;
    }

    private static class QScheduleToServicesListHolder {
        private static final QScheduleToServicesList INSTANCE = new QScheduleToServicesList();
    }

    @Override
    protected LinkedList<QScheduleToServices> load() {
        LinkedList<QScheduleToServices> array = null;
        try {
            array = new LinkedList<>(Spring.getInstance().getHt().
                    findByCriteria(DetachedCriteria.forClass(QScheduleToServices.class).
                    setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)));
        } catch (Exception e) {
            QLog.l().logger().error("Ошибка получения списка привязок услуг и планов работы", e);
        }
        
        return array;
    }
    
    private QScheduleToServices selected;

    @Override
    public void setSelectedItem(Object anItem) {
        selected = (QScheduleToServices)anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }
    
    /**
     * Получить расписание по типу услуги, ИД услуги и ИД отделения
     * @param serviceId - ИД услуги
     * @param unitId - идентификатор отделения
     * @param type - СПиАО или СЦ
     * @return Возвращает расписание для выбранного отделения и типа услуги. Если не найдено, то возвращает null.
     */
    public QSchedule2 getSchedule(Long serviceId, Integer unitId, Integer type) {
        if (serviceId == null || unitId == null || type == null) {
            return null;
        }
        for (Object object : getItems()) {
            QScheduleToServices sch = (QScheduleToServices)object;
            //ищем план работы соответствующий параметрам
            if (Objects.equals(sch.getServiceId(), serviceId)
                    && Objects.equals(sch.getUnitId(), unitId)
                    && Objects.equals(sch.getType(), type)) {
                return QSchedule2List.getInstance().getSchedule(sch.getScheduleId());
            }
        }
        return null;
    }
}