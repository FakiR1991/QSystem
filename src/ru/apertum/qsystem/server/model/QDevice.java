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
public class QDevice implements Serializable {
    @Expose
    @SerializedName("model_id")
    private int modelId;
    
    @Expose
    @SerializedName("model_name")
    private String modelName;
    
    @Expose
    @SerializedName("maker_id")
    private int makerId;
    
    @Expose
    @SerializedName("maker_name")
    private String makerName;
    
    @Expose
    @SerializedName("full_name")
    private String fullName;

    public QDevice(int modelId, String modelName, int makerId, String makerName, String fullName) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.makerId = makerId;
        this.makerName = makerName;
        this.fullName = fullName;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getMakerId() {
        return makerId;
    }

    public void setMakerId(int makerId) {
        this.makerId = makerId;
    }

    public String getMakerName() {
        return makerName;
    }

    public void setMakerName(String makerName) {
        this.makerName = makerName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public static LinkedList<QDevice> getDevicesList(int deviceTypeId) throws SQLException {
        LinkedList<QDevice> devicesList = new LinkedList<>();
        
//        DriverManager.setLoginTimeout(3);
        
        try (Connection con = DriverManager.getConnection(amar1ConnectionString, amar1DbUser, amar1UserPass)) {
            try (CallableStatement stmt = con.prepareCall("{ ? = call bs.qsys.get_device_list(?) }")) {
                stmt.registerOutParameter(1, OracleTypes.CURSOR);
                stmt.setInt(2, deviceTypeId);
//                stmt.setQueryTimeout(3);
                stmt.execute();
                try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                    while (rs.next()) {
                        int modelId = rs.getInt(1);
                        String modelName = rs.getString(2);
                        int makerId = rs.getInt(3);
                        String makerName = rs.getString(13);
                        String fullName = rs.getString(14);
                        
                        QDevice device = new QDevice(modelId, modelName, makerId, makerName, fullName);
                        devicesList.add(device);
                    }
                    rs.close();
                }
                stmt.close();
            }
            con.close();
        } catch (SQLException e) {
            QLog.l().logger().error("Ошибка получения списка устройств", e);
            throw e;
        }
        
        return devicesList;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}