package de.gsolutions.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Ein Depot eines Kunden mit Liste von Positionen.
 */
@XmlType(name = "Depot")
public class Depot {

    private String depotId;
    private List<Position> positionen = new ArrayList<>();
    private double depotWert; // Summe aller Positionswerte (in Kundenwährung)

    public Depot() {}

    public Depot(String depotId) {
        this.depotId = depotId;
    }

    @XmlElement
    public String getDepotId() { return depotId; }
    public void setDepotId(String depotId) { this.depotId = depotId; }

    @XmlElement(name = "position")
    public List<Position> getPositionen() { return positionen; }
    public void setPositionen(List<Position> positionen) { this.positionen = positionen; }

    @XmlElement
    public double getDepotWert() { return depotWert; }
    public void setDepotWert(double depotWert) { this.depotWert = depotWert; }
}
