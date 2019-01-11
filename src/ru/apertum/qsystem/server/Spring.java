/*
 *  Copyright (C) 2011 egorov
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.server;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.apertum.qsystem.common.model.QEmailSendingSettings;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.model.QNotificationsInfo;

/**
 *
 * @author egorov
 */
public class Spring {

    private final BeanFactory factory;
    private final String driverClassName;
    private final String url;
    private final String username;
    private final String password;

    public String getDriverClassName() {
        return driverClassName;
    }

    public BeanFactory getFactory() {
        return factory;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Для транзакций через передачу на выполнениее класса с методом где реализована работа с БД
     *
     * @return
     */
    public TransactionTemplate getTt() {
        return new TransactionTemplate(getTxManager());
    }

    /**
     * Для транзакций обычным образом с открытием именованной транзакции
     *
     * @return
     */
    public HibernateTransactionManager getTxManager() {
        return (HibernateTransactionManager) factory.getBean("transactionManager");
    }

    private Spring() {
        try {
            factory = new ClassPathXmlApplicationContext("/ru/apertum/qsystem/spring/qsContext.xml");
        } catch (BeanCreationException ex) {
            throw new ServerException("Ошибка создания класса-бина контекста приложения: \"" + ex.getCause().getMessage() + "\"\n"
                    + "Бин с ошибкой \"" + ex.getBeanName() + "\""
                    + "Сообщение об ошибке: \"" + ex.getCause().getMessage() + "\"\n" + ex);
        } catch (BeansException ex) {
            throw new ServerException("Ошибка класса-бина контекста приложения: \"" + ex.getCause().getMessage() + "\"\n"
                    + "Сообщение об ошибке: \"" + ex.getCause().getMessage() + "\"\n" + ex);
        } catch (Exception ex) {
            throw new ServerException("Ошибка создания контекста приложения: " + ex);
        }
        //sessionFactory = factory.getBean("mySessionFactory", SessionFactoryImpl.class);
        //ht = new HibernateTemplate(sessionFactory);

        final ComboPooledDataSource bds = (ComboPooledDataSource) factory.getBean("c3p0DataSource");
        bds.setMaxPoolSize(50);
        bds.setMaxStatements(0);
        driverClassName = bds.getDriverClass();
        url = bds.getJdbcUrl();
        username = bds.getUser();
        password = bds.getPassword();
    }

    public static Spring getInstance() {
        return SpringHolder.INSTANCE;
    }

    private static class SpringHolder {

        private static final Spring INSTANCE = new Spring();
    }

    public Spring getHt() {
        return this;
    }

    public void saveAll(Collection list) {
        final Session ses = getTxManager().getSessionFactory().getCurrentSession();
        list.stream().forEach((object) -> {
            ses.save(object);
        });
        ses.flush();
    }
    
    public String executeSelectString(String q) {
        final Session ses = getTxManager().getSessionFactory().getCurrentSession();  
        Query query = ses.createQuery(q);
        //query.setDate("today", new Date());
        List list = query.list();
        String response = (String)list.get(0);
        ses.flush();
        return response;
    }
    
    public Date executeSelectDate(String q) {
        final Session ses = getTxManager().getSessionFactory().getCurrentSession();  
        Query query = ses.createQuery(q);
        //query.setDate("today", new Date());
        List list = query.list();
        Date response = (Date)list.get(0);
        ses.flush();
        return response;
    }
    
    public ArrayList<QNotificationsInfo> executeSelectNotificationsInfo(String q) {
        ArrayList<QNotificationsInfo> ni = new ArrayList<QNotificationsInfo>();
        
        final Session ses = getTxManager().getSessionFactory().getCurrentSession();
        Iterator iterator = ses.createQuery(q)
                               .list()
                               .iterator();
        
        while (iterator.hasNext()) {
            Object[] item = (Object[])iterator.next();
            
            long id = (long)item[QNotificationsInfo.COLUMN_ID];
            String fio = (String)item[QNotificationsInfo.COLUMN_FIO];
            String email = (String)item[QNotificationsInfo.COLUMN_EMAIL];
            String phone = (String)item[QNotificationsInfo.COLUMN_PHONE];
            int maxWaitingMinutesAbo = (int)item[QNotificationsInfo.COLUMN_MAX_WAITING_MINUTES_ABO];
            int maxWaitingMinutesSc = (int)item[QNotificationsInfo.COLUMN_MAX_WAITING_MINUTES_SC];
            double ratioAbo = (double)item[QNotificationsInfo.COLUMN_RATIO_ABO];
            double ratioSc = (double)item[QNotificationsInfo.COLUMN_RATIO_SC];
            int unitId = (int)item[QNotificationsInfo.COLUMN_UNIT_ID];
            
            ni.add(new QNotificationsInfo(id,
                                          fio,
                                          email,
                                          phone,
                                          maxWaitingMinutesAbo,
                                          maxWaitingMinutesSc,
                                          ratioAbo,
                                          ratioSc,
                                          unitId));
        }
        ses.flush();
        
        return ni.size() <= 0 ? null : ni;
    }
    
    public QEmailSendingSettings executeSelectEmailSendingSettings(String q) {
        QEmailSendingSettings sendingSettings = new QEmailSendingSettings();
        
        final Session ses = getTxManager().getSessionFactory().getCurrentSession();
        Iterator iterator = ses.createQuery(q)
                               .list()
                               .iterator();
        
        while (iterator.hasNext()) {
            Object[] item = (Object[])iterator.next();
            
            long id = (long)item[QEmailSendingSettings.COLUMN_ID];
            String smtpHost = (String)item[QEmailSendingSettings.COLUMN_SMTP_HOST];
            String smtpPort = (String)item[QEmailSendingSettings.COLUMN_SMTP_PORT];
            String email = (String)item[QEmailSendingSettings.COLUMN_EMAIL];
            String pass = (String)item[QEmailSendingSettings.COLUMN_PASSWORD];
            
            sendingSettings.setId(id);
            sendingSettings.setSmtpHost(smtpHost);
            sendingSettings.setSmtpPort(smtpPort);
            sendingSettings.setEmail(email);
            sendingSettings.setPassword(pass);
        }
        ses.flush();
        
        return sendingSettings;
    }
    
    public Long executeSelectQuery(String q, Object obj, String q2) {
        final Session ses = getTxManager().getSessionFactory().getCurrentSession();  
        Query  query = ses.createQuery(q);
        query.setDate("today", new Date());
        Long id = (Long) query.list().get(0);
     //   System.out.println(query.list());
      //  String replace = q2.replace("*id*", query.list().get(0).toString());
      //  System.out.println(replace);
      //  query = ses.createQuery(replace);
        
        
       /* List<Object[]> l = query.list();
        for(Object[] result: l) {
            System.out.println(result[0]);
        }*/
        ses.flush();
        
        return id;
       // obj.stream().forEach((object) -> {
       //  Query  query = ses.createQuery(q);
        // System.out.println(query.list());
      // });
       // ses.flush();
       // return query.list();
        
    }
    
    public void executeUpdateQuery(String q) {
        final Session ses = getTxManager().getSessionFactory().getCurrentSession();  
        Query query = ses.createQuery(q);
        int result = query.executeUpdate();
        ses.flush();
    }

    public void saveOrUpdateAll(Collection list) {
        final Session ses = getTxManager().getSessionFactory().getCurrentSession();
        list.stream().forEach((object) -> {
            ses.saveOrUpdate(object);
        });
        ses.flush();
    }

    public void saveOrUpdate(Object obj) {
        final Session ses = getTxManager().getSessionFactory().getCurrentSession();
        ses.saveOrUpdate(obj);
        ses.flush();
    }
    
    public void update(Object obj) {
        final Session ses = getTxManager().getSessionFactory().getCurrentSession();
        ses.update(obj);
        ses.flush();
    }

    public void deleteAll(Collection list) {
        final Session ses = getTxManager().getSessionFactory().getCurrentSession();
        list.stream().forEach((object) -> {
            ses.delete(object);
        });
        ses.flush();
    }

    public void delete(Object obj) {
        final Session ses = getTxManager().getSessionFactory().getCurrentSession();
        ses.delete(obj);
        ses.flush();
    }

    public List loadAll(Class clazz) {
        final Session ses = getTxManager().getSessionFactory().openSession();
        try {
            return ses.createCriteria(clazz).list();
        } finally {
            ses.close();
        }
    }

    public void load(Object obj, Serializable srlzbl) {
        final Session ses = getTxManager().getSessionFactory().openSession();
        try {
            ses.load(obj, srlzbl);
        } finally {
            ses.close();
        }
    }

    public List findByCriteria(DetachedCriteria dCriteria) {
        List list;
        final Session ses = getTxManager().getSessionFactory().openSession();
        try {
            list = dCriteria.getExecutableCriteria(ses).list();
        } finally {
            ses.close();
        }
        return list;
    }

    public <T> T get(Class<T> clazz, Serializable srlzbl) {
        final Session ses = getTxManager().getSessionFactory().openSession();
        try {
            return (T) ses.get(clazz, srlzbl);
        } finally {
            ses.close();
        }
    }

    public List find(String hql) {
        final Session ses = getTxManager().getSessionFactory().openSession();
        try {
            return ses.createQuery(hql).list();
        } finally {
            ses.close();
        }
    }

    public SessionFactory getSessionFactory() {
        return getTxManager().getSessionFactory();
    }
}
