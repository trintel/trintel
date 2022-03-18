# Implementierte Funktionen

## Allgemein
### Erweiterbarer Sprachsupport

- zur Zeit: Deutsch und Englisch

- festgelegt durch Sprache im Browser und Aufenthaltsort

## Administrator

### Transaktionen
Menüreiter "Transaktionen"
- **Übersicht aller Transaktionen in der Datenbank**
    - Angaben: Status, Käufer, Verkäufer, Produkt und letztes Update
- **Einsicht in den Verlauf jeder einzelnen Transaktion in der Datenbank**
    - Angaben für jede zugehörige Aktion: Aktionstyp, auslösendes Unternehmen, auslösender Schüler, Datum, Zeit, Menge, Preis und Nachricht
### PDF-Export

- für den ganzen Transaktionsverlauf

- für einzelne Aktionen

### Admin Panel

Menüreiter “Admin Panel”

- **Einladungslink für Schüler*innen einsehen**
    
    - Abschnitt “Einladungslink”

    
- **Spiel exportieren**
    
    - WORKAROUND @LUCA
    
- **Spiel importieren**
    
    - WORKAROUND @LUCA 
    
- **Aktionen einsehen**
    
    - Abschnitt “Aktionen”
    
- **Aktion bearbeiten**
    
    - auf “Bearbeiten” bei der jeweiligen Aktion klicken (nicht möglich bei Standardaktionen) ⇒ neue Seite öffnet sich
    - Name bearbeiten
    - Beschreibung bearbeiten
    - Initiator bearbeiten
- **Neue Aktion hinzufügen**
    
    - auf “Neue Aktion hinzufügen” klicken ⇒ neue Seite öffnet sich
    - Name eingeben
    - Beschreibung eingeben
    - Initiator auswählen

### Unternehmensverwaltung

Menüreiter “Unternehmen”

- **Liste der Unternehmen einsehen**
- **Neues Unternehmen hinzufügen**
    
    - auf “Unternehmen registrieren” klicken ⇒ neue Seite öffnet sich
    
    - Unternehmensnamen eingeben
- **Unternehmen löschen**
    
    - auf “Löschen” beim jeweiligen Unternehmen klicken ⇒ Löschen bestätigen
    
- **Unternehmensdetails einsehen**
    
    - auf den Unternehmensnamen oder den nach rechts zeigenden Pfeil klicken
    
- **Unternehmen bearbeiten**
    
    - Unternehmensdetails einsehen und auf “Bearbeiten” klicken
    - Logo hochladen
    - Name bearbeiten
    - Signatur bearbeiten

### Schülerverwaltung

Menüreiter “Schüler*innen”

- Liste der Schüler einsehen
- Schüler suchen
    - Namen in Suchfeld eingeben
    - auf die Lupe klicken
- Schüler neu zuordnen
    
    - auf “Neu zuordnen” bei der jeweiligen Person klicken ⇒ neue Seite öffnet sich
    - Unternehmen auswählen
- Schülerunternehmen einsehen
    - auf Unternehmen unterhalb des Personennamens klicken

### Admin Statistik

Menüreiter "Statistik"
- Gesamtübersicht
- sortierbar durch Klick auf Kategorie
- mehr Details durch Klick auf Unternehmen
## Schüler

### Transaktionen

Menüreiter "Transaktionen"

- **Erstellung neuer Transaktionen als Käufer**

    -  mit Auswahl eines Unternehmens als Handelspartner (Verkäufer)

- **grundlegender Ablauf einer erfolgreich abgeschlossenen Transaktion**

    - Anfrage (durch Käufer)
    - Angebot (durch Verkäufer)
    - Annahme (durch Käufer)
    - Lieferung/Rechnungsstellung (durch Verkäufer)
    - Als bezahlt markieren (durch Verkäufer)

- **Verhandlungen**

    - gegenseitiges Versenden von Angeboten von beiden Seiten
    - Annahme des aktuellsten Angebots durch Handelspartner

- **Berichtigungen von fehlerhaften Angeboten (bei z.B. Tippfehlern bei Menge oder Preis)**

    - Versenden eines neuen Angebots durch das selbe Unternehmen möglich

- **Nutzung der vom Administrator erstellten Freitext-Spezialaktionen**
    - Auswahl verfügbar durch Button "Spezialaktionen"
    - jederzeit im Transaktionablauf möglich

- **Abbruch einer Transaktion**

    - durch Käufer: nach Stellen einer Anfrage (bei z.B. falsch ausgewähltem Unternehmen) oder endgültiges Ablehnen von Angeboten
    - durch Verkäufer: nach Eingang einer Anfrage (falls Produkt nicht angeboten wird oder nicht mehr vorrätig) oder edngültiges Ablehnen von Angeboten

- **Übersicht aller Transaktionen, an denen das eigene Unternehmen beteiligt ist**

    - Angaben: Status, Käufer, Verkäufer, Produkt und letztes Update

- **Einsicht in den Verlauf jeder einzelnen Transaktion in der Datenbank**

    - Angaben für jede zugehörige Aktion: Aktionstyp, auslösendes Unternehmen, Datum, Zeit, Menge, Preis und Nachricht

### PDF-Export

- **für den ganzen Transaktionsverlauf**

- **für einzelne Aktionen**

### Eigenes Unternehmen einsehen

Menüreiter “Mein Unternehmen”

- **Unternehmen bearbeiten**
    
    - auf “Bearbeiten” klicken
    - Logo hochladen
    - Name bearbeiten
    - Signatur bearbeiten

### Schüler Statistik 

Menüreiter "Statistik"
- Unternehmensstatistiken
- Tabelle sortierbar durch Klick auf Kategorie
---
# Anwendungsfälle im Pflichtenheft

- Gesamtstatistik einsehen (Administrator): Implementiert
- Unternehmen erstellen (Administrator): Implementiert
- Registrierung (Administrator/Schüler): Implementiert
- Spiel exportieren (Administrator): Implementiert
- Spiel importieren (Administrator): Implementiert
- Transaktion durchführen (Schüler): Implementiert
- PDF zu Transaktionen generieren (Administrator/Schüler): Implementiert

---

# Automatische Tests

*Einzelne Beschreibungen der Tests entnehmen Sie bitte aus den Javadoc Kommentaren.*
Zum Ausführen der Tests: ./gradlew test

### CompanyTests

**Beschreibung: Testet die Funktionen des Company Controller aus Studenten und Admin Sicht. Dabei wird geprüft, ob die Mapping entsprechend der Rechtevergabe erreichbar sind und Aktionen ausgeführt werden können. Dafür sind auch Intergrationstest, die die Datenbank mit einbeziehen, beinhaltet.**

- companiesReachable()
- listCompaniesTestAdmin()
- listCompaniesTestStudent()
- addCompanieTestAdmin()
- addCompanieTestStudent()
- saveCompanieTestAdmin()
- saveCompanieTestStudent()
- listAllStudentsTestAdmin()
- listAllStudentsTestStudent()
- editReassignStudentTestAdmin()
- editReassignStudentTestStudent()
- moveToCompanyTestAdmin()
- moveToCompanyTestStudent()
- companySelectTestAdmin()
- companySelectTestAssignedStudent()
- companySelectTestStudent()
- companySelectIdTestAdmin()
- companySelectIdTestStudent()
- joinCompany2TestAdmin()
- joinCompany2TestStudent()
- editOwnCompanyTestAdmin()
- editOwnCompanyStudentTest()
- saveOwnCompanyTestAdmin()
- saveOwnCompanyTestStudent()
- adminAddCompany()
- adminDeleteCompany()

### TransactionTests

**Beschreibung: Testet die Funktionen des Transaction und Action Type Controller aus Studenten und Admin Sicht. Dabei wird geprüft, ob die Mapping entsprechend der Rechtevergabe erreichbar sind und Aktionen ausgeführt werden können. Dafür sind auch Intergrationstest, die die Datenbank mit einbeziehen, beinhaltet.**

- listTransactionsTestAdmin()
- listTransactionsTestStudent()
- createTransactionsTestAdmin()
- createTransactionsTestStudent1()
- createTransactionsTestStudent2()
- saveTransactionsTestStudent()
- saveTransactionsTestAdmin()
- transactionDetailTestStudent()
- transactionDetailTestAdmin()
- createActionTestStudent()
- createActionTestAdmin()
- addActionTypeTestStudent()
- addActionTypeTestAdmin()
- saveActionTypeTestStudent()
- editExistingActionTypeTestStudent()
- editExistingActionTypeTestAdmin()
- editNewActionTypeTestStudent()
- editNewActionTypeTestAdmin()
- saveEditActionTypeTestStudent()

### BackUpTests

**Beschreibung: Testet die Funktionen des Backup Controller. Es wird überprüft ob ein Export und Import durchgeführt werden kann.**

- importExportTestAdmin()

### DatabaseTests

**Beschreibung: Testet, ob Objekte, die erstellt und in der Datenbank gespeichert werden, auch tatsächlich gesichert werden und abrufbar sind.**

- isUserInDatabase()
- isUserInCompany()
- isCompanyInDatabase()
- isTransactionInDatabase()
- isTransactionInDatabaseRelated()
- isConfirmedSetInDatabase()
- isPaidSetInDatabase()
- isShippedSetInDatabase()
- isActionTypeSavedInDatabase()HomeTest
- isActionSavedInDatabase()

### HomeTest

**Beschreibung: Testet, ob der HomeController existiert.**

- contextLoads()

### StatistikTests

**Beschreibung: Es wird getestet, ob die Statistik für Admin und User abrufbar sind.**

- listStatistikTestAdmin()
- listStatistikTestStudent1()
- listStatistikTestStudent2()
- listAdminStatistikTestAdmin()
- listAdminStatistikTestStudent()

### UserTests

**Beschreibung: Es wird getestet, ob sich Admins und User registrieren können, sowie nicht registrierte Nutzer ohne InviteLink keinen Zugriff haben. Außerdem, ob die Login/ Logout Feature funktioniert.**

- signUpRedirectTestStudent()
- signUpRedirectTestAdmin()
- signUpRedirectTestFalseString()
- loginAvailableForAll()
- adminLogout()
- studentLogout()
- testInvalidLoginDenied()
- testRedirectToLogin()
