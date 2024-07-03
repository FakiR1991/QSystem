
package ru.apertum.qsystem.server.webservice.prereg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for set_reserve complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="set_reserve">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ticket_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="reason_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ticket_number" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="my_idc_acc_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="juridical" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="name1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="comments" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "set_reserve", propOrder = {
    "ticketId",
    "reasonId",
    "ticketNumber",
    "myIdcAccId",
    "juridical",
    "name1",
    "name2",
    "name3",
    "title",
    "email",
    "phone",
    "comments"
})
public class SetReserve {

    @XmlElement(name = "ticket_id")
    protected int ticketId;
    @XmlElement(name = "reason_id")
    protected int reasonId;
    @XmlElement(name = "ticket_number")
    protected int ticketNumber;
    @XmlElement(name = "my_idc_acc_id")
    protected int myIdcAccId;
    protected int juridical;
    protected String name1;
    protected String name2;
    protected String name3;
    protected String title;
    protected String email;
    protected String phone;
    protected String comments;

    /**
     * Gets the value of the ticketId property.
     * 
     */
    public int getTicketId() {
        return ticketId;
    }

    /**
     * Sets the value of the ticketId property.
     * 
     */
    public void setTicketId(int value) {
        this.ticketId = value;
    }

    /**
     * Gets the value of the reasonId property.
     * 
     */
    public int getReasonId() {
        return reasonId;
    }

    /**
     * Sets the value of the reasonId property.
     * 
     */
    public void setReasonId(int value) {
        this.reasonId = value;
    }

    /**
     * Gets the value of the ticketNumber property.
     * 
     */
    public int getTicketNumber() {
        return ticketNumber;
    }

    /**
     * Sets the value of the ticketNumber property.
     * 
     */
    public void setTicketNumber(int value) {
        this.ticketNumber = value;
    }

    /**
     * Gets the value of the myIdcAccId property.
     * 
     */
    public int getMyIdcAccId() {
        return myIdcAccId;
    }

    /**
     * Sets the value of the myIdcAccId property.
     * 
     */
    public void setMyIdcAccId(int value) {
        this.myIdcAccId = value;
    }

    /**
     * Gets the value of the juridical property.
     * 
     */
    public int getJuridical() {
        return juridical;
    }

    /**
     * Sets the value of the juridical property.
     * 
     */
    public void setJuridical(int value) {
        this.juridical = value;
    }

    /**
     * Gets the value of the name1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName1() {
        return name1;
    }

    /**
     * Sets the value of the name1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName1(String value) {
        this.name1 = value;
    }

    /**
     * Gets the value of the name2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName2() {
        return name2;
    }

    /**
     * Sets the value of the name2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName2(String value) {
        this.name2 = value;
    }

    /**
     * Gets the value of the name3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName3() {
        return name3;
    }

    /**
     * Sets the value of the name3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName3(String value) {
        this.name3 = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the phone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the value of the phone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhone(String value) {
        this.phone = value;
    }

    /**
     * Gets the value of the comments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the value of the comments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComments(String value) {
        this.comments = value;
    }

}
