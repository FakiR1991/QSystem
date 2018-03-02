/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model;

import java.util.Date;
import javax.jws.WebService;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.controller.Executer;
import ru.apertum.qsystem.server.model.postponed.QMovedToBankList;

/**
 *
 * @author zaikov
 */
@WebService(endpointInterface = "ru.apertum.qsystem.server.model.QueueIntegration")
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
     * @param additioninfo Доп. параметры
     * @return Признак удачности выполнения функции
     */
    @Override
    public String appendRequest(int unitId,
                                String requestId,
                                int ticketId,
                                int priorityId,
                                int redirection,
                                int pointIdFrom,
                                int pointIdTo,
                                String additioninfo) {
        String logInfo = "\n unitId=" + unitId +
                         "\n requestId=" + requestId +
                         "\n ticketId=" + ticketId + 
                         "\n priorityId=" + priorityId + 
                         "\n redirection=" + redirection + 
                         "\n pointIdFrom=" + pointIdFrom + 
                         "\n pointIdTo=" + pointIdTo + 
                         "\n additioninfo=" + additioninfo;
        
        QLog.l().logger().info("Принимаем клиента от АПБ");
        QLog.l().logger().info(logInfo);
        
        //выполняем проверки и получаем пользователя из списка ушедших на оплату
        QCustomer customer = getCustomerByRequestId(requestId);
        
        if (customer == null) {
            QLog.l().logger().error("Не найден кастомер в списке ушедших на оплату: requestId=" + requestId == null ? "null" : requestId);
            return "0";
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
        
        //вроде как только что встал в очередь, ну и время проставим, а то ожидание будет огромное
        //только что встал типо; просто время нахождения в отложенных не считается как ожидание очереди, иначе в statistic ожидание огромное
        customer.setStandTime(new Date());
        //состояние - "жду после оплаты"
        customer.setState(CustomerState.STATE_WAIT_AFTER_PAYMENT);
        
        return "1";
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
        
        //кастомер не найден в списке ушедших на оплату
        //что то пошло не так
        if (customer == null) {
            QLog.l().logger().error("В списке ушедших на оплату не найден кастомер с requestId=\"" + requestId.trim() + "\"");
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