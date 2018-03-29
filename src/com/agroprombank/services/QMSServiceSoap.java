/**
 * QMSServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.agroprombank.services;

public interface QMSServiceSoap extends java.rmi.Remote {
    public java.lang.String AppendRequest(java.math.BigDecimal unitid, java.lang.String requestid, java.lang.String ticketid, java.math.BigDecimal priorityid, java.math.BigDecimal redirection, java.math.BigDecimal pointidfrom, java.math.BigDecimal pointidto, java.lang.String additioninfo) throws java.rmi.RemoteException;
}
