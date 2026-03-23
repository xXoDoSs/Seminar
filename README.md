# gSolutions Depotbewertung Service

## Projektstruktur

```
depotbewertung/
├── pom.xml
└── src/main/java/de/gsolutions/
    ├── DepotbewertungPublisher.java    ← Startet den neuen Service
    ├── TestClient.java                  ← Test-Client zum Testen
    ├── model/
    │   ├── Customer.java               ← Kundendaten
    │   ├── Depot.java                  ← Depot mit Positionen
    │   ├── Position.java               ← Einzelne Wertpapierposition
    │   └── Depotbewertung.java         ← Ergebnis-Objekt
    ├── client/
    │   ├── CustomerServiceClient.java  ← Ruft bestehenden CustomerService
    │   ├── DepotServiceClient.java     ← Ruft bestehenden DepotService
    │   └── PriceServiceClient.java     ← Ruft bestehenden PriceService
    └── service/
        └── DepotbewertungService.java  ← DER NEUE SERVICE (Kern-Logik)
```

## Voraussetzungen

1. Java 11+ installiert
2. Maven installiert
3. Die 3 bestehenden SOAP-Services müssen laufen:
   - http://localhost:8080/customerservice?wsdl
   - http://localhost:8080/depotservice?wsdl
   - http://localhost:8080/priceservice?wsdl

## Bauen & Starten

```bash
# Projekt bauen
mvn clean package

# Service starten (Port 8081)
mvn exec:java -Dexec.mainClass="de.gsolutions.DepotbewertungPublisher"

# Test-Client starten (in neuem Terminal)
mvn exec:java -Dexec.mainClass="de.gsolutions.TestClient" -Dexec.args="1"
```

## WSDL

Nach dem Start erreichbar unter:
**http://localhost:8081/depotbewertungservice?wsdl**

## Was du anpassen musst

### WICHTIG: Namespace & Feldnamen an deine WSDLs anpassen!

Öffne die 3 bestehenden WSDLs im Browser und prüfe:

1. **Namespace URI** - in jedem Client die `NAMESPACE`-Konstante anpassen
2. **Operationsnamen** - z.B. heißt es vielleicht `findCustomer` statt `getCustomer`
3. **Feldnamen** - z.B. `name` statt `kundenname`

### Alternativ: wsimport verwenden (empfohlen!)

1. Aktiviere das `jaxws-maven-plugin` im `pom.xml` (auskommentiert)
2. Starte die 3 bestehenden Services
3. `mvn generate-sources` generiert dann automatisch die Client-Stubs
4. Ersetze die manuellen Client-Klassen durch die generierten

## Ablauf des Services

```
Client → getDepotbewertung(kundennummer=3)
  │
  ├─→ CustomerService.getCustomer(3)
  │   └─ Kundenname, Währung
  │
  ├─→ DepotService.getDepots(3)
  │   └─ Depot 1: [ISIN-A: 100 Stk EUR, ISIN-B: 50 Stk USD]
  │   └─ Depot 2: [ISIN-C: 200 Stk EUR]
  │
  ├─→ PriceService.getPreis(ISIN-A, EUR) → 45.50
  ├─→ PriceService.getPreis(ISIN-B, USD) → 120.00
  ├─→ PriceService.getDevisenkurs(USD, EUR) → 0.92
  ├─→ PriceService.getPreis(ISIN-C, EUR) → 88.30
  │
  └─ Ergebnis:
     Depot 1: 100*45.50 + 50*120*0.92 = 10.070 EUR
     Depot 2: 200*88.30 = 17.660 EUR
     GESAMT: 27.730 EUR
```
