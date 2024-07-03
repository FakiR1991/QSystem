/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.server.QServer;
import ru.apertum.qsystem.server.Spring;

/**
 *
 * @author dsavchenko
 */
@Entity
@Table(name = "users_statistic")
public class UsersStatistic {
    
    @Transient
    private Integer currentState = null;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public long id;
    
    Date dt;
    @Column(name = "dt")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDt(){
        return dt; 
    }

    Long user_id;
    @Column(name = "user_id")
    public Long getUserId() {
        return user_id;
    }
    
    Integer operation_id;     
    @Column(name = "operation_id")
    public Integer getOperationId() {
        return operation_id;
    }
    
    Date dt_start;
    @Column(name = "dt_start")
   // @Temporal(TemporalType.TIMESTAMP)
    @Type(type="timestamp")
    public Date getDtStart() {
        return dt_start;
    }
    
    Date dt_stop;
    @Column(name = "dt_stop")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDtStop() {
        return dt_stop;
    }
    
    Long service_id;
    @Column(name = "service_id")
    public Long getServiceId() {
        return service_id;
    }
    
    Integer state_in;
    @Column(name = "state_in")
    public Integer getStateIn() {
        return state_in;
    }
    
    Long client_id;    
    @Column(name = "client_id")
    public Long getClientId() {
        return client_id;
    }
    
    String place_id;
    @Column(name = "place_id")
    public String getPlaceId() {
        return place_id;
    }
    
    Integer unit_id;
    @Column(name = "unit_id")
    public Integer getUnitId() {
        return unit_id;
    }
    
    Integer adress_rs;
    @Column(name = "adress_rs")
    public Integer getAdressRs() {
        return adress_rs;
    }
    
    public void setUnitId(Integer unitId) {
        this.unit_id = unitId;
    }
    
    public void setAdressRs(Integer adressRs) {
        this.adress_rs = adressRs;
    }
    
    public void setPlaceId(String place) {
        place_id = place;
    }
    
    public void setDt(Date date){
        dt = date;
    }
    
    public void setUserId(Long userId) {
        user_id = userId;
    }
    
    public void setOperationId(Integer operId) {
        operation_id = operId;
    }
    
    public void setDtStart(Date dtStart) {
        dt_start = dtStart;
    }
    
    public void setDtStop(Date dtStop) {
        dt_stop = dtStop;
    }
    
    public void setServiceId(Long serviceId) {
        service_id = serviceId;
    }
    
    public void setStateIn(Integer stateIn) {
        state_in = stateIn;
    }
    
    public void setClientId(Long clientId) {
        client_id = clientId;
    }
    
    public UsersStatistic() { }
    
    public UsersStatistic(Long userId, String placeId, Integer adressRs, Integer unitId, UsersStatistic workingPeriod, INetProperty netProperty) {
        setUserId(userId);
        setPlaceId(placeId);
        setAdressRs(adressRs);
        setUnitId(unitId);
        
        saveWorkingPeriod(workingPeriod, netProperty);
    }
    
    /**
     * Инициализируем переменную с operationId=1 и сохраняем новой записью в таблице users_statistic
     * @param netProperty Сетевые параметры для взаимодействия с сервером
     */
    private void saveWorkingPeriod(UsersStatistic workingPeriod, INetProperty netProperty) {
        NetCommander.sendUserStat(netProperty, getUserId(), workingPeriod);
    }
    
    public void completeCurrentState(UsersStatistic workingPeriod, INetProperty netProperty) {
//        Date currDate = NetCommander.getServerTime(netProperty, getUserId());
        Date currDate = new Date();
        
        completePreviousState(currentState, currDate, workingPeriod, netProperty);
        
        currentState = null;
    }
    
    /**
     * Изменить состояние оператора
     * @param newState Идентификатор состояние в которое нужно переключить оператора
     * @param workingPeriod
     * @param netProperty Сетевые параметры для взаимодействия с сервером
     */
    public void changeState(Integer newState, UsersStatistic workingPeriod, INetProperty netProperty) {
        //если состояние на которое требуется перейти
        //является тем же, что действует в данный момент
        //нужно чтобы в setSituation каждые 5 секунд не
        //сохранялось в таблицу одно и то же состояние оператора
        if (currentState != null && newState.equals(currentState)) {
            return;
        }
        
        System.err.println("NEW STATE - " + newState);
        
//        Date currDate = NetCommander.getServerTime(netProperty, getUserId());
        Date currDate = new Date();
        
        completePreviousState(currentState, currDate, workingPeriod, netProperty);
        
        initNewState(newState, currDate);
    }
    
    private void completePreviousState(Integer previousState, Date currDate, UsersStatistic workingPeriod, INetProperty netProperty) {
        if (previousState != null) {
            setDt(currDate);
            setDtStop(currDate);
            
            NetCommander.sendUserStat(netProperty, getUserId(), this);
        }
        
        sendForSaveWorkingPeriod(currDate, workingPeriod, netProperty);
    }
    
    public void sendForSaveWorkingPeriod(Date currDate, UsersStatistic workingPeriod, INetProperty netProperty) {
        workingPeriod.setDt(currDate);
        workingPeriod.setDtStop(currDate);
        
        NetCommander.sendWorkTimeForSave(netProperty, getUserId(), workingPeriod);
    }
    
    private void initNewState(Integer newState, Date currDate) {
        currentState = newState;
        
        //если время завершения предыдущей операции не пустое,
        //то берём его, иначе - текущее время сервера
//        setDtStart(getDtStop() != null ? getDtStop() : currDate);
        setDtStart(currDate);
        setOperationId(newState);
    }
    
    private void saveToSelfDB() {
        // сохраним кастомера в базе
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("SomeTxName");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = Spring.getInstance().getTxManager().getTransaction(def);
        try {
            saveOperation(this);
//            Spring.getInstance().getHt().saveOrUpdate(this);
        } catch (Exception ex) {
            Spring.getInstance().getTxManager().rollback(status);
            throw new ServerException("Ошибка при сохранении \n" + ex.toString() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
        Spring.getInstance().getTxManager().commit(status);
//        QLog.l().logger().debug("Сохранили. " + getOperationId());
    }
    
    private void saveOperation(UsersStatistic stat) {
        try {
            Connection con = QServer.getMySQLConnection();
            PreparedStatement stmt = con.prepareStatement(
                    "INSERT INTO users_statistic "
                        + " (user_id, operation_id, dt, dt_start, dt_stop, place_id, unit_id, adress_rs) " +
                    "VALUES (?,       ?,            ?,  ?,        ?,       ?,        ?,       ?)"
            );
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            stmt.setLong(1, stat.getUserId());
            stmt.setInt(2, stat.getOperationId());
            stmt.setString(3, df.format(stat.getDt()));
            stmt.setString(4, df.format(stat.getDtStart()));
            stmt.setString(5, df.format(stat.getDtStop()));
            stmt.setInt(6, Integer.valueOf(stat.getPlaceId()));
            stmt.setInt(7, stat.getUnitId());
            stmt.setInt(8, stat.getAdressRs());
            stmt.execute();
            
        } catch (SQLException ex) {
            QLog.l().logger().error("Ошибка сохранения операции №" + operation_id, ex);
        }
    }
        
    public void Save() {
        saveToSelfDB();
    }
    
    public void saveOperation1() {
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("SomeTxName");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = Spring.getInstance().getTxManager().getTransaction(def);
        //DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String query = "select max(id) from UsersStatistic where user_id ="+ user_id + " and operation_id =" + Uses.WORK_STAT + " and dt >= :today"; // + " and dt >= '"+ df.format(new Date()) + "' and operation_id =" + Uses.WORK_STAT
        Long id = Spring.getInstance().executeSelectQuery(query, this, query);
        try {
            SaveOp(id);
        } catch (Exception ex) {
            Spring.getInstance().getTxManager().rollback(status);
            throw new ServerException("Ошибка при сохранении \n" + ex.toString() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
        Spring.getInstance().getTxManager().commit(status);
//        QLog.l().logger().debug("Сохранили логин. ");
       // System.out.println("++++++++++++++++++++++++++");
       // System.out.println(obj.size());
    }
    
    private void SaveOp(Long id) {
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("SomeTxName1");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = Spring.getInstance().getTxManager().getTransaction(def);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String q2 = "update UsersStatistic set dt_stop='" + df.format(dt_stop) + "', dt='" + df.format(dt_stop) + "' where id=" + id;
        try {
            Spring.getInstance().executeUpdateQuery(q2);
        } catch (Exception ex) {
            Spring.getInstance().getTxManager().rollback(status);
            throw new ServerException("Ошибка при сохранении \n" + ex.toString() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
        Spring.getInstance().getTxManager().commit(status);
    }
}
