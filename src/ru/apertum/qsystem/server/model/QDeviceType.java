/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import oracle.jdbc.internal.OracleTypes;
import ru.apertum.qsystem.common.QLog;
import static ru.apertum.qsystem.server.QServer.amar1ConnectionString;
import static ru.apertum.qsystem.server.QServer.amar1DbUser;
import static ru.apertum.qsystem.server.QServer.amar1UserPass;

/**
 *
 * @author zaikov
 */
public class QDeviceType implements Serializable {
    @Expose
    @SerializedName("device_type_id")
    private int deviceTypeId;
    
    @Expose
    @SerializedName("device_type_name")
    private String deviceTypeName;

    public QDeviceType(int deviceTypeId, String deviceTypeName) {
        this.deviceTypeId = deviceTypeId;
        this.deviceTypeName = deviceTypeName;
    }

    public int getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(int deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }
    
    public static LinkedList<QDeviceType> getDeviceTypesList() throws SQLException {
        LinkedList<QDeviceType> deviceTypesList = new LinkedList<>();
        
//        DriverManager.setLoginTimeout(3);

        try (Connection con = DriverManager.getConnection(amar1ConnectionString, amar1DbUser, amar1UserPass)) {
            try (CallableStatement stmt = con.prepareCall("{ ? = call bs.qsys.get_device_type_list }")) {
                stmt.registerOutParameter(1, OracleTypes.CURSOR);
//                stmt.setQueryTimeout(3);
                stmt.execute();
                
                try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                    while (rs.next()) {
                        int deviceTypeId = rs.getInt(1);
                        String deviceTypeName = rs.getString(2);
                        QDeviceType deviceType = new QDeviceType(deviceTypeId, deviceTypeName);
                        deviceTypesList.add(deviceType);
                    }
                    rs.close();
                }
                stmt.close();
            }
            con.close();
        } catch (SQLException e) {
            QLog.l().logger().error("Ошибка получения списка типов устройств", e);
        }
        
        return deviceTypesList;
    }

    @Override
    public String toString() {
        return getDeviceTypeName();
    }
}