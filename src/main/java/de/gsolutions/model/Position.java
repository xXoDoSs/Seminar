package de.gsolutions.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Eine Wertpapierposition innerhalb eines Depots.
 * ISIN = International Securities Identification Number
 */
@XmlType(name = "Position")
public class Position {

    private String isin;
    private int menge;          // Anzahl Stücke
    private String waehrung;    // Kaufwährung
    private double kurs;        // aktueller Kurs (wird vom PriceService geholt)
    private double wert;        // menge * kurs (berechnet)

    public Position() {}

    public Position(String isin, int menge, String waehrung) {
        this.isin = isin;
        this.menge = menge;
        this.waehrung = waehrung;
    }

    @XmlElement
    public String getIsin() { return isin; }
    public void setIsin(String isin) { this.isin = isin; }

    @XmlElement
    public int getMenge() { return menge; }
    public void setMenge(int menge) { this.menge = menge; }

    @XmlElement
    public String getWaehrung() { return waehrung; }
    public void setWaehrung(String waehrung) { this.waehrung = waehrung; }

    @XmlElement
    public double getKurs() { return kurs; }
    public void setKurs(double kurs) { this.kurs = kurs; }

    @XmlElement
    public double getWert() { return wert; }
    public void setWert(double wert) { this.wert = wert; }
}
