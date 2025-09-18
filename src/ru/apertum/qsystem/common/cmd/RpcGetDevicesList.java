/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.common.cmd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.LinkedList;
import ru.apertum.qsystem.server.model.QDevice;

/**
 *
 * @author zaikov
 */
public class RpcGetDevicesList extends JsonRPC20 {

    public RpcGetDevicesList() { }
    
    @Expose
    @SerializedName("result")
    private LinkedList<QDevice> result;

    public void setResult(LinkedList<QDevice> result) {
        this.result = result;
    }

    public LinkedList<QDevice> getResult() {
        return result;
    }

    public RpcGetDevicesList(LinkedList<QDevice> result) {
        this.result = result;
    }
}