/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.webservice;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

/**
 *
 * @author zaikov
 */
@WebServiceClient(name = "QueueIntegrationImplService",
                  targetNamespace = "http://webservice.server.qsystem.apertum.ru/",
                  wsdlLocation = "http://localhost:9901/queue?wsdl")
public class QueueIntegrationImplService extends Service {
    private final static URL UCIMPLSERVICE_WSDL_LOCATION;
    private final static WebServiceException UCIMPLSERVICE_EXCEPTION;
    private final static QName UCIMPLSERVICE_QNAME = new QName("http://webservice.server.qsystem.apertum.ru/", "QueueIntegrationImplService");

    static {
        URL url = null;
        WebServiceException e = null;
        try 
        {
            url = new URL("http://localhost:9901/queue?wsdl");
        } 
        catch (MalformedURLException ex) 
        {
            e = new WebServiceException(ex);
        }
        UCIMPLSERVICE_WSDL_LOCATION = url;
        UCIMPLSERVICE_EXCEPTION = e;
    }

    public QueueIntegrationImplService()
    {
        super(__getWsdlLocation(), UCIMPLSERVICE_QNAME);
    }

    public QueueIntegrationImplService(WebServiceFeature... features) 
    {
        super(__getWsdlLocation(), UCIMPLSERVICE_QNAME, features);
    }

    public QueueIntegrationImplService(URL wsdlLocation) 
    {
        super(wsdlLocation, UCIMPLSERVICE_QNAME);
    }

    public QueueIntegrationImplService(URL wsdlLocation, WebServiceFeature... features) 
    {
        super(wsdlLocation, UCIMPLSERVICE_QNAME, features);
    }

    public QueueIntegrationImplService(URL wsdlLocation, QName serviceName) 
    {
        super(wsdlLocation, serviceName);
    }

    public QueueIntegrationImplService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) 
    {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * @return
     *    returns UC
     */
    @WebEndpoint(name = "QueueIntegrationImplPort")
    public QueueIntegration getUCImplPort() 
    {
        return super.getPort(new QName("http://webservice.server.qsystem.apertum.ru/", "QueueIntegrationImplPort"), 
                             QueueIntegration.class);
    }

    /**
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the
     *     proxy. Supported features not in the features parameter 
     *     will have their default values.
     * @return
     *     returns UC
     */
    @WebEndpoint(name = "QueueIntegrationImplPort")
    public QueueIntegration getUCImplPort(WebServiceFeature... features) 
    {
        return super.getPort(new QName("http://webservice.server.qsystem.apertum.ru/", "QueueIntegrationImplPort"), 
                             QueueIntegration.class, features);
    }

    private static URL __getWsdlLocation() 
    {
        if (UCIMPLSERVICE_EXCEPTION != null) {
            throw UCIMPLSERVICE_EXCEPTION;
        }
        return UCIMPLSERVICE_WSDL_LOCATION;
    }
}