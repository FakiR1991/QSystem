/*
 *  Copyright (C) 2010 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
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

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Scanner;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.swing.Timer;
import javax.xml.ws.Endpoint;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.apertum.qsystem.About;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.client.forms.FAbout;
import ru.apertum.qsystem.common.CodepagePrintStream;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.EmailSender;
import ru.apertum.qsystem.common.GsonPool;
import ru.apertum.qsystem.common.Mailer;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.TempTicket;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.cmd.JsonRPC20;
import ru.apertum.qsystem.common.cmd.RpcGetAdvanceCustomer;
import ru.apertum.qsystem.common.exceptions.ClientException;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.ATalkingClock;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.IStartServer;
import ru.apertum.qsystem.hibernate.AnnotationSessionFactoryBean;
import ru.apertum.qsystem.reports.model.QReportsList;
import ru.apertum.qsystem.reports.model.WebServer;
import ru.apertum.qsystem.server.controller.Executer;
import static ru.apertum.qsystem.server.controller.Executer.CLIENT_TASK_LOCK;
import ru.apertum.qsystem.server.http.JettyRunner;
import ru.apertum.qsystem.server.model.QNotificationsInfo;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;
import ru.apertum.qsystem.server.model.postponed.QMovedToBankList;
import ru.apertum.qsystem.server.model.postponed.QPostponedList;
import ru.apertum.qsystem.server.webservice.QueueIntegrationImpl;
import ru.apertum.qsystem.server.webservice.prereg.Filial;
import ru.apertum.qsystem.server.webservice.prereg.ReserveInterval;
import ru.apertum.qsystem.server.webservice.prereg.notgenerated.WSCallback;
import ru.apertum.qsystem.server.webservice.prereg.notgenerated.WS_ServiceImpl;

/**
 * Класс старта и exit инициализации сервера. Организация потоков выполнения заданий.
 *
 * @author Evgeniy Egorov
 */
public class QServer extends Thread {

    private final Socket socket;
    private static volatile boolean globalExit = false;
    
    //если для текущего unitId стоит таймаут, то значение date не может быть null
    private static HashMap<Integer, Date> workloadTimeoutHM = new HashMap<Integer, Date>();
    
    //таймер, который раз в минуту будет проверять загруженность операторов
    //и состояние очередей, чтобы в случае высокой нагрузки отправить email
    //с уведомлением о сложившейся ситуации
    private static Timer workloadTimer = new Timer(60 * 1000, (ActionEvent e) -> {
        try {
//            checkWorkload();
        } catch (Exception ex) {
            QLog.l().logger().trace("WORKLOAD TIMER EXCEPTION:", ex);
        }
    });
    
    private final static int HOUR_IN_MILLIS = 60 * 60 * 1000;
    
    /**
     * Таймер по которому будем выгонять временных отложенных
     */
    private static Timer cleanUpMovedToBankTimer = new Timer(60 * 1000, (ActionEvent e) -> {
        //каждый час проверяем надо ли почистить список людей отправленных на оплату
        Executer.MOVED_TO_BANK_TASK_LOCK.lock();
        try {
            final ArrayList<QCustomer> forDel = new ArrayList<>();
            for (QCustomer customer : QMovedToBankList.getInstance().getMovedToBankCustomers()) {
                //если прошло больше трёх часов с тех пор как его отправили оплачивать, то удаляем
                if ( (System.currentTimeMillis() - customer.getStandTime().getTime()) > 3 * HOUR_IN_MILLIS ) {

                    QLog.l().logger().debug("Удаляем по таймеру из списка ушедших на оплату кастомера №" + customer.getPrefix() + customer.getNumber());

                    //добавляем кастомера в список на удаление
                    forDel.add(customer);
                    //добавляем запись в таблицу clients
                    //состояние не сохранится в БД, потому что user у текущего кастомера равен null
                    //т.к. мы завершили работу с ним [сделали setUser(null)] и проставили перед этим stateIn = 10
//                    customer.setState(CustomerState.STATE_DEAD_AFTER_PAYMENT);

                }
            }
            forDel.stream().forEach((qCustomer) -> {
                QMovedToBankList.getInstance().removeElement(qCustomer);
            });

            QServer.savePool();

        } catch (Exception ex) {
//                    throw new ServerException("Ошибка при удалении кастомера из списка ушедших на оплату по таймеру " + ex.getMessage());
            QLog.l().logger().trace("Ошибка при удалении кастомера из списка ушедших на оплату по таймеру", ex);
        } finally {
            Executer.MOVED_TO_BANK_TASK_LOCK.unlock();
        }
    });
    
    /**
     * Таймер который проверяет талоны и назначает их операторам.
     * Ещё назначение происходит во время постановки в очередь клиента.
     */
    private static Timer setCustomerToUserTimer = new Timer(4 * 1000, (ActionEvent e) -> {
        Executer.CLIENT_TASK_LOCK.lock();
        
        try {
            //по каждой услуге идём
            for (QService service : QServiceTree.getInstance().getNodes()) {
                if (service.getStatus().compareTo(1) != 0) {
                    continue;
                }

                //проходимся по каждому ключу (unitId)
                for (Map.Entry<Integer, LinkedBlockingDeque<QCustomer>> set : service.getClients().entrySet()) {
                    //очередной unitId смотрим
                    Integer uid = set.getKey();
                    
                    //клонируем очередь по данной услуге и по данному unitId
                    PriorityQueue<QCustomer> tmpQueue = new PriorityQueue<>(service.getClients(uid));

                    //не боимся делать poll, так как это клонированная очередь,
                    //но нам нужен первый по приоритету клиент
                    while (tmpQueue.size() > 0) {
                        //прокручиваем полностью каждого кастомера и сравниваем друг с другом
                        QCustomer customer = peekCustomer(tmpQueue);
                        
                        if (customer != null) {
                            //если у кастомера ещё нет хозяина
                            if (customer.getIsMine() == null) {
                                //выбираем кастомеру хозяина
                                setCustomerToFreeUser(customer);
                            }
                            
                            //удаляем из клонированной очереди кастомера не зависимо
                            //от того нашли ему хозяина или у него уже есть хозяин
                            tmpQueue.remove(customer);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            QLog.l().logger().debug("EXCEPTION TIMER!", ex);
        } finally {
            Executer.CLIENT_TASK_LOCK.unlock();
        }
    });
    
    private static QCustomer peekCustomer(PriorityQueue<QCustomer> queue) {
        //ещё раз клонируем очередь
        PriorityQueue<QCustomer> tmpQueue = new PriorityQueue<>(queue);
        QCustomer customer = null;
        
        //не боимся делать poll, так как это клонированная очередь,
        //но нам нужен первый по приоритету клиент
        while (tmpQueue.size() > 0) {
            QCustomer cust = tmpQueue.poll();
            
            if (cust == null) {
                continue; 
            }
            
            //сравниваем приоритеты кастомера текущего цикла и сохранённого
            if (customer == null || customer.compareTo(cust) == 1) {
                customer = cust;
            }
        }
        
        return customer;
    }
    
//    private static Timer setCustomerToUserTimer = new Timer(5 * 1000, (ActionEvent e) -> {
//        Executer.MOVED_TO_BANK_TASK_LOCK.lock();
//        try {
//            //по каждой услуге идём
//            for (QService service : QServiceTree.getInstance().getNodes()) {
//                if (service.getStatus().compareTo(1) != 0) {
//                    continue;
//                }
//
//                //в каждой услуге проверяем очередь по каждому отделению
//                //если есть клиенты которые не назначены операторам, то
//                //пробуем найти для них свободного оператора
//                service.getClients().forEach((uid, customers) -> {
//                    customers.forEach(customer -> {
//                        if (customer.getIsMine() == null) {
//                            setCustomerToFreeUser(customer);
//                        }
//                    });
//                });
//            }
//        } catch (Exception ex) {
//            QLog.l().logger().debug("EXCEPTION TIMER!", ex);
//        } finally {
//            Executer.MOVED_TO_BANK_TASK_LOCK.unlock();
//        }
//    });
        
    //фиксируем если назначили кастомера оператору
    //на каждого оператора не более одного кастомера
    //key - userId, value - customerId
    public static HashMap<Long, Long> usersToCustomers = new HashMap<>();
            
    private static boolean checkCustomerForUserInQueue(QUser user) {
        for (QService service : QServiceTree.getInstance().getNodes()) {
            if (service.getStatus().compareTo(1) != 0) {
                continue;
            }
            
            LinkedBlockingDeque<QCustomer> customers = service.getClients(user.getUnitId());
            
            for (QCustomer customer : customers) {
                if (Objects.equals(customer.getIsMine(), user.getId())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static void setCustomerToFreeUser(QCustomer customer) {
        QUser selectedUser = null;
        int selectedUsetCoefficient = 0;
        int maxInactiveTimeSeconds = 0;
        int inactiveTimeSeconds = 0;
        Long serviceId = customer.getService().getId();
        
        for (QUser user : QUserList.getInstance().getItems()) {
            boolean isServiceInPlan = user.isServiceInPlan(serviceId);
            boolean isIncorrectUnitId = !Objects.equals(user.getUnitId(), customer.getUnitId());
            boolean isEmptyShadow = user.getShadow() == null;
            boolean isPause = user.isPause();
            boolean isTechWork = user.isTechWork();
            boolean isContinueServicing = user.isContinueServicing();
            boolean isUserHasCustomer = usersToCustomers.get(user.getId()) != null || user.getCustomer() != null || checkCustomerForUserInQueue(user);
            //задержка после завершения обслуживания клиента 10 секунд
            boolean isUserDelayOver = user.getCustFinishTime() == null || ((new Date().getTime() - user.getCustFinishTime().getTime()) / 1000) >= 10;
            boolean isUserBusy = (user.getShadow() != null && user.getShadow().getStartTime() != null) || isPause || isTechWork || isUserHasCustomer || isContinueServicing;
            
            if (isIncorrectUnitId || isEmptyShadow || isUserBusy || !isServiceInPlan || !isUserDelayOver) {
                continue;
            }
            
            Date lastActivityDate = user.getLastPauseDate() != null
                                        ? user.getLastPauseDate().after(user.getShadow().getFinTime())
                                              ? user.getLastPauseDate()
                                              : user.getShadow().getFinTime()
                                        : user.getShadow().getFinTime();
            
            //вычисляем для каждого юзера время бездействия
            inactiveTimeSeconds = Math.round((new Date().getTime() - lastActivityDate.getTime()) / 1000);
            
            //смотрим коэфициент участия кастомера на данную услугу
            int userCoefficient = user.getPlanService(customer.getService().getId()).getCoefficient();
            
            //если приоритет данного оператора больше или время ожидания кастомера больше
            if (userCoefficient > selectedUsetCoefficient || inactiveTimeSeconds > maxInactiveTimeSeconds) {
                maxInactiveTimeSeconds = inactiveTimeSeconds;
                selectedUser = user;
                selectedUsetCoefficient = userCoefficient;
            }
        }
        
        if (selectedUser != null) {
            //назначаем кастомеру оператора
            customer.setIsMine(selectedUser.getId());
            //запоминает то, что оператору уже назначен кастомер
            usersToCustomers.put(selectedUser.getId(), customer.getId());
        }
    }
    
    /**
     * @param args - первым параметром передается полное имя настроечного XML-файла
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        workloadTimer.start();
        setCustomerToUserTimer.start();
        cleanUpMovedToBankTimer.start();
        
        publishWebService();
        
        About.printdef();
        QLog.initial(args, 0);
        Locale.setDefault(Locales.getInstance().getLangCurrent());
        
        //Установка вывода консольных сообщений в нужной кодировке
        if ("\\".equals(File.separator)) {
            try {
                String consoleEnc = System.getProperty("console.encoding", "Cp866");
                System.setOut(new CodepagePrintStream(System.out, consoleEnc));
                System.setErr(new CodepagePrintStream(System.err, consoleEnc));
            } catch (UnsupportedEncodingException e) {
                System.out.println("Unable to setup console codepage: " + e);
            }
        }

        System.out.println("Welcome to the QSystem server. Your MySQL mast be prepared.");
        FAbout.loadVersionSt();

        if (Locales.getInstance().isRuss()) {

            if ("0".equals(FAbout.CMRC_)) {
                System.out.println("Добро пожаловать на сервер QSystem. Для работы необходим MySQL5.5 или выше.");
                System.out.println("Версия сервера: " + FAbout.VERSION_ + "-community QSystem Server (GPL)");
                System.out.println("Версия базы данных: " + FAbout.VERSION_DB_ + " for MySQL 5.5-community Server (GPL)");
                System.out.println("Дата выпуска : " + FAbout.DATE_);
                System.out.println("Copyright (c) 2016, Apertum Projects. Все права защищены.");
                System.out.println("QSystem является свободным программным обеспечением, вы можете");
                System.out.println("распространять и/или изменять его согласно условиям Стандартной Общественной");
                System.out.println("Лицензии GNU (GNU GPL), опубликованной Фондом свободного программного");
                System.out.println("обеспечения (FSF), либо Лицензии версии 3, либо более поздней версии.");

                System.out.println("Вы должны были получить копию Стандартной Общественной Лицензии GNU вместе");
                System.out.println("с этой программой. Если это не так, напишите в Фонд Свободного ПО ");
                System.out.println("(Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA)");
            }

            System.out.println("Наберите 'exit' чтобы штатно остановить работу сервера.");
            System.out.println();
        } else {
            if ("0".equals(FAbout.CMRC_)) {
                System.out.println("Server version: " + FAbout.VERSION_ + "-community QSystem Server (GPL)");
                System.out.println("Database version: " + FAbout.VERSION_DB_ + " for MySQL 5.5-community Server (GPL)");
                System.out.println("Released : " + FAbout.DATE_);

                System.out.println("Copyright (c) 2010-2016, Apertum Projects and/or its affiliates. All rights reserved.");
                System.out.println("This software comes with ABSOLUTELY NO WARRANTY. This is free software,");
                System.out.println("and you are welcome to modify and redistribute it under the GPL v3 license");
                System.out.println("Text of this license on your language located in the folder with the program.");
            }

            System.out.println("Type 'exit' to stop work and close server.");
            System.out.println();
        }

        final long start = System.currentTimeMillis();

        // Загрузка плагинов из папки plugins
        if (!QConfig.cfg().isNoPlugins()) {
            Uses.loadPlugins("./plugins/");
        }

        // посмотрим не нужно ли стартануть jetty
        // для этого нужно запускать с ключом http
        // если етсь ключ http, то запускаем сервер и принимаем на нем команды серверу суо
        if (QConfig.cfg().getHttp() > 0) {
            QLog.l().logger().info("Run Jetty.");
            try {
                JettyRunner.start(QConfig.cfg().getHttp());
            } catch (NumberFormatException ex) {
                QLog.l().logger().error("Номер порта для Jetty в параметрах запуска не является числом. Формат параметра для порта 8081 '-http 8081'.", ex);
            }
        }

        // Отчетный сервер, выступающий в роли вэбсервера, обрабатывающего запросы на выдачу отчетов
        WebServer.getInstance().startWebServer(ServerProps.getInstance().getProps().getWebServerPort());
        loadPool();
        // запускаем движок индикации сообщения для кастомеров
        MainBoard.getInstance().showBoard();
        startPostponedTimer();
        startCleanUpTicketsTimer();
        startReservedTicketsTimer();
        // test ServerProps.getInstance().getProps().getZoneBoardServAddrList();
        if (!(Uses.FORMAT_HH_MM.format(ServerProps.getInstance().getProps().getStartTime()).equals(Uses.FORMAT_HH_MM.format(ServerProps.getInstance().getProps().getFinishTime())))) {
            /**
             * Таймер, по которому будем Очистка всех услуг и рассылка спама с дневным отчетом.
             */
            ATalkingClock clearServices = new ATalkingClock(Uses.DELAY_CHECK_TO_LOCK, 0) {

                @Override
                public void run() {
                    // это обнуление
                    if (!QConfig.cfg().isRetain() && Uses.FORMAT_HH_MM.format(new Date(new Date().getTime() + 10 * 60 * 1000)).equals(Uses.FORMAT_HH_MM.format(ServerProps.getInstance().getProps().getStartTime()))) {
                        QLog.l().logger().info("Очистка всех услуг.");
                        // почистим все услуги от трупов кастомеров с прошлого дня
                        QServer.clearAllQueue();
                        usersToCustomers = new HashMap<>();
                    }

                    // это рассылка дневного отчета
                    if (("true".equalsIgnoreCase(Mailer.fetchConfig().getProperty("mailing")) || "1".equals(Mailer.fetchConfig().getProperty("mailing")))
                            && Uses.FORMAT_HH_MM.format(new Date(new Date().getTime() - 30 * 60 * 1000)).equals(Uses.FORMAT_HH_MM.format(ServerProps.getInstance().getProps().getFinishTime()))) {
                        QLog.l().logger().info("Рассылка дневного отчета.");
                        // почистим все услуги от трупов кастомеров с прошлого дня
                        for (QUser user : QUserList.getInstance().getItems()) {
                            if (user.getReportAccess()) {
                                final HashMap<String, String> p = new HashMap<>();
                                p.put("date", Uses.FORMAT_DD_MM_YYYY.format(new Date()));
                                final byte[] result = QReportsList.getInstance().generate(user, "/distribution_job_day.pdf", p);
                                try {
                                    try (FileOutputStream fos = new FileOutputStream("temp/distribution_job_day.pdf")) {
                                        fos.write(result);
                                        fos.flush();
                                    }
                                    Mailer.sendReporterMailAtFon(null, null, null, "temp/distribution_job_day.pdf");
                                } catch (Exception ex) {
                                    QLog.l().logger().error("Какой-то облом с дневным отчетом", ex);
                                }
                                break;
                            }
                        }
                    }
                }
            };
            clearServices.start();
        }

        // подключения плагинов, которые стартуют в самом начале.
        // поддержка расширяемости плагинами
        for (final IStartServer event : ServiceLoader.load(IStartServer.class)) {
            QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
            try {
                new Thread(() -> {
                    event.start();
                }).start();
            } catch (Throwable tr) {
                QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: " + tr);
            }
        }

        // привинтить сокет на локалхост, порт 3128
        final ServerSocket server;
        try {
            QLog.l().logger().info("Сервер системы захватывает порт \"" + ServerProps.getInstance().getProps().getServerPort() + "\".");
            server = new ServerSocket(ServerProps.getInstance().getProps().getServerPort());
        } catch (IOException e) {
            throw new ServerException("Network error. Creating net socket is not possible: " + e);
        } catch (Exception e) {
            throw new ServerException("Network error: " + e);
        }
        server.setSoTimeout(500);
        final AnnotationSessionFactoryBean as = (AnnotationSessionFactoryBean) Spring.getInstance().getFactory().getBean("conf");
        System.out.println("Server QSystem started.\n");
        QLog.l().logger().info("Сервер системы 'Очередь' запущен. DB name='" + as.getName() + "' url=" + as.getUrl());
        
        if (!QConfig.cfg().isTestServer()) {
            sendMessage("QSystem - start", "Сервер успешно запущен");
        }
        
        int pos = 0;
        boolean exit = false;
        // слушаем порт
        while (!globalExit && !exit) {
            // ждём нового подключения, после чего запускаем обработку клиента
            // в новый вычислительный поток и увеличиваем счётчик на единичку

            try {
                final QServer qServer = new QServer(server.accept());
                qServer.start();
                if (QConfig.cfg().isDebug()) {
//                    System.out.println();
                }
            } catch (SocketTimeoutException e) {
                // ничего страшного, гасим исключение стобы дать возможность отработать входному/выходному потоку
            } catch (Exception e) {
                throw new ServerException("Network error: " + e);
            }

            if (!QConfig.cfg().isDebug()) {
                final char ch = '*';
                String progres = "Process: " + ch;
                final int len = 5;
                for (int i = 0; i < pos; i++) {
                    progres = progres + ch;
                }
                for (int i = 0; i < len; i++) {
                    progres = progres + ' ';
                }
                if (++pos == len) {
                    pos = 0;
                }
                System.out.print(progres);
                System.out.write(13);// '\b' - возвращает корретку на одну позицию назад

            }

            // Попробуем считать нажатую клавишу
            // если нажади ENTER, то завершаем работу сервера
            // и затираем файл временного состояния Uses.TEMP_STATE_FILE
            //BufferedReader r = new BufferedReader(new StreamReader(System.in));
            int bytesAvailable = System.in.available();
            if (bytesAvailable > 0) {
                byte[] data = new byte[bytesAvailable];
                System.in.read(data);
                if (bytesAvailable == 5
                        && data[0] == 101
                        && data[1] == 120
                        && data[2] == 105
                        && data[3] == 116
                        && ((data[4] == 10) || (data[4] == 13))) {
                    // набрали команду "exit" и нажали ENTER
                    QLog.l().logger().info("Завершение работы сервера.");
                    exit = true;
                }
            }
        }// while
        
        //останавливаем таймер проверки очередей
        workloadTimer.stop();
        setCustomerToUserTimer.stop();

        QLog.l().logger().debug("Закрываем серверный сокет.");
        server.close();
        QLog.l().logger().debug("Останов Jetty.");
        JettyRunner.stop();
        QLog.l().logger().debug("Останов отчетного вэбсервера.");
        WebServer.getInstance().stopWebServer();
        QLog.l().logger().debug("Выключение центрального табло.");
        MainBoard.getInstance().close();

        //при штатном закрытии сервера удалялся temp файл, который содержит состояние очередей
        //вызов метода deleteTempFile закомментирован, чтобы при перезапуске сервера состояние очередей не удалялось
        //deleteTempFile();
        Thread.sleep(1500);
        QLog.l().logger().info("Сервер штатно завершил работу. Время работы: " + Uses.roundAs(((double) (System.currentTimeMillis() - start)) / 1000 / 60, 2) + " мин.");
        
        if (!QConfig.cfg().isTestServer()) {
            sendMessage("QSystem - stop", "Сервер штатно завершил работу");
        }
        
        System.exit(0);
    }
    
    private static void sendMessage(String subject, String message) {
        String[] mails = Mailer.fetchConfig().getProperty("mail.smtp.start_stop").split(",");
        for (String mail : mails) {
            Mailer.sendReporterMailAtFon(
                    subject,
                    message,
                    mail,
                    null
            );
        }
    }
    
    private static String serviceAddress;
    
    /**
     * Создаём и публикуем свой веб-сервис для взаимодействия с очередью Агропромбанка.
     */
    private static void publishWebService() {
        loadWebServiceSettings();
        //АПБ 
        Endpoint.publish(serviceAddress, new QueueIntegrationImpl());
        System.out.println("APB published");
        //Предварительная регистрация для приложения Мой IDC
        Endpoint.publish("http://localhost:9902/prereg", new WS_ServiceImpl(new WSCallback() {
            @Override
            public ArrayList<Filial> getFilialList() {
                System.out.println("getFilialList");
                return null;
            }

            @Override
            public ArrayList<ReserveInterval> getFilialFreeTickets(int id, Date date) {
                System.out.println("getFilialFreeTickets");
                return null;
            }

            @Override
            public boolean setReserve(int ticketId, int reasonId, int ticketNumber, int myIdcAccId, int juridical, String name1, String name2, String name3, String title, String email, String phone, String comments) {
                System.out.println("setReserve");
                return false;
            }
        }));
        System.out.println("MyIDC published");
    }
    
    private static void loadWebServiceSettings() {
        QLog.l().logger().debug("\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u043c \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u044b \u0438\u0437 \u0444\u0430\u0439\u043b\u0430 \"config" + File.separator + "admin.property\"");
        final Properties settings = new Properties();
        final FileInputStream in;
        InputStreamReader inR = null;
        try {
            in = new FileInputStream("config" + File.separator + "admin.properties");
            inR = new InputStreamReader(in, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new ClientException("\u041f\u0440\u043e\u0431\u043b\u0435\u043c\u044b \u0441 \u043a\u043e\u0434\u0438\u0440\u043e\u0432\u043a\u043e\u0439 \u043f\u0440\u0438 \u0447\u0442\u0435\u043d\u0438\u0438. " + ex);
        } catch (FileNotFoundException ex) {
            throw new ClientException("\u041f\u0440\u043e\u0431\u043b\u0435\u043c\u044b \u0441 \u0444\u0430\u0439\u043b\u043e\u043c \u043f\u0440\u0438 \u0447\u0442\u0435\u043d\u0438\u0438. " + ex);
        }
        try {
            settings.load(inR);
        } catch (IOException ex) {
            throw new ClientException("\u041f\u0440\u043e\u0431\u043b\u0435\u043c\u044b \u0441 \u0447\u0442\u0435\u043d\u0438\u0435\u043c \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u043e\u0432. " + ex);
        }
        serviceAddress = settings.getProperty("service_address");
    }
    
    private static class WorkloadStatistics {
        //количество кастомеров в абон. зале
        private int customersCountAbo = 0;
        //максимальное время ожидания среди кастомеров в абон. зале (минуты)
        private int maxWaitingMinutesAbo = 0;
        //количество клиентов со статусом
        //"в работе" или "готов" в абон. зале
        private int clientsCountReadyToWorkAbo = 0;
        
        //количество кастомеров в СЦ
        private int customersCountSC = 0;
        //максимальное время ожидания среди кастомеров в СЦ (минуты)
        private int maxWaitingMinutesSC = 0;
        //количество клиентов со статусом
        //"в работе" или "готов" в СЦ
        private int clientsCountReadyToWorkSC = 0;
        
        /*
        * Абонентский зал
        */
        public int getCustomersCountAbo() {
            return this.customersCountAbo;
        }
        
        public void setCustomersCountAbo(int count) {
            this.customersCountAbo = count;
        }
        
        public void addCustomersCountAbo(int count) {
            this.customersCountAbo += count;
        }
        
        public int getMaxWaitingMinutesAbo() {
            return this.maxWaitingMinutesAbo;
        }
        
        public void setMaxWaitingMinutesAbo(int minutes) {
            this.maxWaitingMinutesAbo = minutes;
        }
        
        public int getClientsCountReadyToWorkAbo() {
            return this.clientsCountReadyToWorkAbo;
        }
        
        public void setClientsCountReadyToWorkAbo(int count) {
            this.clientsCountReadyToWorkAbo = count;
        }
        
        public void addClientsCountReadyToWorkAbo(int count) {
            this.clientsCountReadyToWorkAbo += count;
        }
        
        /**
         * @return Возвращает соотношение количества кастомеров в абон. зале к количеству операторов, готовых их обслужить.
         */
        public double getCustomersToClientsRatioForAbo() {
            if (clientsCountReadyToWorkAbo > 0) {
                return (double)customersCountAbo / (double)clientsCountReadyToWorkAbo;
            } else {
                return 0;
            }
        }
        
        /*
        * Сервисный центр
        */
        public int getCustomersCountSC() {
            return this.customersCountSC;
        }
        
        public void setCustomersCountSC(int count) {
            this.customersCountSC = count;
        }
        
        public void addCustomersCountSC(int count) {
            this.customersCountSC += count;
        }
        
        public int getMaxWaitingMinutesSC() {
            return this.maxWaitingMinutesSC;
        }
        
        public void setMaxWaitingMinutesSC(int minutes) {
            this.maxWaitingMinutesSC = minutes;
        }
        
        public int getClientsCountReadyToWorkSC() {
            return this.clientsCountReadyToWorkSC;
        }
        
        public void setClientsCountReadyToWorkSC(int count) {
            this.clientsCountReadyToWorkSC = count;
        }
        
        public void addClientsCountReadyToWorkSC(int count) {
            this.clientsCountReadyToWorkSC += count;
        }
        
        /**
         * @return Возвращает соотношение количества кастомеров в сервисном центре к количеству операторов, готовых их обслужить.
         */
        public double getCustomersToClientsRatioForSC() {
            if (clientsCountReadyToWorkSC > 0) {
                return (double)customersCountSC / (double)clientsCountReadyToWorkSC;
            }
            else {
                return 0;
            }
        }
    }
    
    static HashMap<Integer, WorkloadStatistics> workloadStatsHM = new HashMap<Integer, WorkloadStatistics>();
    static private int loopNumber = 0;
    
    /**
     * Проверяет загруженность операторов и состояние очередей,
     * чтобы в случае высокой нагрузки отправить email с уведомлением о сложившейся ситуации
     */
    private static void checkWorkload() {
        
        System.out.println("\n\nCHECK WORKLOAD!\n\n");
        
        //по каждой услуге обрабатываем инфу об очередях
        for (QService service : QServiceTree.getInstance().getNodes()) {
            if (service.getStatus().compareTo(1) != 0) {
                continue;
            }
            
            HashMap<Integer, ArrayList<ClientsApproximateWaitingInfo>> clientsInfo = calcApproximateWaitingTime(service);
            Set<Integer> uidSet = clientsInfo.keySet();
            
            System.out.println();
            System.out.println("LOOP #" + ++loopNumber);
            System.out.println();
            
            //проходимся циклом по каждому uid и если есть превышение по времени ожидания, то отправляем SMS
            for (Integer uid : uidSet) {
                
                System.out.println("UNIT ID: " + uid);
                
                for (ClientsApproximateWaitingInfo entry : clientsInfo.get(uid)) {
                    
                    System.out.println("APPROXIMATE WAITING TIME: " + entry.getApproximateWaitingTime());
                    
                    //отправляем SMS если предположительное время ожидания больше нормы и если нет таймаута
                    if (entry.getApproximateWaitingTime() > WAITING_TIME_ALARM && workloadTimeoutHM.get(uid) == null) {
                        
//                        1. КОНТАКТЫ ДЛЯ SMS ЛЕЖАТ В БД
//                        
//                        отправляем SMS, продумать как;
//                        посмотреть как отправляются SMS с терминалов;
//                        уточнить список номеров для отправки SMS;
//                        сделать таймауты на отправку сообщений;



                        setWorkloadTimeout(uid);
                    }
                }
                
                System.out.println();
                System.out.println("------------------");
                System.out.println();
            }
        }
    }
    
    private final static int WORKING_TIME_STANDARD = 7;
    private final static int WAITING_TIME_ALARM = 15;
    
    /**
     * Функция вычисляет ориентировочное время ожидания для последнего в очрееди клиента
     * @param service услуга по которой вычисляем
     * @return 
     */
    private static HashMap<Integer, ArrayList<ClientsApproximateWaitingInfo>> calcApproximateWaitingTime(QService service) {
        
        HashMap<Integer, ArrayList<UsersServiceInfo>> usersInfoByUidHM = new HashMap<>();
        HashMap<Integer, ArrayList<ClientsApproximateWaitingInfo>> clientsInfoByUidHM = new HashMap<>();

        //пройдёмся по операторам, соберём инфу о статусе и времени обслуживания
        for (QUser user : QUserList.getInstance().getItems()) {
            
            if (!usersInfoByUidHM.containsKey(user.getUnitId())) {
                usersInfoByUidHM.put(user.getUnitId(), new ArrayList<>());
            }
            
            ArrayList<UsersServiceInfo> usersInfoByUid = usersInfoByUidHM.get(user.getUnitId());

            if (user.getShadow() == null) {
                continue;
            }
            else if (user.getShadow().getStartTime() == null) {         
                //если свободно
                if (!user.isPause() && !user.isTechWork()) {
                    //так как оператор свободен, то ставим ему максимум по нормативу,
                    //чтобы при вычислении у него получался 0 для ожидания приёма
                    usersInfoByUid.add(new UsersServiceInfo(user, WORKING_TIME_STANDARD));
                }
            } else {
                //если в работе
                int mins = Math.round((new Date().getTime() - user.getShadow().getStartTime().getTime()) / 1000 / 60);
                usersInfoByUid.add(new UsersServiceInfo(user, mins));
            }
        }
        
        //пройдёмся по клиентам имея инфу об операторах
        service.getClients().forEach((uid, clients) -> {
            
            if (!clientsInfoByUidHM.containsKey(uid)) {
                clientsInfoByUidHM.put(uid, new ArrayList<>());
            }
            
            //берём только тех операторов, которые обслуживают текущую услугу
            ArrayList<UsersServiceInfo> usersInfoByPointType = (ArrayList<UsersServiceInfo>) usersInfoByUidHM.get(uid)
                    .stream()
                    .filter(ui -> Objects.equals(String.valueOf(ui.getUser().getPointType()), service.getPrefix()))
                    .collect(Collectors.toList());
            
            ArrayList<ClientsApproximateWaitingInfo> clientsInfoByUid = clientsInfoByUidHM.get(uid);
            
            int iUser = 0;
            int iMultiplier = 0;
            
            for (QCustomer c : clients) {
                
                UsersServiceInfo userInfo = usersInfoByPointType.get(iUser++);
                if (userInfo == null) {
                    continue;
                }
                
                int remainingTime = userInfo.getMinRemainingTime();
                int approximateWaitingTime = remainingTime + iMultiplier * WORKING_TIME_STANDARD;
                
                System.out.println("client: " + c.getNumber() + ", approx: " + approximateWaitingTime);
                
                clientsInfoByUid.add(new ClientsApproximateWaitingInfo(c, approximateWaitingTime));
                
                //если один цикл по операторам прошёл, то обнуляем индекс опертора
                //и увеличиваем множитель для подсчёта ожидания очереди
                if (iUser >= usersInfoByPointType.size()) {
                    iUser = 0;
                    iMultiplier++;
                }
            }
        });
        
        return clientsInfoByUidHM;
    }
    
    private static class ClientsApproximateWaitingInfo {
        private QCustomer client;
        private int approximateWaitingTime;

        public ClientsApproximateWaitingInfo(QCustomer client, int approximateWaitingTime) {
            this.client = client;
            this.approximateWaitingTime = approximateWaitingTime;
        }

        public QCustomer getClient() {
            return client;
        }

        public void setClient(QCustomer client) {
            this.client = client;
        }
        
        public int getApproximateWaitingTime() {
            return approximateWaitingTime;
        }

        public void setApproximateWaitingTime(int approximateWaitingTime) {
            this.approximateWaitingTime = approximateWaitingTime;
        }
    }
    
    private static class UsersServiceInfo {
        private QUser user;
        private int minInWork;

        public UsersServiceInfo(QUser user, int minInWork) {
            this.user = user;
            this.minInWork = minInWork;
        }

        public QUser getUser() {
            return user;
        }

        public void setUser(QUser user) {
            this.user = user;
        }

        public int getMinInWork() {
            return minInWork;
        }

        public void setMinInWork(int minInWork) {
            this.minInWork = minInWork;
        }
        
        public int getMinRemainingTime() {
            return (WORKING_TIME_STANDARD - minInWork) < 0 ? 1 : (WORKING_TIME_STANDARD - minInWork);
        }
    }
    
    /**
     * Количество операторов находящихся в состоянии "В работе" или "Готов".
     */
    private static void getCountUsersReadyToWork() {
        LinkedList<QUser> users = QUserList.getInstance().getItems();
        
        //цикл по всем пользователям системы
        for (QUser user : users) {
            
            if (user.getPointType() == null) {
                continue;
            }
            
            if (user.getShadow() == null) {
                continue;
            } else if (user.getShadow().getStartTime() == null) {
                //перерыв
                if (user.isPause()) {
                    continue;
                }
                //свободно
                else {
                    incrementUsersReadyToWork(user);
                }
            }
            //в работе
            else {
                incrementUsersReadyToWork(user);
            }
        }
    }
    
    /**
     * Наращиваем количество юзеров готовых работать в зависимости от unitId и pointType.
     * @param user Юзер которого анализируем.
     */
    private static void incrementUsersReadyToWork(QUser user) {
        //если ещё нету такого unitId, то создаём
        if (!workloadStatsHM.containsKey(user.getUnitId())) {
            workloadStatsHM.put(user.getUnitId(), new WorkloadStatistics());
        }
        
        WorkloadStatistics wls = workloadStatsHM.get(user.getUnitId());
        
        //если юзер относится к абон. залу
        if (user.getPointType().compareTo(QUser.POINT_TYPE_ABO) == 0) {
            wls.addClientsCountReadyToWorkAbo(1);
        }
        //если юзер относится к сервисному центру
        else if (user.getPointType().compareTo(QUser.POINT_TYPE_SC) == 0) {
            wls.addClientsCountReadyToWorkSC(1);
        }
    }
    
    private static void handleCustomersCountAndWaitingTime(QService service) {
        
        service.getClients().forEach((uid, customers) -> {
//            for (QCustomer customer : customers) {
                //если ещё нету такого unitId, то создаём
                if (!workloadStatsHM.containsKey(uid)) {
                    workloadStatsHM.put(uid, new WorkloadStatistics());
                }

                WorkloadStatistics wls = workloadStatsHM.get(uid);

                //префикс услуги 1 это абон. зал
                if (service.getPrefix().compareTo(QService.SERVICE_PREFIX_ABO) == 0) {
                    //прибавляем количество клиентов в очереди
                    wls.setCustomersCountAbo(wls.getCustomersCountAbo() + customers.size());

                    if (service.getClients() == null || service.getClients().size() <= 0) {
                        return;
                    }

                    Comparator<QCustomer> comp = (p1, p2) -> Integer.compare(p1.getWaitingMinutes(), p2.getWaitingMinutes());
                    int maxWaitingMinutes = customers.stream()
                                                     .max(comp)
                                                     .get()
                                                     .getWaitingMinutes();
                    wls.setMaxWaitingMinutesAbo(Integer.max(wls.getMaxWaitingMinutesAbo(), maxWaitingMinutes));
                }
                //префикс услуги 2 это СЦ
                else if (service.getPrefix().compareTo(QService.SERVICE_PREFIX_SC) == 0) {
                    wls.setCustomersCountSC(wls.getCustomersCountSC() + customers.size());

                    if (service.getClients() == null || service.getClients().size() <= 0) {
                        return;
                    }

                    Comparator<QCustomer> comp = (p1, p2) -> Integer.compare(p1.getWaitingMinutes(), p2.getWaitingMinutes());
                    int maxWaitingMinutes = customers.stream()
                                                     .max(comp)
                                                     .get()
                                                     .getWaitingMinutes();
                    wls.setMaxWaitingMinutesSC(Integer.max(wls.getMaxWaitingMinutesSC(), maxWaitingMinutes));
                }
//            }
        });
    }
    
    /**
     * Логгирование статистики очередей.
     */
    private static void loggingWorkloadStats() {
        
        if (workloadStatsHM == null) {
            return;
        }
        
        workloadStatsHM.forEach((unitId, wls) -> {
            String logText = "ИД зала: " + unitId.toString() + "\n" +
                             "Операторов готовых работать АБО: " + wls.getClientsCountReadyToWorkAbo() + "\n" +
                             "Операторов готовых работать СЦ: " + wls.getClientsCountReadyToWorkSC() + "\n" +
                             "Кол-во клиентов в очереди АБО: " + wls.getCustomersCountAbo() + "\n" +
                             "Кол-во клиентов в очереди СЦ: " + wls.getCustomersCountSC() + "\n" +
                             "Макс. время ожидания АБО: " + wls.getMaxWaitingMinutesAbo() + "\n" +
                             "Макс. время ожидания СЦ: " + wls.getMaxWaitingMinutesSC() + "\n" +
                             "Соотношение клиентов к операторам АБО: " + wls.getCustomersToClientsRatioForAbo() + "\n" +
                             "Соотношение клиентов к операторам СЦ: " + wls.getCustomersToClientsRatioForSC();
            
            QLog.l().logger().info(logText);
        });
    }
    
    private static final String subject = "Электронная очередь";
    private static String messageText = "";
    
    /**
     * Анализируем собранные данные и отправляем уведомления на почту, если требуется.
     */
    private static void sendMails() {
        //получаем лист с адресами для рассылки уведомлений
        ArrayList<QNotificationsInfo> ni = getEmailsForNotification();
        
        if (ni == null) {
            return;
        }
        
        //проходимся по каждому залу, по которому собрали статистику
        for (Map.Entry<Integer, WorkloadStatistics> entry : workloadStatsHM.entrySet()) {
            Integer unitId = entry.getKey();
            
            boolean needTimeout = false;
            
            for (QNotificationsInfo item : ni) {
                
                if (Objects.equals(item.getUnitId(), unitId)) {
                    String email = item.getEmail() == null || item.getEmail().compareTo("") == 0 ? null : item.getEmail();
                    if (email != null) {
                        messageText = "";
                        
                        //если требуется, то отправляем сообщение и устанавливаем таймаут
                        if (createMessageTextIfNeedNotification(item)) {
                            EmailSender esender = new EmailSender();
                            esender.sendMessages(email, subject, messageText);
                            
                            needTimeout = true;
                        }
                    }
                }
            }
            
            if (needTimeout) {
                setWorkloadTimeout(unitId);
            }
        }
    }
    
    /**
     * Проверка необходимости обправки уведомлений по e-mail.
     * @param ni Информация для отправки уведомления.
     * @return Возвращает true если требуется уведомить о большой очереди и false если нагрузка соответствует норме и уведомлять не требуется.
     */
    private static boolean createMessageTextIfNeedNotification(QNotificationsInfo ni) {
        if (workloadStatsHM == null || workloadStatsHM.isEmpty()) {
            return false;
        }
        
        if (workloadTimeoutHM.get(ni.getUnitId()) != null) {
            return false;
        }
        
        WorkloadStatistics wls = workloadStatsHM.get(ni.getUnitId());
        
        if (wls == null) {
            return false;
        }
        
        int maxWaitingMinutesAbo = wls.getMaxWaitingMinutesAbo();
        int maxWaitingMinutesSC = wls.getMaxWaitingMinutesSC();
        double ratioForAbo = wls.getCustomersToClientsRatioForAbo();
        double ratioForSC = wls.getCustomersToClientsRatioForSC();
        
        //если сейчас не рабочее время
        if (!isNowWorkingTime()) {
            return false;
        }
        //если время ожидания в абон. зале больше, чем значения при которых требуется отправить оповещение
        //в случае когда у какого-либо параметра ni указано значение 0, то не требуется уведомлять по текущему параметру,
        //в случае когда нету операторов готовых работать не присылаем уведомления
        else if (
                    (ni.getMaxWaitingMinutesAbo() != 0 && wls.getClientsCountReadyToWorkAbo() != 0 && maxWaitingMinutesAbo > ni.getMaxWaitingMinutesAbo() )
                        ||
                    (ni.getMaxWaitingMinutesSC() != 0 && wls.getClientsCountReadyToWorkSC() != 0 && maxWaitingMinutesSC > ni.getMaxWaitingMinutesSC())
                ) {
            
            messageText = "Внимание! Кол-во клиентов в очереди - " + (wls.getCustomersCountAbo() + wls.getCustomersCountSC()) +
                          ", кол-во актив. работников - " + (wls.getClientsCountReadyToWorkAbo() + wls.getClientsCountReadyToWorkSC()) +
                          ", макс. время ожидания - " + Integer.max(maxWaitingMinutesAbo, maxWaitingMinutesSC) + " мин.";
            return true;
        //если количество клиентов на одного юзера больше, чем значения при которых требуется отправить оповещение
        } else if (
                    (ni.getRatioAbo() != 0 && ratioForAbo >= ni.getRatioAbo())
                        ||
                    (ni.getRatioSC() != 0 && ratioForSC >= ni.getRatioSC())
                ) {
            
            messageText = "Внимание! Кол-во клиентов в очереди - " + (wls.getCustomersCountAbo() + wls.getCustomersCountSC()) +
                          ", кол-во актив. работников - " + (wls.getClientsCountReadyToWorkAbo() + wls.getClientsCountReadyToWorkSC()) +
                          ", макс. время ожидания - " + Integer.max(maxWaitingMinutesAbo, maxWaitingMinutesSC) + " мин.";
            return true;
        }
        
        return false;
    }
    
    /**
     * На 10 минут отключает проверку состояния очередей.
     * @param unitId Идентификатор зала для которого требуется установить таймаут.
     */
    private static void setWorkloadTimeout(int unitId) {
        
        if (!workloadTimeoutHM.containsKey(unitId)) {
            workloadTimeoutHM.put(unitId, new Date());
        } else {
            return;
        }
        
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable runnable = () -> {
            workloadTimeoutHM.forEach((unitId1, date) -> {
                if (date != null && getDiffBetweenDatesInMinutes(new Date(), date) >= 10) {
                    workloadTimeoutHM.remove(unitId1);
                }
            });
        };
        scheduler.schedule(runnable, 10, TimeUnit.MINUTES);
    }
    
    private static int getDiffBetweenDatesInMinutes(Date newDate, Date oldDate) {
        return (int)Math.ceil((newDate.getTime() - oldDate.getTime()) / 1000 / 60);
    }
    
    private static ArrayList<QNotificationsInfo> getEmailsForNotification() {
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("SomeTxName");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = Spring.getInstance().getTxManager().getTransaction(def);
        String query = "select id, fio, email, phone, maxWaitingMinutesAbo, maxWaitingMinutesSC, ratioAbo, ratioSC, unitId from QNotificationsInfo";
        ArrayList<QNotificationsInfo> ni;
        try {
            ni = Spring.getInstance().executeSelectNotificationsInfo(query);
        }
        catch (Exception ex) {
            throw new ServerException("\n" + ex.toString() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
        Spring.getInstance().getTxManager().commit(status);

        return ni;
    }
    
    private static boolean isNowWorkingTime() {
        final Date now = new Date();
        return now.after(getWorkingTimeStart()) && now.before(getWorkingTimeEnd());
    }
    
    private static Date getWorkingTimeStart(){
        final GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.HOUR_OF_DAY, 8);
        gc.set(Calendar.MINUTE, 5);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        Date date = gc.getTime();
        return date;
    }
    
    private static Date getWorkingTimeEnd(){
        final GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.HOUR_OF_DAY, 18);
        gc.set(Calendar.MINUTE, 55);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        Date date = gc.getTime();
        return date;
    }
    
    private static final Connection mySQLConnection = null;
    
    public static Connection getMySQLConnection() throws SQLException {
        if (mySQLConnection != null) {
            return mySQLConnection;
        }
        
        final String DB_HOST = QConfig.cfg().getServerAddress();
        final String DB_NAME = "qsystem";
        final int DB_PORT = 3306;
        final String MYSQL_CONNECTION_STRING = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
        final String DB_USER = "qsystem";
        final String DB_PASSWORD = "executenonquery";
        
        return DriverManager.getConnection(MYSQL_CONNECTION_STRING, DB_USER, DB_PASSWORD);
    }
    
    private static void startReservedTicketsTimer() {
        //раз в минуту проверяем есть ли зарезервированные талоны которые нужно в очередь добавить
        Timer checkReservedTicketsTimer = new Timer(60 * 1000, (ActionEvent e) -> {
//            QLog.l().logger().info("START RESERVE CHECKING");
            
            try {
                Connection con = getMySQLConnection();
                PreparedStatement stmt = con.prepareStatement(
                        "SELECT id, ticket_number, reserve_date, unit_id, ticket_id, service_id " +
                        "FROM reserved_tickets " +
                        "WHERE deleted IS NULL AND started IS NULL AND DATE_FORMAT(reserve_date, '%Y-%m-%d %H:%i:%s') <= str_to_date(CURRENT_TIMESTAMP, '%Y-%m-%d %H:%i:%s')"
                );
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt(1);
                    int ticketNumber = rs.getInt(2);
                    String reserveDateStr = rs.getString(3);
                    int unitId = rs.getInt(4);
                    int ticketId = rs.getInt(5);
                    long serviceId = rs.getLong(6);
                    Date reserveDate = null;
                    if (reserveDateStr != null) {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            reserveDate = formatter.parse(reserveDateStr);
                        } catch (ParseException ex) {
                            QLog.l().logger().error("Ошибка парсинга даты резервирования", ex);
                        }
                    }
                    
                    QLog.l().logger().info("TICKET_NUMBER: " + ticketNumber);
                    
                    //создаём клиента и добавляем в очередь
                    createCustomer(
                            ticketNumber,
                            reserveDate,
                            unitId,
                            serviceId
                            );
                    
                    QLog.l().logger().info("DELETING TICKET WITH ID: " + id);
                    
                    //удаляем отработанную запись из БД
                    deleteTicketFromDB(id);
                    
                    //меняем статус талона у Евстратенко
                    Uses.setTicketState(ticketId, Uses.TICKET_IN_QUEUE);
                }
            } catch (SQLException ex) {
                QLog.l().logger().error("Ошибка проверки зарезервированных талонов.", ex);
            }
        });
        checkReservedTicketsTimer.start();
    }
    
    public static void createCustomer(int ticketNumber, Date reserveDate, int unitId, long serviceId) {
        final QService service = QServiceTree.getInstance().getById(serviceId);
        final QCustomer customer;
        //нормальный приоритет
        int priority = 1;
        // синхронизируем работу с клиентом
        CLIENT_TASK_LOCK.lock();
        try {
            // Создадим вновь испеченного кастомера
            customer = new QCustomer(ticketNumber);
            customer.setUnitId(unitId);
            customer.setPriority(priority);
            customer.setState(CustomerState.STATE_WAIT);
            customer.setService(service);
            service.addCustomer(customer);
        } finally {
            CLIENT_TASK_LOCK.unlock();
        }
    }
    
    //помечается удалённым
    public static void deleteTicketFromDB(int id) {
        try {
            Connection con = getMySQLConnection();
            PreparedStatement stmt = con.prepareStatement(
                    "UPDATE reserved_tickets " +
                    "SET started = CURRENT_TIMESTAMP " + 
                    "WHERE id = ?"
            );
            stmt.setInt(1, id);
            stmt.execute();
        } catch (SQLException e) {
            QLog.l().logger().error("Ошибка удаления отработанной зарезервированной записи из БД", e);
        }
    }
    
    private static void startCleanUpTicketsTimer() {
        //каждые три часа чистим список использованных билетов
        Timer cleanUpTicketsTimer = new Timer(3 * 60 * 60 * 1000, (ActionEvent e) -> {
            
            QService.usedTickets.clear();
            
            QServiceTree.sailToStorm(QServiceTree.getInstance().getRoot(), service -> {
                ((QService)service).getClients().forEach((uid, customers) -> {
                    for (QCustomer customer : customers) {
                        if (!QService.usedTickets.containsKey(uid)) {
                            QService.usedTickets.put(uid, new LinkedList<>());
                        }
                        QService.usedTickets.get(uid).add(customer.getNumber());
                    }
                });
            });
            
            //проходимся по тем, кто уже в обслуживании
            for (QUser user : QUserList.getInstance().getItems()) {
                if (user.getCustomer() != null) {
                    if (!QService.usedTickets.containsKey(user.getCustomer().getUnitId())) {
                        QService.usedTickets.put(user.getCustomer().getUnitId(), new LinkedList<>());
                    }
                    QService.usedTickets.get(user.getCustomer().getUnitId()).add(user.getCustomer().getNumber());
                }
            }
            
            //проходимся по отложенным
            for (QCustomer customer : QPostponedList.getInstance().getPostponedCustomers()) {
                if (!QService.usedTickets.containsKey(customer.getUnitId())) {
                    QService.usedTickets.put(customer.getUnitId(), new LinkedList<>());
                }
                QService.usedTickets.get(customer.getUnitId()).add(customer.getNumber());
            }
            
            //проходимся по ушедшим на оплату
            for (QCustomer customer : QMovedToBankList.getInstance().getMovedToBankCustomers()) {
                if (!QService.usedTickets.containsKey(customer.getUnitId())) {
                    QService.usedTickets.put(customer.getUnitId(), new LinkedList<>());
                }
                QService.usedTickets.get(customer.getUnitId()).add(customer.getNumber());
            }
        });
        cleanUpTicketsTimer.start();
    }
    
    private static void startPostponedTimer() {
        Timer timerOut = new Timer(90 * 1000, (ActionEvent e) -> {
            
            String connectionString = "jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = amar-node1-vip.int.idknet.com)(PORT = 1521)) (ADDRESS = (PROTOCOL = TCP)(HOST = amar-node1.int.idknet.com)(PORT = 1521)) (ADDRESS = (PROTOCOL = TCP)(HOST = amar-node2-vip.int.idknet.com)(PORT = 1521)) (FAILOVER = yes) (LOAD_BALANCE = yes) (CONNECT_DATA = (SERVER = SHARED) (SERVICE_NAME = amar_s1) (FAILOVER_MODE = (TYPE = SELECT) (METHOD = BASIC) (RETRIES = 180) (DELAY = 5))))";
            String strUserID = "qsystem";
            String strPassword = "nyZd6je6R";
            ArrayList<TempTicket> tickets = new ArrayList<>();
            Connection myConnection = null;
            try {
                myConnection = DriverManager.getConnection(connectionString, strUserID, strPassword);
                Statement sqlStatement = myConnection.createStatement();

                ResultSet myResultSet = sqlStatement.executeQuery("select bs.qsys.get_ticket_list from dual");
                myResultSet.next();
                int columnCount = myResultSet.getMetaData().getColumnCount();
                for (int columnNumber = 1; columnNumber <= columnCount; columnNumber++) {
                    String columnName = myResultSet.getMetaData().getColumnName(columnNumber);
                    Object value = myResultSet.getObject(columnName);
//                    QLog.l().logger().info(columnName);
                    if (value != null) {
                        while(((ResultSet)value).next()) {
                            tickets.add(new TempTicket(((ResultSet)value).getInt("ID_TICKET"),
                                                       ((ResultSet)value).getString("CODE"),
                                                       ((ResultSet)value).getInt("ID_TICKET_STATE")));
                        }   
                    }
                }
                
                //новый механизм работы с банком
                for (QCustomer customer : QMovedToBankList.getInstance().getMovedToBankCustomers()) {
                    if (customer.getPostponedStatus().toLowerCase().contains("отправлен на оплату") || customer.getState() == CustomerState.STATE_PAYMENT) {
                        List<TempTicket> temp = tickets.stream().filter(t -> t.code.equals(customer.getNumber())).collect(Collectors.toList());
                        QLog.l().logger().debug(customer.getNumber());
                        if (temp.size() > 0) {
                            if (temp.get(0).state == 1) {
                                QLog.l().logger().debug("Попытаемся кастомера " + temp.get(0).code + " переместить в очередь из отложенных");
                                //если приоритет меньше высокого, то увеличиваем его (до VIP не увеличиваем)
//                                if (customer.getPriority().get() < Uses.PRIORITY_HI) {
//                                    customer.setPriority(customer.getPriority().get() + 1);
//                                }
                                //делаем максимальный приоритет после оплаты
                                customer.setPriority(Uses.PRIORITY_VIP);
                                
                                //вроде как только что встал в очередь, ну и время проставим,
                                //а то ожидание будет огромное только что встал типо;
                                //просто время нахождения в отложенных не считается как ожидание очереди,
                                //иначе в statistic ожидание огромное
                                customer.setStandTime(new Date());
                                customer.setState(CustomerState.STATE_WAIT_AFTER_PAYMENT);
                                
                                //добавим нового пользователя в очередь
                                final QService service = QServiceTree.getInstance().getById(customer.getService().getId());
                                service.addCustomer(customer);
                                
                                //удалим из списка ушедших на оплату
                                removeCustomerFromList(customer);
                                
                                QLog.l().logger().debug("Сменили время кастомеру " + temp.get(0).code + ". При следующем опросе отложенных он будет вызван");
                                
                                String call = ("{call bs.qsys.set_ticket_state(?, ?)}");
                                try (CallableStatement stmt = myConnection.prepareCall(call)) {
                                    stmt.setInt(1, temp.get(0).id);
                                    //2 - для удаления из таблицы
                                    stmt.setInt(2, 2);
                                    stmt.execute();
                                } catch (Exception exeption) {
                                    throw new ServerException("Ошибка проверки оплаты по счету в биллинге " + exeption);
                                } finally {
                                    if (myConnection != null) {
                                        myConnection.close();
                                    }
                                }
                            }
                        }
                    }
                }
                
                //старый механизм работы с банком (для залов где нету эл. очереди АПБ)
                for (QCustomer customer : QPostponedList.getInstance().getPostponedCustomers()) {
                    if (customer.getPostponedStatus().toLowerCase().contains("отправлен на оплату") || customer.getState() == CustomerState.STATE_PAYMENT) {
                        List<TempTicket> temp = tickets.stream().filter(t -> t.code.equals(customer.getNumber())).collect(Collectors.toList());
                        QLog.l().logger().debug(customer.getNumber());
                        if (temp.size() > 0) {
                            if (temp.get(0).state == 1) {
                                QLog.l().logger().debug("Попытаемся кастомера " + temp.get(0).code + " переместить в очередь из отложенных");
                                
                                customer.setFinishPostpone(System.currentTimeMillis());
                                
                                QLog.l().logger().debug("Сменили время кастомеру " + temp.get(0).code + ". При следующем опросе отложенных он будет вызван");
                                
                                String call = ("{call bs.qsys.set_ticket_state(?, ?)}");
                                try (CallableStatement stmt = myConnection.prepareCall(call)) {
                                    stmt.setInt(1, temp.get(0).id);
                                    //2 - для удаления из таблицы
                                    stmt.setInt(2, 2);
                                    stmt.execute();
                                } catch (Exception exeption) {
                                    throw new ServerException("Ошибка проверки оплаты по счету в биллинге " + exeption);
                                } finally {
                                    if (myConnection != null) {
                                        myConnection.close();
                                    }
                                }
                            }
                        }
                    }
                }
                
            } catch (Exception ex) {
                throw new ServerException("Ошибка проверки оплаты по счету в биллинге " + ex);
            }
        });
        timerOut.start();
    }
    
    private static void removeCustomerFromList(QCustomer customer) {
        Executer.MOVED_TO_BANK_TASK_LOCK.lock();
        try {
            QMovedToBankList.getInstance().removeElement(customer);
        } finally {
            Executer.MOVED_TO_BANK_TASK_LOCK.unlock();
        }
    } 
    
    /**
     * @param socket
     */
    public QServer(Socket socket) {
        this.socket = socket;
        // и запускаем новый вычислительный поток (см. ф-ю run())
        setDaemon(true);
        setPriority(NORM_PRIORITY);
    }

    @Override
    public void run() {
        try {
//            QLog.l().logger().debug(" Start thread for receiving task. host=" + socket.getInetAddress().getHostAddress() + " ip=" + Arrays.toString(socket.getInetAddress().getAddress()));

            // из сокета клиента берём поток входящих данных
            InputStream is;
            try {
                is = socket.getInputStream();
            } catch (IOException e) {
                throw new ServerException("Input Stream broken: " + Arrays.toString(e.getStackTrace()));
            }

            final String data;
            try {
                // подождать пока хоть что-то приползет из сети, но не более 10 сек.
                int i = 0;
                while (is.available() == 0 && i < 100) {
                    Thread.sleep(100);//бля
                    i++;
                }

                StringBuilder sb = new StringBuilder(new String(Uses.readInputStream(is)));
                while (is.available() != 0) {
                    sb = sb.append(new String(Uses.readInputStream(is)));
                    Thread.sleep(150);//бля
                }
                data = URLDecoder.decode(sb.toString(), "utf-8");
            } catch (IOException ex) {
                throw new ServerException("Ошибка при чтении из входного потока: " + ex);
            } catch (InterruptedException ex) {
                throw new ServerException("Проблема со сном: " + ex);
            } catch (IllegalArgumentException ex) {
                throw new ServerException("Ошибка декодирования сетевого сообщения: " + ex);
            }
//            QLog.l().logger().trace("Task:\n" + (data.length() > 200 ? (data.substring(0, 200) + "...") : data));

            /*
             Если по сетке поймали exit, то это значит что запустили останавливающий батник.
             */
            if ("exit".equalsIgnoreCase(data)) {
                globalExit = true;
                return;
            }

            final String answer;
            final JsonRPC20 rpc;
            final Gson gson = GsonPool.getInstance().borrowGson();
            try {
                rpc = gson.fromJson(data, JsonRPC20.class);
                // полученное задание передаем в пул
                final Object result = Executer.getInstance().doTask(rpc, socket.getInetAddress().getHostAddress(), socket.getInetAddress().getAddress());
                answer = gson.toJson(result);
            } catch (JsonSyntaxException ex) {
                QLog.l().logger().error("Received data \"" + data + "\" has not correct JSON format. ", ex);
                throw new ServerException("Received data \"" + data + "\" has not correct JSON format. " + Arrays.toString(ex.getStackTrace()));
            } catch (Exception ex) {
                QLog.l().logger().error("Late caught the error when running the command. ", ex);
                throw new ServerException("Поздно пойманная ошибка при выполнении команды: " + Arrays.toString(ex.getStackTrace()));
            } finally {
                GsonPool.getInstance().returnGson(gson);
            }

            // выводим данные:
//            QLog.l().logger().trace("Response:\n" + (answer.length() > 200 ? (answer.substring(0, 200) + "...") : answer));
            try {
                // Передача данных ответа
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.print(URLEncoder.encode(answer, "utf-8"));
                writer.flush();
            } catch (IOException e) {
                throw new ServerException("Ошибка при записи в поток: " + Arrays.toString(e.getStackTrace()));
            }
        } catch (ServerException | JsonParseException ex) {
            final StringBuilder sb = new StringBuilder("\nStackTrace:\n");
            for (StackTraceElement bag : ex.getStackTrace()) {
                sb.append("    at ").append(bag.getClassName()).append(".").append(bag.getMethodName()).append("(").append(bag.getFileName()).append(":").append(bag.getLineNumber()).append(")\n");
            }
            final String err = sb.toString() + "\n";
            sb.setLength(0);
            throw new ServerException("Ошибка при выполнении задания.\n" + ex + err);
        } finally {
            // завершаем соединение
            try {
                //оборачиваем close, т.к. он сам может сгенерировать ошибку IOExeption. Просто выкинем Стек-трейс
                socket.close();
            } catch (IOException e) {
                QLog.l().logger().trace(e);
            }
//            QLog.l().logger().trace("Response was finished");
        }
    }

    /**
     * Сохранение состояния пула услуг в xml-файл на диск
     */
    public synchronized static void savePool() {
        final long start = System.currentTimeMillis();
//        QLog.l().logger().info("Save the state.");
        final LinkedList<QCustomer> backup = new LinkedList<>();// создаем список кастомеров в очереди + тех кто в обслуживании
        final LinkedList<QCustomer> parallelBackup = new LinkedList<>();// создаем список сохраняемых Parallel кастомеров
        final LinkedList<Long> pauses = new LinkedList<>();// создаем список юзеров у которых менопауза
        QServiceTree.getInstance().getNodes().stream().forEach((service) -> {
            backup.addAll(service.getClientsForBackup());
        });

        QUserList.getInstance().getItems().forEach((user) -> {
            if (user.getCustomer() != null) {
                backup.add(user.getCustomer());
            }
            parallelBackup.addAll(user.getParallelCustomers().values());
            if (user.isPause()) {
                pauses.add(user.getId());
            }
        });
        // в темповый файл
        final FileOutputStream fos;
        try {
            (new File(Uses.TEMP_FOLDER)).mkdir();
            fos = new FileOutputStream(new File(Uses.TEMP_FOLDER + File.separator + Uses.TEMP_STATE_FILE));
        } catch (FileNotFoundException ex) {
            throw new ServerException("Не возможно создать временный файл состояния. " + ex.getMessage());
        }
        Gson gson = null;
        try {
            gson = GsonPool.getInstance().borrowGson();
            
            fos.write(gson.toJson(new TempList(backup,
                                               parallelBackup,
                                               QPostponedList.getInstance().getPostponedCustomers(),
                                               QMovedToBankList.getInstance().getMovedToBankCustomers(),
                                               pauses,
                                               usersToCustomers)).getBytes("UTF-8"));
            fos.flush();
            fos.close();
        } catch (IOException ex) {
            throw new ServerException("Не возможно сохранить изменения в поток." + ex.getMessage());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
//        QLog.l().logger().info("Состояние сохранено. Затрачено времени: " + ((double) (System.currentTimeMillis() - start)) / 1000 + " сек.");
    }

    static public class TempList {

        public TempList() {
        }

        public TempList(LinkedList<QCustomer> backup, LinkedList<QCustomer> parallelBackup, LinkedList<QCustomer> postponed) {
            this.backup = backup;
            this.parallelBackup = parallelBackup;
            this.postponed = postponed;
        }

        public TempList(LinkedList<QCustomer> backup,
                        LinkedList<QCustomer> parallelBackup,
                        LinkedList<QCustomer> postponed,
                        LinkedList<QCustomer> movedToPayment,
                        LinkedList<Long> pauses,
                        HashMap<Long, Long> usersToCustomers) {
            this.backup = backup;
            this.parallelBackup = parallelBackup;
            this.postponed = postponed;
            this.movedToPayment = movedToPayment;
            this.pauses = pauses;
            this.usersToCustomers = usersToCustomers;
        }
        @Expose
        @SerializedName("backup")
        public LinkedList<QCustomer> backup;
        @Expose
        @SerializedName("parallelBackup")
        public LinkedList<QCustomer> parallelBackup;
        @Expose
        @SerializedName("postponed")
        public LinkedList<QCustomer> postponed;
        @Expose
        @SerializedName("movedToPayment")
        public LinkedList<QCustomer> movedToPayment;
        @Expose
        @SerializedName("method")
        public String method = null;
        @Expose
        @SerializedName("pauses")
        public LinkedList<Long> pauses = null;
        @Expose
        @SerializedName("usersToCustomers")
        public HashMap<Long, Long> usersToCustomers;
        @Expose
        @SerializedName("date")
        public Long date = new Date().getTime();
    }

    /**
     * Загрузка состояния пула услуг из временного json-файла
     */
    static public void loadPool() {
        final long start = System.currentTimeMillis();
        // если есть временный файлик сохранения состояния, то надо его загрузить.
        // все ошибки чтения и парсинга игнорить.
        QLog.l().logger().info("Пробуем восстановить состояние системы.");
        File recovFile = new File(Uses.TEMP_FOLDER + File.separator + Uses.TEMP_STATE_FILE);
        if (recovFile.exists()) {
            QLog.l().logger().warn(Locales.locMes("came_back"));
            //восстанавливаем состояние

            final FileInputStream fis;
            try {
                fis = new FileInputStream(recovFile);
            } catch (FileNotFoundException ex) {
                throw new ServerException(ex);
            }
            final Scanner scan = new Scanner(fis, "utf8");
            String rec_data = "";
            while (scan.hasNextLine()) {
                rec_data += scan.nextLine();
            }
            try {
                fis.close();
            } catch (IOException ex) {
                throw new ServerException(ex);
            }

            final TempList recList;
            final Gson gson = GsonPool.getInstance().borrowGson();
            final RpcGetAdvanceCustomer rpc;
            try {
                recList = gson.fromJson(rec_data, TempList.class);
            } catch (JsonSyntaxException ex) {
                throw new ServerException("Не возможно интерпритировать сохраненные данные.\n" + ex.toString());
            } finally {
                GsonPool.getInstance().returnGson(gson);
            }

            // Проверим не просрочился ли кеш. Время просточки 3 часа.
            if (!QConfig.cfg().isRetain() && (recList.date == null || new Date().getTime() - recList.date > 3 * 60 * 60 * 1000)) {
                // Просрочился кеш, не грузим
                QLog.l().logger().warn("Срок давности хранения состояния истек. Если в системе ничего не происходит 3 часа, то считается что сохраненные данные устарели безвозвратно.");
            } else {
                // Свежий, загружаем в сервер данные кеша
                try {
                    //загружаем список отложенных
                    QPostponedList.getInstance().loadPostponedList(recList.postponed);
                    //загружаем список ушедших на оплату
                    QMovedToBankList.getInstance().loadMovedToBankList(recList.movedToPayment);
                    //загружаем список тех, с кем уже работали операторы
                    for (QCustomer recCustomer : recList.backup) {
                        if (!QService.usedTickets.containsKey(recCustomer.getUnitId())) {
                            QService.usedTickets.put(recCustomer.getUnitId(), new LinkedList<>());
                        }
                        QService.usedTickets.get(recCustomer.getUnitId()).add(recCustomer.getNumber());
                        //QService.usedTickets = new LinkedList<>();
                        final QService service = QServiceTree.getInstance().getById(recCustomer.getService().getId());
                        if (service == null) {
                            QLog.l().logger().warn("Попытка добавить клиента \"" + recCustomer.getPrefix() + recCustomer.getNumber() + "\" к услуге \"" + recCustomer.getService().getName() + "\" не успешна. Услуга не обнаружена!");
                            continue;
                        }
                        service.setCountPerDay(recCustomer.getService().getCountPerDay());
                        service.setDay(recCustomer.getService().getDay());
                        // так зовут юзера его обрабатываюшего
                        final QUser user = recCustomer.getUser();
                        // кастомер ща стоит к этой услуге к какой стоит
                        recCustomer.setService(service);
                        // смотрим к чему привязан кастомер. либо в очереди стоит, либо у юзера обрабатыватся
                        if (user == null) {
                            // сохраненный кастомер стоял в очереди и ждал, но его еще никто не звал
                            QServiceTree.getInstance().getById(recCustomer.getService().getId()).addCustomerForRecoveryOnly(recCustomer);
                            QLog.l().logger().debug("Добавили клиента \"" + recCustomer.getPrefix() + recCustomer.getNumber() + "\" к услуге \"" + recCustomer.getService().getName() + "\"");
                        } else {
                            // сохраненный кастомер обрабатывался юзером с именем userId
                            if (QUserList.getInstance().getById(user.getId()) == null) {
                                QLog.l().logger().warn("Попытка добавить клиента \"" + recCustomer.getPrefix() + recCustomer.getNumber() + "\" к юзеру \"" + user.getName() + "\" не успешна. Юзер не обнаружен!");
                                continue;
                                
                            }
                            QUserList.getInstance().getById(user.getId()).setCustomer(recCustomer);
                            recCustomer.setUser(QUserList.getInstance().getById(user.getId()));
                            QLog.l().logger().debug("Добавили клиента \"" + recCustomer.getPrefix() + recCustomer.getNumber() + "\" к юзеру \"" + user.getName() + "\"");
                        }
                    }
                    // Параллельные кастомеры, загрузим
                    for (QCustomer recCustomer : recList.parallelBackup) {
                        // в эту очередь он был
                        final QService service = QServiceTree.getInstance().getById(recCustomer.getService().getId());
                        if (service == null) {
                            QLog.l().logger().warn("Попытка добавить клиента \"" + recCustomer.getPrefix() + recCustomer.getNumber() + "\" к услуге \"" + recCustomer.getService().getName() + "\" не успешна. Услуга не обнаружена!");
                            continue;
                        }
                        service.setCountPerDay(recCustomer.getService().getCountPerDay());
                        service.setDay(recCustomer.getService().getDay());
                        // так зовут юзера его обрабатываюшего
                        final QUser user = recCustomer.getUser();
                        // кастомер ща стоит к этой услуге к какой стоит
                        recCustomer.setService(service);
                        // смотрим к чему привязан кастомер. либо в очереди стоит, либо у юзера обрабатыватся
                        if (user == null) {
                            QLog.l().logger().warn("Для параллельного клиента \"" + recCustomer.getPrefix() + recCustomer.getNumber() + "\" добавление к юзеру не успешна. Юзер потерялся!");
                        } else {
                            // сохраненный кастомер обрабатывался юзером с именем userId
                            if (QUserList.getInstance().getById(user.getId()) == null) {
                                QLog.l().logger().warn("Попытка добавить параллельного клиента \"" + recCustomer.getPrefix() + recCustomer.getNumber() + "\" к юзеру \"" + user.getName() + "\" не успешна. Юзер не обнаружен!");
                                continue;
                            }
                            QUserList.getInstance().getById(user.getId()).setCustomer(recCustomer);
                            recCustomer.setUser(QUserList.getInstance().getById(user.getId()));
                            QLog.l().logger().debug("Добавили клиента \"" + recCustomer.getPrefix() + recCustomer.getNumber() + "\" к юзеру \"" + user.getName() + "\"");
                        }
                    }
                    recList.pauses.stream().map((idUser) -> QUserList.getInstance().getById(idUser)).filter((user) -> (user != null)).forEachOrdered((user) -> {
                        user.setPause(Boolean.TRUE);
                    });
                    
                    if (recList.usersToCustomers != null) {
                        usersToCustomers = recList.usersToCustomers;
                    }
                } catch (ServerException ex) {
                    System.err.println("Востановление состояния сервера после изменения конфигурации. " + ex);
                    clearAllQueue();
                    QLog.l().logger().error("Востановление состояния сервера после изменения конфигурации. Для выключения сервера используйте команду exit. ", ex);
                }
            }
        }
        QLog.l().logger().info("Восстановление состояния системы завершено. Затрачено времени: " + ((double) (System.currentTimeMillis() - start)) / 1000 + " сек.");
    }

    static public void clearAllQueue() {
        // почистим все услуги от трупов кастомеров
        QServiceTree.getInstance().getNodes().forEach((service) -> {
            service.clearNextNumber();
            service.freeCustomers();
        });
        QService.clearNextStNumber();

        QMovedToBankList.getInstance().clear();
        QPostponedList.getInstance().clear();
        MainBoard.getInstance().clear();

        // Сотрем временные файлы
        deleteTempFile();
        QLog.l().logger().info("Очистка всех пользователей от привязанных кастомеров.");
        QUserList.getInstance().getItems().forEach((user) -> {
            user.setCustomer(null);
            user.getParallelCustomers().clear();
            user.setShadow(null);
            user.getPlanServices().forEach((plan) -> {
                plan.setAvg_wait(0);
                plan.setAvg_work(0);
                plan.setKilled(0);
                plan.setWorked(0);
            });
        });
        QLog.l().logger().info("Очистка списка использованных билетов.");
//        QService.usedTickets = new LinkedList<>();
        QService.usedTickets.clear();
    }

    public static void deleteTempFile() {
        QLog.l().logger().debug("Remove " + Uses.TEMP_FOLDER + File.separator + Uses.TEMP_STATE_FILE);
        File file = new File(Uses.TEMP_FOLDER + File.separator + Uses.TEMP_STATE_FILE);
        if (file.exists()) {
            file.delete();
        }
        QLog.l().logger().debug("Remove " + Uses.TEMP_FOLDER + File.separator + Uses.TEMP_STATATISTIC_FILE);
        file = new File(Uses.TEMP_FOLDER + File.separator + Uses.TEMP_STATATISTIC_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}