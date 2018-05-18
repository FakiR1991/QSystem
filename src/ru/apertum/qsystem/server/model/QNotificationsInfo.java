/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author zaikov
 */
@Entity
@Table(name = "notifications_info")
public class QNotificationsInfo implements Serializable{
    
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_FIO = 1;
    public static final int COLUMN_EMAIL = 2;
    public static final int COLUMN_PHONE = 3;
    public static final int COLUMN_MAX_WAITING_MINUTES_ABO = 4;
    public static final int COLUMN_MAX_WAITING_MINUTES_SC = 5;
    public static final int COLUMN_RATIO_ABO = 6;
    public static final int COLUMN_RATIO_SC = 7;
    public static final int COLUMN_UNIT_ID = 8;
    
    public QNotificationsInfo() { }
    
    public QNotificationsInfo(long id,
                              String fio,
                              String email,
                              String phone,
                              int maxWaitingMinutesAbo,
                              int maxWaitingMinutesSc,
                              double ratioAbo,
                              double ratioSc,
                              int unitId) {
        this.id = id;
        this.fio = fio;
        this.email = email;
        this.phone = phone;
        this.maxWaitingMinutesAbo = maxWaitingMinutesAbo;
        this.maxWaitingMinutesSC = maxWaitingMinutesSc;
        this.ratioAbo = ratioAbo;
        this.ratioSC = ratioSc;
        this.unitId = unitId;
    }
    
    private Long id;
    
    @Id
    @Column(name = "id")
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    private String fio;
    
    @Column(name = "fio")
    public String getFio() {
        return fio;
    }
    
    public void setFio(String fio) {
        this.fio = fio;
    }
    
    private String email;
    
    @Column(name = "email")
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    private String phone;
    
    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    private Integer maxWaitingMinutesAbo;
    
    @Column(name = "max_waiting_minutes_abo")
    public Integer getMaxWaitingMinutesAbo() {
        return maxWaitingMinutesAbo;
    }
    
    public void setMaxWaitingMinutesAbo(Integer maxWaitingMinutesAbo) {
        this.maxWaitingMinutesAbo = maxWaitingMinutesAbo;
    }
    
    private Integer maxWaitingMinutesSC;
    
    @Column(name = "max_waiting_minutes_sc")
    public Integer getMaxWaitingMinutesSC() {
        return maxWaitingMinutesSC;
    }
    
    public void setMaxWaitingMinutesSC(Integer maxWaitingMinutesSC) {
        this.maxWaitingMinutesSC = maxWaitingMinutesSC;
    }
    
    private Double ratioAbo;
    
    @Column(name = "ratio_abo")
    public Double getRatioAbo() {
        return ratioAbo;
    }
    
    public void setRatioAbo(Double ratioAbo) {
        this.ratioAbo = ratioAbo;
    }
    
    private Double ratioSC;
    
    @Column(name = "ratio_sc")
    public Double getRatioSC() {
        return ratioSC;
    }
    
    public void setRatioSC(Double ratioSC) {
        this.ratioSC = ratioSC;
    }
    
    private Integer unitId;
    
    @Column(name = "unit_id")
    public Integer getUnitId() {
        return unitId;
    }
    
    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }
}
