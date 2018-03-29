/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.webservice;

import java.math.BigDecimal;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author zaikov
 */
@WebService
public interface QueueIntegration
{
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_STANDARD = 2;
    public static final int PRIORITY_NORMAL_AGAIN = 3;
    public static final int PRIORITY_STANDARD_AGAIN = 4;
    public static final int PRIORITY_INCREASED = 5;
    public static final int PRIORITY_INCREASED_AGAIN = 6;
    public static final int PRIORITY_VIP = 7;
    public static final int PRIORITY_VIP_AGAIN = 8;
    public static final int PRIORITY_HIGHEST = 9;
    
    /*
        Немного о перенаправлении клиентов в АПБ и обратно:
        1.	Redirection = 0 + pointIdFrom = 0 или неважно какое значение - перенаправление без возврата
        2.	Redirection = 1 + pointIdFrom = 0 – перенаправление без возврата
        3.	Redirection = 1 + pointIdFrom = 99 (или 9999, чтобы наверняка не попасть на реальный ид точки) – перенаправление с возвратом любому ближайшему доступному оператору
        4.	Redirection = 1 + pointIdFrom = ID вашей точки – перенаправление с возвратом  к конкретному вашему оператору (ID вашей точки)
    */
    
    @WebMethod String appendRequest(@WebParam(name = "unitId", mode = WebParam.Mode.IN)
                                    int unitId,         // ID зала обслуживания
                                    @WebParam(name = "requestId", mode = WebParam.Mode.IN)
                                    String requestId,   // ID заявки в вашем/нашем зале
                                    @WebParam(name = "ticketId", mode = WebParam.Mode.IN)
                                    int ticketId,       // Номер талона в нашем/вашем зале
                                    @WebParam(name = "priorityId", mode = WebParam.Mode.IN)
                                    int priorityId,     // ID приоритета обслуживания
                                    @WebParam(name = "redirection", mode = WebParam.Mode.IN)
                                    int redirection,    // Признак перенаправления (возврата заявки в наш/ваш зал)
                                                        // 0 – нет
                                                        // 1 – да
                                    @WebParam(name = "pointIdFrom", mode = WebParam.Mode.IN)
                                    int pointIdFrom,    // ID кассы/точки обслуживания у нас/вас, откуда пришла заявка
                                                        // если 0, то обслужить может любой оператор
                                    @WebParam(name = "pointIdTo", mode = WebParam.Mode.IN)
                                    int pointIdTo,      // ID кассы/точки обслуживания у вас/нас, куда направляем заявку,
                                    @WebParam(name = "additionInfo", mode = WebParam.Mode.IN)
                                    String additionInfo // XML для доп. параметров, зарезервировано для будущего использования
    );
}