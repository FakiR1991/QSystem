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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.apertum.qsystem.common.Uses;

/**
 *
 * @author zaikov
 */
@Entity
@Table(name = "ip_to_unit")
public class QComputer implements IidGetter, Serializable {

    public QComputer() { }
    
    public QComputer(String ip, Long unitId, String name) {
        this.ip = ip;
        this.unitId = unitId.intValue();
        this.name = name;
    }

    @Override
    public String toString() {
        String prefix = "";
        switch(getUnitId()) {
            case Uses.UNIT_TIRASPOL_KARL_MARX:
                prefix = "[ТИРАСПОЛЬ]";
                break;
            case Uses.UNIT_BENDERY_LAZO:
                prefix = "[БЕНДЕРЫ]";
                break;
            case Uses.UNIT_RYBNICA:
                prefix = "[РЫБНИЦА]";
                break;
            default:
                prefix = "[НЕИЗВЕСТНОЕ ОТДЕЛЕНИЕ]";
                break;
        }
        return prefix + " " + getName();
    }
    
    @Expose
    @SerializedName("id")
    private Long id;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * IP компьютера
     */
    @Expose
    @SerializedName("ip")
    private String ip;

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Column(name = "ip")
    public String getIp() {
        return ip;
    }
    
    /**
     * Идентификатор зала (Тирасполь - 5, Бендеры - 6 и т. д.)
     */
    @Expose
    @SerializedName("unit_id")
    private Integer unitId;

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }
    
    @Column(name = "unit_id")
    public Integer getUnitId() {
        return unitId;
    }
    
    /**
     * Название машины
     */
    @Expose
    @SerializedName("name")
    private String name;

    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name = "name")
    @Override
    public String getName() {
        return name;
    }
    
    @Expose
    @SerializedName("plan")
    private List<QPlanService> planServices = new ArrayList<>();

    public void setPlanServices(List<QPlanService> planServices) {
        this.planServices = planServices;
        planServiceList = new QPlanServiceList(planServices);
    }
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "ip", insertable = true, nullable = false, updatable = true, referencedColumnName="ip")
    @Fetch(FetchMode.SELECT) // Это отсечение дублирования при джойне таблици, т.к. в QPlanService есть @OneToOne к QService, и в нем есть @OneToMany к QServiceLang - дублится по количеству переводов
    public List<QPlanService> getPlanServices() {
        return planServices;
    }
    private QPlanServiceList planServiceList = new QPlanServiceList(new LinkedList<>());

    /**
     * Только для отображения в админке в виде списка
     *
     * @return
     */
    @Transient
    public QPlanServiceList getPlanServiceList() {
        return planServiceList;
    }
    
    public boolean hasService(long serviceId) {
        return planServices.stream().anyMatch((qPlanService) -> (Objects.equals(serviceId, qPlanService.getService().getId())));
    }

    public boolean hasService(QService service) {
        return hasService(service.getId());
    }
    
    /**
     * Добавить сервис в список обслуживаемых ip. Помнить про ДБ.
     *
     * @param service добавляемый сервис.
     */
    public void addPlanService(QService service) {
        // в список услуг
        planServiceList.addElement(new QPlanService(service, this.ip, 1));
        setPlanServices(planServiceList.getServices());
    }

    /**
     * Добавить сервис в список обслуживаемых на данном ip используя параметры. Используется при добавлении на горячую.
     *
     * @param service добавляемый сервис
     * @param coefficient приоритет обработки
     */
    public void addPlanService(QService service, int coefficient) {
        // в список услуг
        planServiceList.addElement(new QPlanService(service, this.ip, coefficient));
        setPlanServices(planServiceList.getServices());
    }
    
    /**
     * Удалить сервис из списка обслуживаемых компьютером.
     *
     * @param serviceId удаляемый сервис.
     * @return
     */
    public boolean deletePlanService(long serviceId) {
        for (QPlanService qPlanService : planServices) {
            if (Objects.equals(serviceId, qPlanService.getService().getId())) {
                planServiceList.removeElement(qPlanService);
                setPlanServices(planServiceList.getServices());
                return true;
            }
        }
        return false;
    }

    public boolean deletePlanService(QService service) {
        return deletePlanService(service.getId());
    }
}