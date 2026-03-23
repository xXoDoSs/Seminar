package de.gsolutions.client;

import javax.xml.soap.*;
import java.util.Iterator;

/**
 * SOAP-Client für den bestehenden PriceService.
 * Ruft http://localhost:8080/priceservice?wsdl auf.
 *
 * Liefert Preisinformation für ein Wertpapier (ISIN) und Devisenkurse.
 */
public class PriceServiceClient {

    // ====== HIER ANPASSEN an deine WSDL ======
    private static final String ENDPOINT = "http://localhost:8080/priceservice";
    private static final String NAMESPACE = "http://gsolutions.de/priceservice";
    // ==========================================

    /**
     * Holt den aktuellen Kurs für eine ISIN in der angegebenen Währung.
     *
     * @param isin     z.B. "DE0005140008" (Deutsche Bank)
     * @param waehrung z.B. "EUR"
     * @return Kurs als double, oder 0.0 bei Fehler
     */
    public double getPreis(String isin, String waehrung) {
        try {
            SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = factory.createConnection();

            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage request = messageFactory.createMessage();
            SOAPEnvelope envelope = request.getSOAPPart().getEnvelope();
            envelope.addNamespaceDeclaration("pri", NAMESPACE);

            SOAPBody body = envelope.getBody();
            SOAPElement operation = body.addChildElement("getPreis", "pri");

            SOAPElement isinParam = operation.addChildElement("isin", "pri");
            isinParam.addTextNode(isin);

            SOAPElement waehrungParam = operation.addChildElement("waehrung", "pri");
            waehrungParam.addTextNode(waehrung);

            request.saveChanges();

            SOAPMessage response = connection.call(request, ENDPOINT);
            connection.close();

            // Response parsen - erwarte einen Preis-Wert
            SOAPBody responseBody = response.getSOAPBody();
            SOAPElement responseElement = (SOAPElement) responseBody.getChildElements().next();

            Iterator<?> children = responseElement.getChildElements();
            while (children.hasNext()) {
                Object child = children.next();
                if (child instanceof SOAPElement) {
                    SOAPElement elem = (SOAPElement) child;
                    String name = elem.getLocalName().toLowerCase();
                    if (name.equals("preis") || name.equals("price") || name.equals("kurs")) {
                        return Double.parseDouble(elem.getTextContent());
                    }
                }
            }

            // Fallback: versuche den Textwert direkt zu parsen
            String text = responseElement.getTextContent().trim();
            if (!text.isEmpty()) {
                return Double.parseDouble(text);
            }

        } catch (Exception e) {
            System.err.println("Fehler beim Aufruf des PriceService für ISIN " + isin + ": " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Holt den Devisenkurs (z.B. EUR nach USD).
     *
     * @param vonWaehrung z.B. "USD"
     * @param nachWaehrung z.B. "EUR"
     * @return Umrechnungskurs, oder 1.0 bei gleicher Währung
     */
    public double getDevisenkurs(String vonWaehrung, String nachWaehrung) {
        if (vonWaehrung.equalsIgnoreCase(nachWaehrung)) {
            return 1.0;
        }

        try {
            SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = factory.createConnection();

            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage request = messageFactory.createMessage();
            SOAPEnvelope envelope = request.getSOAPPart().getEnvelope();
            envelope.addNamespaceDeclaration("pri", NAMESPACE);

            SOAPBody body = envelope.getBody();
            SOAPElement operation = body.addChildElement("getDevisenkurs", "pri");

            SOAPElement vonParam = operation.addChildElement("vonWaehrung", "pri");
            vonParam.addTextNode(vonWaehrung);

            SOAPElement nachParam = operation.addChildElement("nachWaehrung", "pri");
            nachParam.addTextNode(nachWaehrung);

            request.saveChanges();

            SOAPMessage response = connection.call(request, ENDPOINT);
            connection.close();

            SOAPBody responseBody = response.getSOAPBody();
            SOAPElement responseElement = (SOAPElement) responseBody.getChildElements().next();

            Iterator<?> children = responseElement.getChildElements();
            while (children.hasNext()) {
                Object child = children.next();
                if (child instanceof SOAPElement) {
                    SOAPElement elem = (SOAPElement) child;
                    String name = elem.getLocalName().toLowerCase();
                    if (name.contains("kurs") || name.contains("rate") || name.contains("preis")) {
                        return Double.parseDouble(elem.getTextContent());
                    }
                }
            }

            String text = responseElement.getTextContent().trim();
            if (!text.isEmpty()) {
                return Double.parseDouble(text);
            }

        } catch (Exception e) {
            System.err.println("Fehler beim Aufruf des Devisenkurses: " + e.getMessage());
            e.printStackTrace();
        }

        return 1.0; // Fallback
    }
}
