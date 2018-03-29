/**
 * QMSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.agroprombank.services;

public class QMSServiceLocator extends org.apache.axis.client.Service implements com.agroprombank.services.QMSService {

    public QMSServiceLocator() {
    }


    public QMSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public QMSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for QMSServiceSoap
    private java.lang.String QMSServiceSoap_address = "https://ws.agroprombank.com/qms/APB.Services.QMSService.asmx";

    public java.lang.String getQMSServiceSoapAddress() {
        return QMSServiceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String QMSServiceSoapWSDDServiceName = "QMSServiceSoap";

    public java.lang.String getQMSServiceSoapWSDDServiceName() {
        return QMSServiceSoapWSDDServiceName;
    }

    public void setQMSServiceSoapWSDDServiceName(java.lang.String name) {
        QMSServiceSoapWSDDServiceName = name;
    }

    public com.agroprombank.services.QMSServiceSoap getQMSServiceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(QMSServiceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getQMSServiceSoap(endpoint);
    }

    public com.agroprombank.services.QMSServiceSoap getQMSServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.agroprombank.services.QMSServiceSoapStub _stub = new com.agroprombank.services.QMSServiceSoapStub(portAddress, this);
            _stub.setPortName(getQMSServiceSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setQMSServiceSoapEndpointAddress(java.lang.String address) {
        QMSServiceSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.agroprombank.services.QMSServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.agroprombank.services.QMSServiceSoapStub _stub = new com.agroprombank.services.QMSServiceSoapStub(new java.net.URL(QMSServiceSoap_address), this);
                _stub.setPortName(getQMSServiceSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("QMSServiceSoap".equals(inputPortName)) {
            return getQMSServiceSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://services.agroprombank.com/", "QMSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://services.agroprombank.com/", "QMSServiceSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("QMSServiceSoap".equals(portName)) {
            setQMSServiceSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
