/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.common.cmd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import ru.apertum.qsystem.server.model.QUser;

/**
 *
 * @author zaikov
 */
public class RpcGetUser extends JsonRPC20 {

    public RpcGetUser() {
    }
    
    @Expose
    @SerializedName("result")
    private QUser result;

    public void setResult(QUser result) {
        this.result = result;
    }

    public QUser getResult() {
        return result;
    }

    public RpcGetUser(QUser result) {
        this.result = result;
    }
}
