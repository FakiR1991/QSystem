/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.client.forms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ClientException;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.server.model.QTerminal;

/**
 *
 * @author zaikov
 */
public class FPaperUsage extends javax.swing.JDialog {

    private static FPaperUsage dialog;
    private static INetProperty netProperty;
    private static Integer unitId;
    
    public FPaperUsage(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        loadTerminalsList();
    }
    
    public static void show(INetProperty netProps, JFrame owner, Integer uid) {
        QLog.l().logger().info("Окно управления терминалами.");
        netProperty = netProps;
        unitId = uid;
        if (dialog == null) {
            dialog = new FPaperUsage(owner, true);
        }
        dialog.setLocation(Math.round(owner.getLocation().x + owner.getWidth() / 2 - dialog.getWidth() / 2),
                           Math.round(owner.getLocation().y + owner.getHeight() / 2 - dialog.getHeight() / 2));
        dialog.setVisible(true);
    }
    
    private void loadTerminalsList() {
        DefaultListModel<QTerminal> lm = new DefaultListModel<>();
        LinkedList<QTerminal> terminals = NetCommander.getTerminalsList(netProperty, unitId);
        
        for (QTerminal terminal : terminals) {
            lm.addElement(terminal);
        }
        
        jListTerminals.setModel(lm);
    }
    
    private void resetPaperUsage() {
        if (JOptionPane.showConfirmDialog(this,
                                          "Вы действительно хотите обнулись счётчик использованной бумаги в выбранном терминале?",
                                          "Сброс счётчика бумаги",
                                          JOptionPane.YES_NO_OPTION) == 1) {
            return;
        }

        QTerminal terminal = (QTerminal)jListTerminals.getSelectedValue();
        final String result;
        try {
            result = NetCommander.getWelcomeState(
                    netPropWelcome(QConfig.cfg().getClientPort(), terminal.getIp()),
                    null,
                    true
            );
        } catch (Exception ex) {
            QLog.l().logger().error("Терминал не ответил на запрос о состоянии или произошла ошибка. \"" + ex.getMessage() + "\"");
            JOptionPane.showMessageDialog(dialog,
                                          "Терминал не ответил на запрос о состоянии или произошла ошибка. \"" + ex.getMessage() + "\"",
                                          "Ошибка",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(dialog, result, "Информация", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showPaperUsage() {
        QTerminal terminal = (QTerminal)jListTerminals.getSelectedValue();
        final String result;
        try {
            result = NetCommander.getWelcomeState(
                    netPropWelcome(QConfig.cfg().getClientPort(), terminal.getIp()),
                    null,
                    false
            );
        } catch (Exception ex) {
            QLog.l().logger().error("Терминал не ответил на запрос о состоянии или произошла ошибка. \"" + ex.getMessage() + "\"");
            JOptionPane.showMessageDialog(dialog,
                                          "Терминал не ответил на запрос о состоянии или произошла ошибка. \"" + ex.getMessage() + "\"",
                                          "Ошибка",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(dialog, result, "Информация", JOptionPane.INFORMATION_MESSAGE);
    }
    
    protected INetProperty netPropWelcome(Integer port, String ip) {
        return new INetProperty() {

            @Override
            public Integer getPort() {
                return port;
            }

            @Override
            public InetAddress getAddress() {
                InetAddress adr = null;
                try {
                    adr = InetAddress.getByName(ip);
                } catch (UnknownHostException ex) {
                    throw new ClientException("Error! " + ex);
                }
                return adr;
            }
        };
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jListTerminals = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Управление терминалами");

        jListTerminals.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jListTerminals);

        jButton1.setText("Обнулить счётчик бумаги");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Терминалы:");

        jButton2.setText("Расходование бумаги");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 399, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        resetPaperUsage();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        showPaperUsage();
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jListTerminals;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
