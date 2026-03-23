package de.gsolutions.client;

import de.gsolutions.model.Customer;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.net.URL;

/**
 * SOAP-Client für den bestehenden CustomerService.
 * Ruft http://localhost:8080/customerservice?wsdl auf.
 *
 * =====================================================================
 * VARIANTE A (EMPFOHLEN): wsimport-generierte Stubs verwenden
 * =====================================================================
 * Wenn du wsimport laufen lässt (im pom.xml das Plugin aktivieren),
 * werden automatisch Java-Klassen generiert. Dann kannst du diese
 * Klasse durch die generierten Stubs ersetzen. Beispiel:
 *
 *   CustomerService_Service service = new CustomerService_Service();
 *   CustomerServicePortType port = service.getCustomerServicePort();
 *   CustomerType result = port.getCustomer(kundennummer);
 *
 * =====================================================================
 * VARIANTE B (AKTUELL): Manueller SOAP-Aufruf mit SOAPMessage
 * =====================================================================
 * Da ich die WSDL nicht kenne, baue ich die SOAP-Nachricht manuell.
 * Die Namespace-URIs und Elementnamen musst du ggf. an deine WSDL anpassen!
 */
public class CustomerServiceClient {

    // ====== HIER ANPASSEN an deine WSDL ======
    private static final String WSDL_URL = "http://localhost:8080/customerservice?wsdl";
    private static final String NAMESPACE = "http://gsolutions.de/customerservice";
    private static final String SERVICE_NAME = "CustomerService";
    private static final String PORT_NAME = "CustomerServicePort";
    // ==========================================

    /**
     * Holt Kundendaten anhand der Kundennummer.
     */
    public Customer getCustomer(int kundennummer) {
        try {
            // SOAP-Verbindung aufbauen
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // SOAP-Request erstellen
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();

            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration("cus", NAMESPACE);

            // Body aufbauen
            SOAPBody body = envelope.getBody();
            SOAPElement operation = body.addChildElement("getCustomer", "cus");
            SOAPElement param = operation.addChildElement("kundennummer", "cus");
            param.addTextNode(String.valueOf(kundennummer));

            soapMessage.saveChanges();

            // Senden
            SOAPMessage response = soapConnection.call(soapMessage, WSDL_URL.replace("?wsdl", ""));
            soapConnection.close();

            // Response parsen
            SOAPBody responseBody = response.getSOAPBody();

            // ====== HIER ANPASSEN an die tatsächliche Response-Struktur ======
            Customer customer = new Customer();
            customer.setKundennummer(kundennummer);

            // Versuche die Felder aus der SOAP-Response zu lesen
            SOAPElement responseElement = (SOAPElement) responseBody.getChildElements().next();

            java.util.Iterator<?> children = responseElement.getChildElements();
            while (children.hasNext()) {
                Object next = children.next();
                if (next instanceof SOAPElement) {
                    SOAPElement elem = (SOAPElement) next;
                    String localName = elem.getLocalName();
                    String value = elem.getTextContent();

                    switch (localName.toLowerCase()) {
                        case "kundenname":
                        case "name":
                            customer.setKundenname(value);
                            break;
                        case "waehrung":
                        case "currency":
                            customer.setWaehrung(value);
                            break;
                        case "kundennummer":
                        case "id":
                            customer.setKundennummer(Integer.parseInt(value));
                            break;
                    }
                }
            }

            return customer;

        } catch (Exception e) {
            System.err.println("Fehler beim Aufruf des CustomerService: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
