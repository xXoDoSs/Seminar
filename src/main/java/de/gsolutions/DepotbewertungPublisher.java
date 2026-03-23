package de.gsolutions;

import de.gsolutions.service.DepotbewertungService;

import javax.xml.ws.Endpoint;

/**
 * Startet den neuen DepotbewertungService als SOAP-Endpoint.
 *
 * WSDL wird verfügbar unter:
 *   http://localhost:8081/depotbewertungservice?wsdl
 *
 * Voraussetzung: Die 3 bestehenden Services müssen auf Port 8080 laufen!
 *   - http://localhost:8080/customerservice?wsdl
 *   - http://localhost:8080/depotservice?wsdl
 *   - http://localhost:8080/priceservice?wsdl
 */
public class DepotbewertungPublisher {

    private static final String SERVICE_URL = "http://localhost:8081/depotbewertungservice";

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("  gSolutions - Depotbewertung Service");
        System.out.println("============================================");
        System.out.println();

        // Service veröffentlichen
        DepotbewertungService service = new DepotbewertungService();
        Endpoint endpoint = Endpoint.publish(SERVICE_URL, service);

        System.out.println("Service gestartet!");
        System.out.println("WSDL: " + SERVICE_URL + "?wsdl");
        System.out.println();
        System.out.println("Drücke ENTER zum Beenden...");

        try {
            System.in.read();
        } catch (Exception e) {
            // ignore
        }

        endpoint.stop();
        System.out.println("Service gestoppt.");
    }
}
