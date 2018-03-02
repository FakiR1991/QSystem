/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model;

import javax.jws.WebMethod;
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
    
    @WebMethod String appendRequest(int    unitId,      // ID зала обслуживания
                                    String requestId,   // ID заявки в вашем/нашем зале
                                    int    ticketId,    // Номер талона в нашем/вашем зале
                                    int    priorityId,  // ID приоритета обслуживания
                                    int    redirection, // Признак перенаправления (возврата заявки в наш/ваш зал)
                                                        // 0 – нет
                                                        // 1 – да
                                    int    pointIdFrom, // ID кассы/точки обслуживания у нас/вас, откуда пришла заявка
                                    int    pointIdTo,   // ID кассы/точки обслуживания у вас/нас, куда направляем заявку,
                                                        // если 0, то обслужить может любой оператор
                                    String additioninfo // XML для доп. параметров, зарезервировано для будущего использования
    );
}
