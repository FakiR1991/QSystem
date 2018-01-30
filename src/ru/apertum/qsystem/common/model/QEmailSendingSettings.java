/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.common.model;

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
@Table(name = "email_sending_settings")
public class QEmailSendingSettings implements Serializable {
    
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_SMTP_HOST = 1;
    public static final int COLUMN_SMTP_PORT = 2;
    public static final int COLUMN_EMAIL = 3;
    public static final int COLUMN_PASSWORD = 4;
    public static final int COLUMN_SUBJECT = 5;
    public static final int COLUMN_MESSAGE = 6;

    public QEmailSendingSettings() { }
    
    //id
    private Long id;
    
    @Id
    @Column(name = "id")
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    //smtpHost
    private String smtpHost;
    
    @Column(name = "smtp_host")
    public String getSmtpHost() {
        return this.smtpHost;
    }
    
    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }
    
    //smtpPort
    private String smtpPort;
    
    @Column(name = "smtp_port")
    public String getSmtpPort() {
        return this.smtpPort;
    }
    
    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }
    
    //email
    private String email;
    
    @Column(name = "email")
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    //password
    private String password;
    
    @Column(name = "password")
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    //subject
    private String subject;
    
    @Column(name = "subject")
    public String getSubject() {
        return this.subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    //message
    private String message;
    
    @Column(name = "message")
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
