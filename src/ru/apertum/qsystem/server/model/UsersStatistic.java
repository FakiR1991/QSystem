/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model;
import com.google.gson.annotations.Expose;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import javax.persistence.Id;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.Spring;

/**
 *
 * @author dsavchenko
 */
@Entity
@Table(name = "users_statistic")
public class UsersStatistic {
    public UsersStatistic(){
        
    }
    
    Date dt;
    @Column(name = "dt")
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
    public Date getDtStart() {
        return dt_start;
    }
    
    Date dt_stop;
    @Column(name = "dt_stop")
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
    
    
        private void saveToSelfDB() {
        // сохраним кастомера в базе
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("SomeTxName");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = Spring.getInstance().getTxManager().getTransaction(def);
        try {
           // if (input_data == null) { // вот жеж черд дернул выставить констрейнт на то что введенные данные не нул, а они этот ввод редко нужкн
           //     input_data = "";
         //   }
            Spring.getInstance().getHt().saveOrUpdate(this);
            // костыль. Если кастомер оставил отзывы прежде чем попал в БД, т.е. во время работы еще с ним.
        } catch (Exception ex) {
            Spring.getInstance().getTxManager().rollback(status);
            throw new ServerException("Ошибка при сохранении \n" + ex.toString() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
        Spring.getInstance().getTxManager().commit(status);
        QLog.l().logger().debug("Сохранили. " + getOperationId());
    }
        
        public void Save() {
            saveToSelfDB();
        }
        
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public long id;
}
