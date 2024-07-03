
package ru.apertum.qsystem.server.webservice.prereg;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.apertum.qsystem.server.webservice.prereg package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetFilialsFreeTickets_QNAME = new QName("http://services.prereg.qsystem.com/", "get_filials_free_tickets");
    private final static QName _GetFilialsFreeTicketsResponse_QNAME = new QName("http://services.prereg.qsystem.com/", "get_filials_free_ticketsResponse");
    private final static QName _GetFilials_QNAME = new QName("http://services.prereg.qsystem.com/", "get_filials");
    private final static QName _SetReserve_QNAME = new QName("http://services.prereg.qsystem.com/", "set_reserve");
    private final static QName _GetFilialsResponse_QNAME = new QName("http://services.prereg.qsystem.com/", "get_filialsResponse");
    private final static QName _SetReserveResponse_QNAME = new QName("http://services.prereg.qsystem.com/", "set_reserveResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.apertum.qsystem.server.webservice.prereg
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetFilialsFreeTicketsResponse }
     * 
     */
    public GetFilialsFreeTicketsResponse createGetFilialsFreeTicketsResponse() {
        return new GetFilialsFreeTicketsResponse();
    }

    /**
     * Create an instance of {@link GetFilialsFreeTickets }
     * 
     */
    public GetFilialsFreeTickets createGetFilialsFreeTickets() {
        return new GetFilialsFreeTickets();
    }

    /**
     * Create an instance of {@link GetFilials }
     * 
     */
    public GetFilials createGetFilials() {
        return new GetFilials();
    }

    /**
     * Create an instance of {@link SetReserve }
     * 
     */
    public SetReserve createSetReserve() {
        return new SetReserve();
    }

    /**
     * Create an instance of {@link SetReserveResponse }
     * 
     */
    public SetReserveResponse createSetReserveResponse() {
        return new SetReserveResponse();
    }

    /**
     * Create an instance of {@link GetFilialsResponse }
     * 
     */
    public GetFilialsResponse createGetFilialsResponse() {
        return new GetFilialsResponse();
    }

    /**
     * Create an instance of {@link ReserveInterval }
     * 
     */
    public ReserveInterval createReserveInterval() {
        return new ReserveInterval();
    }

    /**
     * Create an instance of {@link Filial }
     * 
     */
    public Filial createFilial() {
        return new Filial();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFilialsFreeTickets }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.prereg.qsystem.com/", name = "get_filials_free_tickets")
    public JAXBElement<GetFilialsFreeTickets> createGetFilialsFreeTickets(GetFilialsFreeTickets value) {
        return new JAXBElement<GetFilialsFreeTickets>(_GetFilialsFreeTickets_QNAME, GetFilialsFreeTickets.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFilialsFreeTicketsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.prereg.qsystem.com/", name = "get_filials_free_ticketsResponse")
    public JAXBElement<GetFilialsFreeTicketsResponse> createGetFilialsFreeTicketsResponse(GetFilialsFreeTicketsResponse value) {
        return new JAXBElement<GetFilialsFreeTicketsResponse>(_GetFilialsFreeTicketsResponse_QNAME, GetFilialsFreeTicketsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFilials }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.prereg.qsystem.com/", name = "get_filials")
    public JAXBElement<GetFilials> createGetFilials(GetFilials value) {
        return new JAXBElement<GetFilials>(_GetFilials_QNAME, GetFilials.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetReserve }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.prereg.qsystem.com/", name = "set_reserve")
    public JAXBElement<SetReserve> createSetReserve(SetReserve value) {
        return new JAXBElement<SetReserve>(_SetReserve_QNAME, SetReserve.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFilialsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.prereg.qsystem.com/", name = "get_filialsResponse")
    public JAXBElement<GetFilialsResponse> createGetFilialsResponse(GetFilialsResponse value) {
        return new JAXBElement<GetFilialsResponse>(_GetFilialsResponse_QNAME, GetFilialsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetReserveResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://services.prereg.qsystem.com/", name = "set_reserveResponse")
    public JAXBElement<SetReserveResponse> createSetReserveResponse(SetReserveResponse value) {
        return new JAXBElement<SetReserveResponse>(_SetReserveResponse_QNAME, SetReserveResponse.class, null, value);
    }

}
