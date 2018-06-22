/*
 * Copyright (C) 2018 Apertum Project LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.server.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.Spring;

/**
 *
 * @author zaikov
 */
public class QComputerList extends ATListModel<QComputer> {

    public QComputerList() {
        super();
    }
    
    public static QComputerList getInstance() {
        return QComputerListHolder.INSTANCE;
    }

    @Override
    protected LinkedList<QComputer> load() {
        DetachedCriteria criteria = DetachedCriteria.forClass(QComputer.class);
        criteria.addOrder(Order.asc("unitId"));
        criteria.addOrder(Order.asc("name"));
        final LinkedList<QComputer> computers = new LinkedList<>(
            Spring.getInstance().getHt().findByCriteria(criteria)
        );
        return computers;
    }
    
    private static class QComputerListHolder {
        private static final QComputerList INSTANCE = new QComputerList();
    }
    
    public boolean hasByIp(String ip) {
        return getItems().stream().anyMatch((item) -> ( Objects.equals(ip, item.getIp()) ));
    }
    
    @Override
    public void save() {
        //удалим ip из списка удалённых
        deleteIp(deleted);
        deleted.clear();
        
        //сохраняем все ip и привязанные им услуги
        getItems().stream().forEach(qComputer -> {
            insertUpdateIpWithPlanServices(qComputer);
        });
    }
    
    private void insertUpdateIpWithPlanServices(QComputer computer) {
        /* СНАЧАЛА ДОБАВИМ В ТАБЛИЦУ ip_to_unit ДАННЫЕ О НОВОМ IP */
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("SomeTxName");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = Spring.getInstance().getTxManager().getTransaction(def);
        
        String qInsertIP  = "insert into ip_to_unit (ip, unit_id, name) values(:ip, :unitId, :name)";
        
        try {
            final Session ses = Spring.getInstance().getTxManager().getSessionFactory().getCurrentSession();
            //добавим данные очередного ip в таблицу ip_to_unit
            Query query = ses.createSQLQuery(qInsertIP);
            query.setParameter("ip", computer.getIp());
            query.setParameter("unitId", computer.getUnitId());
            query.setParameter("name", computer.getName());
            query.executeUpdate();
            ses.flush();
        } catch (ConstraintViolationException cex) {
            QLog.l().logger().debug("ip " + computer.getIp() + " уже присутствует в таблице ip_to_unit!", cex);
        } catch (Exception ex) {
            throw new ServerException("\n" + ex.toString() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
//        Spring.getInstance().getTxManager().commit(status);
        
        /* ТЕПЕРЬ УДАЛИМ ИЗ ТАБЛИЦЫ services_to_ip ВСЕ ДАННЫЕ ПО УКАЗАННОМУ IP */
        String qDelete  = "delete from services_to_ip where ip=:ip";
        try {
            final Session ses = Spring.getInstance().getTxManager().getSessionFactory().getCurrentSession();
            //удаляем все записи в таблице services_to_ip по данному ip
            Query query = ses.createSQLQuery(qDelete);
            query.setParameter("ip", computer.getIp());
            query.executeUpdate();

            ses.flush();
        }
        catch (Exception ex) {
            throw new ServerException("\n" + ex.toString() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
//        Spring.getInstance().getTxManager().commit(status);
        
        /* ДАЛЬШЕ НАМ НУЖНО ПРОЙТИСЬ ПО ВСЕМ ПРИВЯЗАННЫМ УСЛУГАМ И ДОБАВИТЬ ЗАПИСИ В ТАБЛИЦУ services_to_ip */
        computer.getPlanServices().stream().forEach(qPlanService -> {
            String qInsertPlanServices = "insert into services_to_ip (service_id, ip, coefficient, flexible_coef) values(:serviceId, :ip, :coefficient, :flexibleCoef)";
            try {
                final Session ses = Spring.getInstance().getTxManager().getSessionFactory().getCurrentSession();
                //удаляем все записи в таблице services_to_ip по данному ip
                Query query = ses.createSQLQuery(qInsertPlanServices);
                query.setParameter("serviceId", qPlanService.getService().getId());
                query.setParameter("ip", qPlanService.getIp());
                query.setParameter("coefficient", qPlanService.getCoefficient());
                query.setParameter("flexibleCoef", qPlanService.getFlexible_coef());
                query.executeUpdate();

                ses.flush();
            }
            catch (Exception ex) {
                throw new ServerException("\n" + ex.toString() + "\n" + Arrays.toString(ex.getStackTrace()));
            }
//            Spring.getInstance().getTxManager().commit(status);
        });
        
        Spring.getInstance().getTxManager().commit(status);
    }
    
    private void deleteIp(LinkedList<QComputer> deleted) {
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("SomeTxName");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = Spring.getInstance().getTxManager().getTransaction(def);
        
        deleted.stream().forEach(qComputer -> {
            //сначала удалим назначенные услуги, а затем сам ip
            String qDeleteServicesIp = "delete from services_to_ip where ip=:ip";
            String qDeleteIp = "delete from ip_to_unit where ip=:ip";
            try {
                final Session ses = Spring.getInstance().getTxManager().getSessionFactory().getCurrentSession();
                
                //удаляем все записи в таблице services_to_ip по данному ip
                Query query = ses.createSQLQuery(qDeleteServicesIp);
                query.setParameter("ip", qComputer.getIp());
                query.executeUpdate();
                
                //удаляем данный ip из таблицы ip_to_unit
                query = ses.createSQLQuery(qDeleteIp);
                query.setParameter("ip", qComputer.getIp());
                query.executeUpdate();
                
                ses.flush();
            }
            catch (Exception ex) {
                throw new ServerException("\n" + ex.toString() + "\n" + Arrays.toString(ex.getStackTrace()));
            }
        });
        
        Spring.getInstance().getTxManager().commit(status);
    }
}