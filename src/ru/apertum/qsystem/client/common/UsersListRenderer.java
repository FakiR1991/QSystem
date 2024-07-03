/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.client.common;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import ru.apertum.qsystem.server.model.QUser;

/**
 *
 * @author zaikov
 */
public class UsersListRenderer extends JLabel implements ListCellRenderer<QUser> {

    @Override
    public Component getListCellRendererComponent(JList<? extends QUser> list, QUser value, int index, boolean isSelected, boolean cellHasFocus) {
        QUser user = value;
        if (user.getShadow() == null) {
            
        }
        else if (user.getShadow().getStartTime() == null) {
            Date lastActivityDate = user.getLastPauseDate() != null
                                        ? user.getLastPauseDate().after(user.getShadow().getFinTime())
                                              ? user.getLastPauseDate()
                                              : user.getShadow().getFinTime()
                                        : user.getShadow().getFinTime();                        

            final int mnt = Math.round((new Date().getTime() - lastActivityDate.getTime()) / 1000 / 60);
            if (user.isPause())
                setText(user.getName() + " (" + "перерыв" + ")");
            else
                setText(user.getName() + " (" + "свободно " + mnt + " мин.)");
        } else {
            final int mnt = Math.round((new Date().getTime() - user.getShadow().getStartTime().getTime()) / 1000 / 60);
            setText(user.getName() + " (" + "в работе " + mnt + " мин.)");
        }
        
        if (isSelected) {
            setBackground(Color.GREEN);
            setForeground(Color.GREEN);
        } else {
            setBackground(null);
            setForeground(null);
        }
        
        return this;
    }
}