/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 *
 * @author zaikov
 */
public class QDeviceSelected implements Serializable {
    @Expose
    @SerializedName("device")
    private QDevice device;
    
    @Expose
    @SerializedName("device_type")
    private QDeviceType deviceType;
    
    @Expose
    @SerializedName("count")
    private int count;

    public QDeviceSelected(QDevice device, QDeviceType deviceType, int count) {
        this.device = device;
        this.deviceType = deviceType;
        this.count = count;
    }

    public QDevice getDevice() {
        return device;
    }

    public void setDevice(QDevice device) {
        this.device = device;
    }

    public QDeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(QDeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}