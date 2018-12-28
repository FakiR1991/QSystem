/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.common.cmd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.LinkedList;
import ru.apertum.qsystem.server.QSession;

/**
 *
 * @author zaikov
 */
public class RpcGetAllSessions extends JsonRPC20 {

    @Expose
    @SerializedName("result")
    private LinkedList<QSession> result = new LinkedList<>();
    
    public RpcGetAllSessions() { }
    
    public RpcGetAllSessions(LinkedList<QSession> sessions) {
        this.result = sessions;
    }

    public LinkedList<QSession> getResult() {
        return result;
    }
}