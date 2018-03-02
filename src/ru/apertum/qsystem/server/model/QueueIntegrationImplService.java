/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.model;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

/**
 * Класс-клиент для веб-сервиса Агропромбанка, который реализует функции для взаимодействия нашей системы с системой электронной очереди Агропромбанка.
 * @author zaikov
 */
@WebServiceClient(name = "QueueIntegrationImplService",
                  targetNamespace = "http://model.server.qsystem.apertum.ru/",
                  wsdlLocation = "http://localhost:9901/queue?wsdl")
public class QueueIntegrationImplService extends Service {
    
    private final static URL QI_SERVICE_WSDL_LOCATION;
    private final static WebServiceException QI_SERVICE_EXCEPTION;
    private final static QName QI_SERVICE_QNAME = new QName("http://model.server.qsystem.apertum.ru/", "QueueIntegrationImplService");
    
    static {
        URL url = null;
        WebServiceException e = null;
        try {
           url = new URL("http://localhost:9901/queue?wsdl");
        } catch (MalformedURLException ex) {
           e = new WebServiceException(ex);
        }
        QI_SERVICE_WSDL_LOCATION = url;
        QI_SERVICE_EXCEPTION = e;
    }

    public QueueIntegrationImplService() {
       super(__getWsdlLocation(), QI_SERVICE_QNAME);
    }

    public QueueIntegrationImplService(WebServiceFeature... features) {
       super(__getWsdlLocation(), QI_SERVICE_QNAME, features);
    }

    public QueueIntegrationImplService(URL wsdlLocation) {
       super(wsdlLocation, QI_SERVICE_QNAME);
    }

    public QueueIntegrationImplService(URL wsdlLocation, WebServiceFeature... features) {
       super(wsdlLocation, QI_SERVICE_QNAME, features);
    }

    public QueueIntegrationImplService(URL wsdlLocation, QName serviceName) {
       super(wsdlLocation, serviceName);
    }

    public QueueIntegrationImplService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
       super(wsdlLocation, serviceName, features);
    }

    /**
     * @return
     *    returns QueueIntegration
     */
    @WebEndpoint(name = "QueueIntegrationImplPort")
    public QueueIntegration getQueueIntegrationImplPort() {
       return super.getPort(new QName("http://model.server.qsystem.apertum.ru/", "QueueIntegrationImplPort"),
                            QueueIntegration.class);
    }

    /**
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the
     *     proxy. Supported features not in the features parameter 
     *     will have their default values.
     * @return
     *     returns QueueIntegration
     */
    @WebEndpoint(name = "QueueIntegrationImplPort")
    public QueueIntegration getQueueIntegrationImplPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://model.server.qsystem.apertum.ru/", "QueueIntegrationImplPort"),
                             QueueIntegration.class, features);
    }

    private static URL __getWsdlLocation() {
        if (QI_SERVICE_EXCEPTION != null) {
            throw QI_SERVICE_EXCEPTION;
        }
        
        return QI_SERVICE_WSDL_LOCATION;
    }
}