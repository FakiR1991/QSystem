/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.client.forms;

import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableStringConverter;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.server.model.QDeviceType;
import ru.apertum.qsystem.server.model.QDevice;
import ru.apertum.qsystem.server.model.QDeviceSelected;

/**
 *
 * @author zaikov
 */
public class FSelectDevice extends javax.swing.JDialog {
    private final INetProperty netProperty;
    //список оборудования загруженный при переключении типа оборудования
    private LinkedList<QDevice> loadedDevicesList;
    //список оборудования добавленного для талона
    private LinkedList<QDeviceSelected> selectedDevices = new LinkedList<>();

    public LinkedList<QDeviceSelected> getSelectedDevices() {
        return selectedDevices;
    }
    
    /**
     * Creates new form FSelectDevice
     * @param parent
     * @param modal
     * @param netProperty
     */
    public FSelectDevice(java.awt.Frame parent, boolean modal, final INetProperty netProperty) {
        super(parent, modal);
        initComponents();
        
        this.netProperty = netProperty;
        devicesCountSpinner.setValue(1);
        
        Uses.setLocation(this);
        setTitle("Выбор устройства");
        loadDeviceTypes();
        setChangeSelectedDevicesTableListener();
        clearSelectedDevices();
    }
    
    private void clearSelectedDevices() {
        this.selectedDevices = new LinkedList<>();
        //очищаем список выбранного оборудования
        ((DefaultTableModel) selectedDevicesTable.getModel()).setRowCount(0);
    }
    
    private void setChangeSelectedDevicesTableListener() {
        selectedDevicesTable.getModel().addTableModelListener((TableModelEvent event) -> {
            if (event.getType() == TableModelEvent.UPDATE) {
                int row = event.getFirstRow();
                int column = event.getColumn();
                
                int targetColumnIndex = 2;
                if (column == targetColumnIndex) {
                    Object newValue = selectedDevicesTable.getModel().getValueAt(row, column);
                    
                    try {
                        selectedDevices.get(row).setCount((int)newValue);
                    } catch (Exception e) { }
                }
            }
        });
    }
    
    private void setDevicesFilter() {
        DefaultTableModel model = (DefaultTableModel) devicesTable.getModel();
        RowFilter<DefaultTableModel, Object> rf;
        try {
            rf = RowFilter.regexFilter("(?i)" + deviceNameFilterTextField.getText(), 1);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        TableRowSorter sorter = new TableRowSorter(model);
        sorter.setStringConverter(new TableStringConverter() {
            @Override
            public String toString(TableModel tableModel, int row, int column) {
                String maker = tableModel.getValueAt(row, 0).toString().toLowerCase();
                String model = tableModel.getValueAt(row, 1).toString().toLowerCase();
                return maker + " " + model;
            }
        });
        sorter.setRowFilter(rf);
        devicesTable.setRowSorter(sorter);
    }
    
    private void loadDeviceTypes() {
        LinkedList<QDeviceType> deviceTypes = NetCommander.getDeviceTypesList(netProperty);
        updateDeviceTypesList(deviceTypes);
    }
    
    private void updateDeviceTypesList(LinkedList<QDeviceType> deviceTypes) {
        DefaultListModel<QDeviceType> model = new DefaultListModel();
        deviceTypesList.setModel(model);
        
        for (QDeviceType deviceType : deviceTypes) {
            model.addElement(deviceType);
        }
        
        deviceTypesList.setModel(model);
    }
    
    private void loadDevices(int deviceTypeId) {
        LinkedList<QDevice> devices = NetCommander.getDevicesList(netProperty, deviceTypeId);
        updateDevicesTable(devices);
    }
    
    private void updateDevicesTable(LinkedList<QDevice> devices) {
        DefaultTableModel model = (DefaultTableModel) devicesTable.getModel();
        model.setRowCount(0);
        
        for (QDevice device : devices) {
            model.addRow(new Object[] { device.getMakerName(), device.getModelName() });
        }
        devicesTable.setModel(model);
        
        this.loadedDevicesList = devices;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        deviceTypesList = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        devicesTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        deviceNameFilterTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        devicesCountSpinner = new javax.swing.JSpinner();
        addButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        selectedDevicesTable = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        removeSelectedDevice = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);

        jLabel1.setText("Тип оборудования:");

        deviceTypesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        deviceTypesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                deviceTypesListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(deviceTypesList);

        devicesTable.setAutoCreateRowSorter(true);
        devicesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Производитель", "Модель"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        devicesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        devicesTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(devicesTable);
        devicesTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (devicesTable.getColumnModel().getColumnCount() > 0) {
            devicesTable.getColumnModel().getColumn(0).setPreferredWidth(120);
            devicesTable.getColumnModel().getColumn(1).setMinWidth(500);
        }

        jLabel2.setText("Оборудование:");

        jLabel3.setText("Поиск по оборудованию:");

        deviceNameFilterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                deviceNameFilterTextFieldKeyReleased(evt);
            }
        });

        jLabel4.setText("Кол-во:");

        devicesCountSpinner.setValue(1);

        addButton.setText("Добавить");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        selectedDevicesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Производитель", "Модель", "Кол-во"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        selectedDevicesTable.setColumnSelectionAllowed(true);
        jScrollPane3.setViewportView(selectedDevicesTable);
        selectedDevicesTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (selectedDevicesTable.getColumnModel().getColumnCount() > 0) {
            selectedDevicesTable.getColumnModel().getColumn(2).setResizable(false);
        }

        jLabel5.setText("Выбранное оборудование:");

        cancelButton.setText("Отмена");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("Ок");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        removeSelectedDevice.setText("Удалить");
        removeSelectedDevice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSelectedDeviceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel1)))
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(4, 4, 4)
                                .addComponent(deviceNameFilterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)
                                .addGap(4, 4, 4)
                                .addComponent(devicesCountSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2))))
                .addGap(10, 12, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(removeSelectedDevice, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel3))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(deviceNameFilterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jLabel4))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(devicesCountSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(addButton)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(7, 7, 7)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton)
                    .addComponent(removeSelectedDevice))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        clearSelectedDevices();
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        int selectedRow = devicesTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        QDeviceType deviceType = deviceTypesList.getSelectedValue();
        QDevice selectedDevice = getSelectedDevice();
        int count = (int) devicesCountSpinner.getValue();
        
        if (selectedDevice == null) {
            JOptionPane.showMessageDialog(this, "Не удалось добавить выбранное оборудование", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        selectedDevices.add(new QDeviceSelected(selectedDevice, deviceType, count));
        ((DefaultTableModel) selectedDevicesTable.getModel()).addRow(
                new Object[] {
                    selectedDevice.getMakerName(),
                    selectedDevice.getModelName(),
                    count
                }
        );
    }//GEN-LAST:event_addButtonActionPerformed

    private QDevice getSelectedDevice() {
        int selectedRow = devicesTable.getSelectedRow();
        
        String selectedMaker = (String) devicesTable.getValueAt(selectedRow, 0);
        String selectedModel = (String) devicesTable.getValueAt(selectedRow, 1);
        
        for (QDevice loadedDevice : loadedDevicesList) {
            String deviceMaker = loadedDevice.getMakerName();
            String deviceModel = loadedDevice.getModelName();
            
            if (deviceMaker.equalsIgnoreCase(selectedMaker) && deviceModel.equalsIgnoreCase(selectedModel)) {
                return loadedDevice;
            }
        }
        
        return null;
    }
    
    private void deviceTypesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_deviceTypesListValueChanged
        QDeviceType selectedDeviceType = deviceTypesList.getSelectedValue();
        if (selectedDeviceType != null) {
            loadDevices(selectedDeviceType.getDeviceTypeId());
        }
    }//GEN-LAST:event_deviceTypesListValueChanged

    private void deviceNameFilterTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_deviceNameFilterTextFieldKeyReleased
        setDevicesFilter();
    }//GEN-LAST:event_deviceNameFilterTextFieldKeyReleased

    private void removeSelectedDeviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSelectedDeviceActionPerformed
        int selectedRow = selectedDevicesTable.getSelectedRow();
        
        String selectedMaker = (String) selectedDevicesTable.getValueAt(selectedRow, 0);
        String selectedModel = (String) selectedDevicesTable.getValueAt(selectedRow, 1);
        
        for (Iterator<QDeviceSelected> iterator = selectedDevices.iterator(); iterator.hasNext();) {
            QDeviceSelected next = iterator.next();
            String deviceMaker = next.getDevice().getMakerName();
            String deviceModel = next.getDevice().getModelName();
            
            //если нашли в списке выбранного оборудования нужный итем для удаления
            if (deviceMaker.equalsIgnoreCase(selectedMaker) && deviceModel.equalsIgnoreCase(selectedModel)) {
                //удаляем элемент из списка
                iterator.remove();
                
                //удаляем элеметр из таблицы
                int modelRow = selectedDevicesTable.convertRowIndexToModel(selectedRow);
                DefaultTableModel model = (DefaultTableModel) selectedDevicesTable.getModel();
                model.removeRow(modelRow);
            }
        }
    }//GEN-LAST:event_removeSelectedDeviceActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField deviceNameFilterTextField;
    private javax.swing.JList<QDeviceType> deviceTypesList;
    private javax.swing.JSpinner devicesCountSpinner;
    private javax.swing.JTable devicesTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton okButton;
    private javax.swing.JButton removeSelectedDevice;
    private javax.swing.JTable selectedDevicesTable;
    // End of variables declaration//GEN-END:variables
}