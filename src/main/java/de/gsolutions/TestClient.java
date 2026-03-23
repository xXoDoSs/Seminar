package de.gsolutions;

import javax.xml.soap.*;
import java.util.Iterator;

/**
 * Einfacher Test-Client für den neuen DepotbewertungService.
 *
 * Aufruf: Starte zuerst den DepotbewertungPublisher,
 *         dann diesen Client.
 */
public class TestClient {

    private static final String SERVICE_URL = "http://localhost:8081/depotbewertungservice";
    private static final String NAMESPACE = "http://gsolutions.de/depotbewertungservice";

    public static void main(String[] args) {
        // Teste mit Kundennummer 1 bis 5
        int kundennummer = 1;
        if (args.length > 0) {
            kundennummer = Integer.parseInt(args[0]);
        }

        System.out.println("Teste Depotbewertung für Kunde: " + kundennummer);
        System.out.println("================================================");

        try {
            SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = factory.createConnection();

            // SOAP-Request bauen
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage request = messageFactory.createMessage();
            SOAPEnvelope envelope = request.getSOAPPart().getEnvelope();
            envelope.addNamespaceDeclaration("dep", NAMESPACE);

            SOAPBody body = envelope.getBody();
            SOAPElement operation = body.addChildElement("getDepotbewertung", "dep");
            SOAPElement param = operation.addChildElement("kundennummer", "dep");
            param.addTextNode(String.valueOf(kundennummer));

            request.saveChanges();

            // Request anzeigen
            System.out.println("\n--- SOAP Request ---");
            request.writeTo(System.out);
            System.out.println("\n");

            // Senden
            SOAPMessage response = connection.call(request, SERVICE_URL);
            connection.close();

            // Response anzeigen
            System.out.println("--- SOAP Response ---");
            response.writeTo(System.out);
            System.out.println("\n");

            // Response parsen und formatiert ausgeben
            SOAPBody responseBody = response.getSOAPBody();
            printElement(responseBody, 0);

        } catch (Exception e) {
            System.err.println("Fehler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gibt ein SOAP-Element rekursiv formatiert aus.
     */
    private static void printElement(SOAPElement element, int indent) {
        String prefix = "  ".repeat(indent);

        Iterator<?> children = element.getChildElements();
        while (children.hasNext()) {
            Object child = children.next();
            if (child instanceof SOAPElement) {
                SOAPElement elem = (SOAPElement) child;
                String name = elem.getLocalName();

                // Hat Kinder? Dann rekursiv ausgeben
                boolean hasChildElements = false;
                Iterator<?> grandchildren = elem.getChildElements();
                while (grandchildren.hasNext()) {
                    if (grandchildren.next() instanceof SOAPElement) {
                        hasChildElements = true;
                        break;
                    }
                }

                if (hasChildElements) {
                    System.out.println(prefix + name + ":");
                    printElement(elem, indent + 1);
                } else {
                    System.out.println(prefix + name + " = " + elem.getTextContent());
                }
            }
        }
    }
}
