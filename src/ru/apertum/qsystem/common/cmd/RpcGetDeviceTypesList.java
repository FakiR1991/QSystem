/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.common.cmd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.LinkedList;
import ru.apertum.qsystem.server.model.QDeviceType;

/**
 *
 * @author zaikov
 */
public class RpcGetDeviceTypesList extends JsonRPC20 {

    public RpcGetDeviceTypesList() { }
    
    @Expose
    @SerializedName("result")
    private LinkedList<QDeviceType> result;

    public void setResult(LinkedList<QDeviceType> result) {
        this.result = result;
    }

    public LinkedList<QDeviceType> getResult() {
        return result;
    }

    public RpcGetDeviceTypesList(LinkedList<QDeviceType> result) {
        this.result = result;
    }
}