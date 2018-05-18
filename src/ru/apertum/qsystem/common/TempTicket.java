/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.common;

/**
 *
 * @author zaikov
 */
public class TempTicket {
    public int id;
    public String code;
    public int state;

    public TempTicket(int id, String code, int state) {
        this.id = id;
        this.code = code;
        this.state = state;
    }
}