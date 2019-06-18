/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.common.cmd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.LinkedList;
import ru.apertum.qsystem.server.model.QTerminal;

/**
 *
 * @author zaikov
 */
public class RpcGetTerminalsList extends JsonRPC20 {
    public RpcGetTerminalsList() {
    }

    public RpcGetTerminalsList(LinkedList<QTerminal> result) {
        this.result = result;
    }

    @Expose
    @SerializedName("result")
    private LinkedList<QTerminal> result;

    public void setResult(LinkedList<QTerminal> result) {
        this.result = result;
    }

    public LinkedList<QTerminal> getResult() {
        return result;
    }
}