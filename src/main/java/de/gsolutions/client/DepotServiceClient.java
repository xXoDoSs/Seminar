package de.gsolutions.client;

import de.gsolutions.model.Depot;
import de.gsolutions.model.Position;

import javax.xml.soap.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * SOAP-Client für den bestehenden DepotService.
 * Ruft http://localhost:8080/depotservice?wsdl auf.
 *
 * Liefert die Depots eines Kunden mit deren Positionen (ISIN, Menge, Währung).
 */
public class DepotServiceClient {

    // ====== HIER ANPASSEN an deine WSDL ======
    private static final String ENDPOINT = "http://localhost:8080/depotservice";
    private static final String NAMESPACE = "http://gsolutions.de/depotservice";
    // ==========================================

    /**
     * Holt alle Depots eines Kunden anhand der Kundennummer.
     */
    public List<Depot> getDepots(int kundennummer) {
        List<Depot> depots = new ArrayList<>();

        try {
            SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = factory.createConnection();

            // SOAP-Request bauen
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage request = messageFactory.createMessage();
            SOAPEnvelope envelope = request.getSOAPPart().getEnvelope();
            envelope.addNamespaceDeclaration("dep", NAMESPACE);

            SOAPBody body = envelope.getBody();
            SOAPElement operation = body.addChildElement("getDepots", "dep");
            SOAPElement param = operation.addChildElement("kundennummer", "dep");
            param.addTextNode(String.valueOf(kundennummer));

            request.saveChanges();

            // Senden
            SOAPMessage response = connection.call(request, ENDPOINT);
            connection.close();

            // Response parsen
            SOAPBody responseBody = response.getSOAPBody();
            SOAPElement responseElement = (SOAPElement) responseBody.getChildElements().next();

            // Iteriere über Depot-Elemente
            Iterator<?> depotElements = responseElement.getChildElements();
            while (depotElements.hasNext()) {
                Object depotObj = depotElements.next();
                if (!(depotObj instanceof SOAPElement)) continue;
                SOAPElement depotElem = (SOAPElement) depotObj;

                if (depotElem.getLocalName().equalsIgnoreCase("depot")) {
                    Depot depot = parseDepot(depotElem);
                    depots.add(depot);
                }
            }

        } catch (Exception e) {
            System.err.println("Fehler beim Aufruf des DepotService: " + e.getMessage());
            e.printStackTrace();
        }

        return depots;
    }

    /**
     * Parst ein einzelnes Depot-Element aus der SOAP-Response.
     * ====== HIER ANPASSEN falls die Struktur anders ist ======
     */
    private Depot parseDepot(SOAPElement depotElem) {
        Depot depot = new Depot();

        Iterator<?> children = depotElem.getChildElements();
        while (children.hasNext()) {
            Object child = children.next();
            if (!(child instanceof SOAPElement)) continue;
            SOAPElement elem = (SOAPElement) child;
            String name = elem.getLocalName().toLowerCase();
            String value = elem.getTextContent();

            switch (name) {
                case "depotid":
                case "id":
                    depot.setDepotId(value);
                    break;
                case "position":
                case "positionen":
                    Position pos = parsePosition(elem);
                    depot.getPositionen().add(pos);
                    break;
            }
        }

        return depot;
    }

    /**
     * Parst eine einzelne Position aus der SOAP-Response.
     */
    private Position parsePosition(SOAPElement posElem) {
        Position position = new Position();

        Iterator<?> children = posElem.getChildElements();
        while (children.hasNext()) {
            Object child = children.next();
            if (!(child instanceof SOAPElement)) continue;
            SOAPElement elem = (SOAPElement) child;
            String name = elem.getLocalName().toLowerCase();
            String value = elem.getTextContent();

            switch (name) {
                case "isin":
                    position.setIsin(value);
                    break;
                case "menge":
                case "quantity":
                case "stueck":
                    position.setMenge(Integer.parseInt(value));
                    break;
                case "waehrung":
                case "currency":
                    position.setWaehrung(value);
                    break;
            }
        }

        return position;
    }
}
