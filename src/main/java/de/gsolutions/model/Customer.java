package de.gsolutions.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Kundendaten - kommt vom CustomerService.
 * =========================================
 * ACHTUNG: Falls deine WSDL andere Feldnamen hat, hier anpassen!
 */
@XmlType(name = "Customer")
public class Customer {

    private int kundennummer;
    private String kundenname;
    private String waehrung; // Kundenwährung, z.B. "EUR" oder "USD"

    public Customer() {}

    public Customer(int kundennummer, String kundenname, String waehrung) {
        this.kundennummer = kundennummer;
        this.kundenname = kundenname;
        this.waehrung = waehrung;
    }

    @XmlElement
    public int getKundennummer() { return kundennummer; }
    public void setKundennummer(int kundennummer) { this.kundennummer = kundennummer; }

    @XmlElement
    public String getKundenname() { return kundenname; }
    public void setKundenname(String kundenname) { this.kundenname = kundenname; }

    @XmlElement
    public String getWaehrung() { return waehrung; }
    public void setWaehrung(String waehrung) { this.waehrung = waehrung; }
}
