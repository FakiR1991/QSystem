/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author zaikov
 */
@Entity
@Table(name = "terminals")
public class QTerminal implements IidGetter, Serializable {

    public QTerminal() { }
    
    public QTerminal(String ip, Long unitId, String name, Integer point) {
        this.ip = ip;
        this.unitId = unitId.intValue();
        this.point = point;
        this.name = name;
    }

    @Override
    public String toString() {
        return getName() + " [" + getIp() + "]";
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
     * IP терминала
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
     * Поинт (группа терминалов, в каждом отделении свой point)
     */
    @Expose
    @SerializedName("point")
    private Integer point;

    public void setPoint(Integer point) {
        this.point = point;
    }

    @Column(name = "point")
    public Integer getPoint() {
        return point;
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
     * Название терминала (описание его расположения)
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
}