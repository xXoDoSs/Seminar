package de.gsolutions.service;

import de.gsolutions.client.CustomerServiceClient;
import de.gsolutions.client.DepotServiceClient;
import de.gsolutions.client.PriceServiceClient;
import de.gsolutions.model.Customer;
import de.gsolutions.model.Depot;
import de.gsolutions.model.Depotbewertung;
import de.gsolutions.model.Position;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import java.util.List;

/**
 * ========================================================
 * NEUER SOAP-SERVICE: Depotbewertung
 * ========================================================
 * Orchestriert die 3 bestehenden Services:
 * 1. CustomerService  -> Kundendaten + Währung
 * 2. DepotService     -> Depots + Positionen (ISIN, Menge)
 * 3. PriceService     -> Kurse + Devisenkurse
 *
 * Berechnet das Gesamtvermögen in Kundenwährung.
 *
 * WSDL wird verfügbar unter:
 *   http://localhost:8081/depotbewertungservice?wsdl
 */
@WebService(
    name = "DepotbewertungService",
    serviceName = "DepotbewertungService",
    targetNamespace = "http://gsolutions.de/depotbewertungservice"
)
public class DepotbewertungService {

    private final CustomerServiceClient customerClient = new CustomerServiceClient();
    private final DepotServiceClient depotClient = new DepotServiceClient();
    private final PriceServiceClient priceClient = new PriceServiceClient();

    /**
     * Hauptoperation: Bewertet alle Depots eines Kunden.
     *
     * @param kundennummer Die Kundennummer (z.B. 1 bis 5)
     * @return Komplette Depotbewertung mit allen Details
     */
    @WebMethod(operationName = "getDepotbewertung")
    @WebResult(name = "depotbewertung")
    public Depotbewertung getDepotbewertung(
            @WebParam(name = "kundennummer") int kundennummer) {

        System.out.println("=== Depotbewertung für Kunde " + kundennummer + " gestartet ===");

        Depotbewertung bewertung = new Depotbewertung();
        bewertung.setKundennummer(kundennummer);

        // -------------------------------------------------------
        // SCHRITT 1: Kundendaten vom CustomerService holen
        // -------------------------------------------------------
        System.out.println("1. Hole Kundendaten...");
        Customer customer = customerClient.getCustomer(kundennummer);

        if (customer == null) {
            System.err.println("Kunde " + kundennummer + " nicht gefunden!");
            bewertung.setKundenname("UNBEKANNT");
            bewertung.setKundenwaehrung("EUR");
            return bewertung;
        }

        bewertung.setKundenname(customer.getKundenname());
        bewertung.setKundenwaehrung(customer.getWaehrung());
        String kundenWaehrung = customer.getWaehrung();

        System.out.println("   Kunde: " + customer.getKundenname()
                + " (Währung: " + kundenWaehrung + ")");

        // -------------------------------------------------------
        // SCHRITT 2: Depots vom DepotService holen
        // -------------------------------------------------------
        System.out.println("2. Hole Depots...");
        List<Depot> depots = depotClient.getDepots(kundennummer);
        System.out.println("   " + depots.size() + " Depot(s) gefunden.");

        double gesamtVermoegen = 0.0;

        // -------------------------------------------------------
        // SCHRITT 3: Für jede Position den Kurs vom PriceService
        //            holen und Wert berechnen
        // -------------------------------------------------------
        for (Depot depot : depots) {
            System.out.println("3. Bewerte Depot: " + depot.getDepotId());
            double depotWert = 0.0;

            for (Position position : depot.getPositionen()) {
                // Kurs in der Positionswährung holen
                double kurs = priceClient.getPreis(position.getIsin(), position.getWaehrung());
                position.setKurs(kurs);

                // Wert in Positionswährung berechnen
                double wertInPosWaehrung = position.getMenge() * kurs;

                // Falls die Positionswährung != Kundenwährung -> umrechnen
                double wertInKundenWaehrung;
                if (!position.getWaehrung().equalsIgnoreCase(kundenWaehrung)) {
                    double devisenkurs = priceClient.getDevisenkurs(
                            position.getWaehrung(), kundenWaehrung);
                    wertInKundenWaehrung = wertInPosWaehrung * devisenkurs;
                    System.out.println("   " + position.getIsin()
                            + ": " + position.getMenge() + " x " + kurs
                            + " " + position.getWaehrung()
                            + " = " + wertInPosWaehrung + " " + position.getWaehrung()
                            + " -> " + wertInKundenWaehrung + " " + kundenWaehrung
                            + " (Kurs: " + devisenkurs + ")");
                } else {
                    wertInKundenWaehrung = wertInPosWaehrung;
                    System.out.println("   " + position.getIsin()
                            + ": " + position.getMenge() + " x " + kurs
                            + " = " + wertInKundenWaehrung + " " + kundenWaehrung);
                }

                position.setWert(wertInKundenWaehrung);
                depotWert += wertInKundenWaehrung;
            }

            depot.setDepotWert(depotWert);
            gesamtVermoegen += depotWert;
            System.out.println("   -> Depotwert: " + depotWert + " " + kundenWaehrung);
        }

        bewertung.setDepots(depots);
        bewertung.setGesamtvermoegen(gesamtVermoegen);

        System.out.println("=== GESAMTVERMÖGEN: " + gesamtVermoegen
                + " " + kundenWaehrung + " ===");

        return bewertung;
    }
}
