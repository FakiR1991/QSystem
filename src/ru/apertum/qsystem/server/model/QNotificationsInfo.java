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
    
    public QNotificationsInfo() { }
    
    public QNotificationsInfo(long id,
                              String fio,
                              String email,
                              String phone) {
        this.id = id;
        this.fio = fio;
        this.email = email;
        this.phone = phone;
    }
    
    //id
    private Long id;
    
    @Id
    @Column(name = "id")
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    //fio
    private String fio;
    
    @Column(name = "fio")
    public String getFio() {
        return fio;
    }
    
    public void setFio(String fio) {
        this.fio = fio;
    }
    
    //email
    private String email;
    
    @Column(name = "email")
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    //phone
    private String phone;
    
    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
