/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.webservice;

import java.util.Date;
import javax.jws.WebService;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.QServer;
import ru.apertum.qsystem.server.controller.Executer;
import static ru.apertum.qsystem.server.controller.Executer.CLIENT_TASK_LOCK;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.postponed.QMovedToBankList;

/**
 *
 * @author zaikov
 */
@WebService(endpointInterface = "ru.apertum.qsystem.server.webservice.QueueIntegration")
public class QueueIntegrationImpl implements QueueIntegration {

    /**
     * Принимаем клиента от АПБ
     * @param unitId Идентификатор зала
     * @param requestId Идентификатор заявки (номер кастомера в нашей системе)
     * @param ticketId Номер талона
     * @param priorityId Приоритет кастомера
     * @param redirection Признак необходимости редиректа обратно
     * @param pointIdFrom Номер окна из которого пришёл кастомер
     * @param pointIdTo Номер окна в которое требуется отправить кастомера
     * @param additionInfo Доп. параметры
     * @return Признак удачности выполнения функции
     */
    @Override
    public String appendRequest(int unitId,
                                String requestId,
                                String ticketId,
                                int priorityId,
                                int redirection,
                                int pointIdFrom,
                                int pointIdTo,
                                String additionInfo) {
        
        String logInfo = "\n unitId=" + unitId +
                         "\n requestId=" + requestId +
                         "\n ticketId=" + ticketId + 
                         "\n priorityId=" + priorityId + 
                         "\n redirection=" + redirection + 
                         "\n pointIdFrom=" + pointIdFrom + 
                         "\n pointIdTo=" + pointIdTo + 
                         "\n additionInfo=" + additionInfo;
        
        QLog.l().logger().info("Принимаем клиента от АПБ");
        QLog.l().logger().info(logInfo);
        
        boolean initFromApb = false;
        
        //выполняем проверки и получаем пользователя из списка ушедших на оплату
        QCustomer customer = getCustomerByRequestId(requestId);
        
        //если кустомера не нашли в списке ушедших на оплату, значит нужно
        //создать нового и поставить в очередь на дефолтную услугу
        if (customer == null) {
            QLog.l().logger().warn("Не найден кастомер в списке ушедших на оплату: requestId=" + requestId +
                                   ". Значит клиент инициирован Агропромбанком. Нужно создать нового клиента и поставить в очередь.");
            
            customer = initCustomerFromBank(unitId, requestId, ticketId, priorityId, pointIdFrom);
            initFromApb = true;
        }
        
        //не получилось создать нового кустомера и поставить его в услугу
        //в теории такого не должно быть
        if (customer == null) {
            QLog.l().logger().error("Не удалось поставить клиента в очередь на дефолтную услугу.");
            throw new ServerException("Не удалось поставить клиента в очередь на дефолтную услугу.");
        }
        
        //если нам вернули кастомера с redirection=1, то пишем комментарий, что его нужно вернуть снова в банк
        if (redirection == 1) {
            customer.setPointIdTo(pointIdFrom);
            customer.setTempComments("Автоматический текст: оператор банка запросил вернуть клиента обратно после обслуживания.");
        }
        
        //если приоритет меньше высокого, то увеличиваем его (до VIP не увеличиваем)
        if (customer.getPriority().get() < Uses.PRIORITY_HI) {
            customer.setPriority(customer.getPriority().get() + 1);
        }
        
        //добавим нового пользователя в очередь
        final QService service = QServiceTree.getInstance().getById(customer.getService().getId());
        service.addCustomer(customer);
        
        //удалим из списка ушедших на оплату
        removeCustomerFromList(customer);
        
        //если кустомер вернулся после оплаты
        //состояние - "жду после оплаты"
        if (!initFromApb) {
            //вроде как только что встал в очередь, ну и время проставим, а то ожидание будет огромное
            //только что встал типо; просто время нахождения в отложенных не считается как ожидание очереди, иначе в statistic ожидание огромное
            customer.setStandTime(new Date());
            customer.setState(CustomerState.STATE_WAIT_AFTER_PAYMENT);
        }
        //если кустомер был инициирован АПБ и создан только что
        else {
            customer.setState(CustomerState.STATE_WAIT);
        }
        
        try {
            // сохраняем состояния очередей.
            QServer.savePool();
        } catch (Exception ex) {
            QLog.l().logger().error("Ошибка сохранения состояния очередей", ex);
        }
        
        return customer.getId().toString();
    }
    
    private QCustomer initCustomerFromBank(int unitId,
                                         String externalId,
                                         String ticketId,
                                         int externalPriority,
                                         int pointIdFrom) {
        
        final QService service = QServiceTree.getInstance().getById(QService.SERVICE_CONSULTATION_CDMA);
        final QCustomer customer;
        int priority = externalPriorityToInternal(externalPriority);
        // синхронизируем работу с клиентом
        CLIENT_TASK_LOCK.lock();
        try {
            // Создадим вновь испеченного кастомера
            customer = new QCustomer(Integer.valueOf(ticketId));
            customer.setUnitId(unitId);
            customer.setPriority(priority);
            customer.setPointIdTo(pointIdFrom);
            try {
                customer.setExtId(Long.valueOf(externalId));
            } catch (NumberFormatException nfe) {
                customer.setExtId(null);
                QLog.l().logger().warn("Ошибка парсинга идентификатора из системы АПБ, apbCustomerId=\"" + externalId + "\"", nfe);
            }
            customer.setService(service);

//            // Определим кастомера в очередь
//            customer.setService(service);
//            if (service.getLink() != null) {
//                customer.setService(service.getLink());
//            }
//
//            //добавим нового пользователя
//            (service.getLink() != null ? service.getLink() : service).addCustomer(customer);
//            // Состояние у него "Стою, жду".
//            customer.setState(CustomerState.STATE_WAIT);
        } finally {
            CLIENT_TASK_LOCK.unlock();
        }
        QLog.l().logger().trace("С приоритетом " + priority + " К услуге \"" + QService.SERVICE_CONSULTATION_CDMA +
                                "\" -> " + service.getPrefix() + '\'' + service.getName() + '\'');
        
        return customer;
    }
    
    /**
     * Отражает значение приоритета для внешнего взаимодействия на внутреннюю систему приоритетов.
     * @param externalPriority Значение приоритета для внешнего взаимодействия.
     * @return Вернёт значение, которое является отражением приоритета для внешнего взаимодействия на систему приоритетов внутри системы.
     */
    private int externalPriorityToInternal(int externalPriority) {
        switch(externalPriority) {
            case QueueIntegration.PRIORITY_NORMAL:
            case QueueIntegration.PRIORITY_NORMAL_AGAIN:
                return Uses.PRIORITY_NORMAL;
            case QueueIntegration.PRIORITY_INCREASED:
            case QueueIntegration.PRIORITY_INCREASED_AGAIN:
                return Uses.PRIORITY_HI;
            case QueueIntegration.PRIORITY_VIP:
            case QueueIntegration.PRIORITY_VIP_AGAIN:
            case QueueIntegration.PRIORITY_HIGHEST:
                return Uses.PRIORITY_VIP;
            default:
                return Uses.PRIORITY_NORMAL;
        }
    }
    
    private QCustomer getCustomerByRequestId(String requestId) {
        //не должно быть null или пустой строкой
        if (requestId == null || requestId.equals("")) {
            QLog.l().logger().error("Ошибка приёма кастомера из АПБ; requestId=\"" + requestId + "\"");
            return null;
        }
        
        QCustomer customer = null;
        
        try {
            //получаем по requestId кастомера из списка ушедших на оплату
            customer = QMovedToBankList.getInstance().getById(Long.valueOf(requestId.trim()));
        } catch (NumberFormatException e) {
            QLog.l().logger().error("Ошибка парсинга requestId=\"" + requestId + "\"", e);
        }
        
        return customer;
    }
    
    private void removeCustomerFromList(QCustomer customer) {
        Executer.MOVED_TO_BANK_TASK_LOCK.lock();
        try {
            QMovedToBankList.getInstance().removeElement(customer);
        } finally {
            Executer.MOVED_TO_BANK_TASK_LOCK.unlock();
        }
    }
    
}