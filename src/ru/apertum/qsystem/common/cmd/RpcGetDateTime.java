/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.common.cmd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 *
 * @author dsavchenko
 */
public class RpcGetDateTime extends JsonRPC20{
    
       public RpcGetDateTime() {
    }
    
    @Expose
    @SerializedName("date")
    private Date date;

    public void setResult(Date result) {
        this.date = result;
    }

    public Date getResult() {
        return date;
    }

    public RpcGetDateTime(Date result) {
        this.date = result;
    }
}
