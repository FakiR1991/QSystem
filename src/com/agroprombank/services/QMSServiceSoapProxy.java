package com.agroprombank.services;

public class QMSServiceSoapProxy implements com.agroprombank.services.QMSServiceSoap {
  private String _endpoint = null;
  private com.agroprombank.services.QMSServiceSoap qMSServiceSoap = null;
  
  public QMSServiceSoapProxy() {
    _initQMSServiceSoapProxy();
  }
  
  public QMSServiceSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initQMSServiceSoapProxy();
  }
  
  private void _initQMSServiceSoapProxy() {
    try {
      qMSServiceSoap = (new com.agroprombank.services.QMSServiceLocator()).getQMSServiceSoap();
      if (qMSServiceSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)qMSServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)qMSServiceSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (qMSServiceSoap != null)
      ((javax.xml.rpc.Stub)qMSServiceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.agroprombank.services.QMSServiceSoap getQMSServiceSoap() {
    if (qMSServiceSoap == null)
      _initQMSServiceSoapProxy();
    return qMSServiceSoap;
  }
  
  public java.lang.String AppendRequest(java.math.BigDecimal unitid, java.lang.String requestid, java.lang.String ticketid, java.math.BigDecimal priorityid, java.math.BigDecimal redirection, java.math.BigDecimal pointidfrom, java.math.BigDecimal pointidto, java.lang.String additioninfo) throws java.rmi.RemoteException{
    if (qMSServiceSoap == null)
      _initQMSServiceSoapProxy();
    return qMSServiceSoap.AppendRequest(unitid, requestid, ticketid, priorityid, redirection, pointidfrom, pointidto, additioninfo);
  }
  
  
}