/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Версия программы
 *
 * @author zaikov
 */
@Entity
@Table(name = "version_software")
public class QVersionSoftware implements Serializable {

    public QVersionSoftware() {
    }
    
    @Expose
    @SerializedName("id")
    private Long id;
    
    @Id
    @Column(name = "id")
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    private String version;
    
    @Column(name = "version")
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    private Date dateReception;
    
    @Column(name = "date_reception")
    public Date getDateReception() {
        return dateReception;
    }
    
    public void setDateReception(Date dateReception) {
        this.dateReception = dateReception;
    }
}
