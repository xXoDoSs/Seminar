package de.gsolutions.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Ergebnis der Depotbewertung:
 * - Kundendaten
 * - Liste aller Depots mit Beständen und Kursen
 * - Gesamtvermögen in Kundenwährung
 */
@XmlRootElement(name = "Depotbewertung")
@XmlType(name = "Depotbewertung")
public class Depotbewertung {

    private int kundennummer;
    private String kundenname;
    private String kundenwaehrung;
    private List<Depot> depots = new ArrayList<>();
    private double gesamtvermoegen; // Summe aller Depots in Kundenwährung

    public Depotbewertung() {}

    @XmlElement
    public int getKundennummer() { return kundennummer; }
    public void setKundennummer(int kundennummer) { this.kundennummer = kundennummer; }

    @XmlElement
    public String getKundenname() { return kundenname; }
    public void setKundenname(String kundenname) { this.kundenname = kundenname; }

    @XmlElement
    public String getKundenwaehrung() { return kundenwaehrung; }
    public void setKundenwaehrung(String kundenwaehrung) { this.kundenwaehrung = kundenwaehrung; }

    @XmlElement(name = "depot")
    public List<Depot> getDepots() { return depots; }
    public void setDepots(List<Depot> depots) { this.depots = depots; }

    @XmlElement
    public double getGesamtvermoegen() { return gesamtvermoegen; }
    public void setGesamtvermoegen(double gesamtvermoegen) { this.gesamtvermoegen = gesamtvermoegen; }
}
