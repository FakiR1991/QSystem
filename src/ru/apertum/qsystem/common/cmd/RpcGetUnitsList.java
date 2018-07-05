/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.common.cmd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.LinkedList;
import ru.apertum.qsystem.server.model.QUnit;

/**
 *
 * @author zaikov
 */
public class RpcGetUnitsList extends JsonRPC20 {

    public RpcGetUnitsList() { }
    
    @Expose
    @SerializedName("result")
    private LinkedList<QUnit> result;

    public void setResult(LinkedList<QUnit> result) {
        this.result = result;
    }

    public LinkedList<QUnit> getResult() {
        return result;
    }

    public RpcGetUnitsList(LinkedList<QUnit> result) {
        this.result = result;
    }
}