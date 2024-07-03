/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.webservice.prereg.notgenerated;

import java.util.List;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
import ru.apertum.qsystem.server.webservice.prereg.Filial;
import ru.apertum.qsystem.server.webservice.prereg.PreRegAPI;
import ru.apertum.qsystem.server.webservice.prereg.ReserveInterval;

/**
 *
 * @author zaikov
 */
@WebService(endpointInterface = "ru.apertum.qsystem.server.webservice.prereg.PreRegAPI")
public class WS_ServiceImpl implements PreRegAPI {

    private final WSCallback callback;
    
    public WS_ServiceImpl(WSCallback callback) {
        this.callback = callback;
    }
    
    @Override
    public List<Filial> getFilials() {
        return callback.getFilialList();
    }

    @Override
    public boolean setReserve(int ticketId, int reasonId, int ticketNumber, int myIdcAccId, int juridical, String name1, String name2, String name3, String title, String email, String phone, String comments) {
        return callback.setReserve(ticketId, reasonId, ticketNumber, myIdcAccId, juridical, name1, name2, name3, title, email, phone, comments);
    }

    @Override
    public List<ReserveInterval> getFilialsFreeTickets(int arg0, XMLGregorianCalendar arg1) {
        return callback.getFilialFreeTickets(arg0, arg1.toGregorianCalendar().getTime());
    }
}