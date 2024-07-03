/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.webservice.prereg.notgenerated;

import java.util.ArrayList;
import java.util.Date;
import ru.apertum.qsystem.server.webservice.prereg.Filial;
import ru.apertum.qsystem.server.webservice.prereg.ReserveInterval;

/**
 *
 * @author zaikov
 */
public interface WSCallback {
    ArrayList<Filial> getFilialList();
    
    ArrayList<ReserveInterval> getFilialFreeTickets(int id, Date date);
    
    boolean setReserve(int ticketId, int reasonId, int ticketNumber,
            int myIdcAccId, int juridical, String name1, String name2, String name3,
            String title, String email, String phone, String comments);
}