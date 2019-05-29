/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model.schedule;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.model.IidGetter;

/**
 *
 * @author zaikov
 */
@Entity
@Table(name = "schedule_to_services")
public class QScheduleToServices implements IidGetter, Serializable {

    public QScheduleToServices() { }
    
    @Id
    @Column(name = "id")
    private Long id = new Date().getTime();

    @Override
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof QScheduleToServices)) {
            throw new TypeNotPresentException("Неправильный тип для сравнения", new ServerException("Неправильный тип для сравнения"));
        }
        return id.equals(((QScheduleToServices)o).id);
    }

    @Override
    public int hashCode() {
        return (int)(this.id != null ? this.id : 0);
    }
    
    private String name = "";

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Тип услуг для которых это расписание (СПиАО или СЦ);
     * 1 - СПиАО, 2 - СЦ
     */
    @Column(name = "type")
    private Integer type;
    
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    
    /**
     * ИД отделения
     */
    @Column(name = "unit_id")
    private Integer unitId;
    
    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }
    
    /**
     * Услуга для которой указан план работы
     */
    @Column(name = "service_id")
    private Long serviceId;    
    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "service_id")
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
    
    /**
     * План работы для указанной услуги
     */
    @Column(name = "schedule_id")
    private Long scheduleId;

//    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    @JoinColumn(name = "schedule_id")
    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }
}