# Trintel

Laufende Instanz: [http://134.245.1.240:1200](http://134.245.1.240:1200)

  - [Login Daten](#login-daten)
  - [Deployment](#deployment)
  - [Implementierte Funktionen](#implementierte-funktionen)
    - [Allgemein](#allgemein)
      - [Erweiterbarer Sprachsupport](#erweiterbarer-sprachsupport)
    - [Administrator](#administrator)
      - [Transaktionen](#transaktionen)
      - [PDF-Export](#pdf-export)
      - [Admin Panel](#admin-panel)
      - [Unternehmensverwaltung](#unternehmensverwaltung)
      - [Schülerverwaltung](#schülerverwaltung)
      - [Admin Statistik](#admin-statistik)
    - [Schüler](#schüler)
      - [Transaktionen](#transaktionen)
      - [PDF-Export](#pdf-export)
      - [Eigenes Unternehmen einsehen](#eigenes-unternehmen-einsehen)
      - [Schüler Statistik ](#schüler-statistik-)
  - [Anwendungsfälle im Pflichtenheft](#anwendungsfälle-im-pflichtenheft)
  - [Automatische Tests](#automatische-tests)
      - [CompanyTests](#companytests)
      - [TransactionTests](#transactiontests)
      - [BackUpTests](#backuptests)
      - [DatabaseTests](#databasetests)
      - [HomeTest](#hometest)
      - [StatistikTests](#statistiktests)
      - [UserTests](#usertests)


### Login Daten

Bei uns meldent man sich mit mail und pw an.
```
email: admin@admin
password: password
```

Als Admin kann man dann im Admin Panel die signup url einsehen mit der sich neue Schüler anmelden können. (Achtung, der ist nicht persistent, gilt nur für die aktuelle Laufzeit).
Zudem wir in den logs ein Admin signup link geprintet für weitere Admins.

### Deployment

In der deploy Pipeline wird ein Docker Image erstellt, das wir verwenden können.
Wir laden das Docker Image und führen es aus. Das wird hier im folgenden erklärt.

Aufgrund der max Artifact size bei GitLab wird das hier gleich ein bisschen sketchy.

1. In GitLab in den CI Tab gehen die letzte deploy Pipeline raussuchen. [Link zur Pipeline übersicht auf deploy Branch](https://git.informatik.uni-kiel.de/sopro/lms8_eg_017/softwareprojekt/-/pipelines?page=1&scope=all&ref=deploy)
2. Bei den Stages der letzten Pipeline die Stage `build-docker` anklicken und ihre logs anzeigen lassen.
3. In den logs einen Link suchen, der in etwa so aussieht: `https://transfer.sh/XXXXXX/trintel-image.tar`. (IdR. Zeile 66)

Nun die folgenden  Commands ausführen.

```sh
wget https://transfer.sh/XXXXXX/trintel-image.tar` # der Link aus den Logs.
docker load -i trintel-image.tar
docker run -d -p 8080:8080 --name trintel-container trintel
```

Jetzt läuft die App in Container und ist über Port 8080 erreichbar.

Will man das Docker Image lokal bauen muss man beachten, dass die folgenden Directories gemoved werden:

```
mv build/libs/trintel-0.0.1-SNAPSHOT.jar target/app.jar
mv build/resources target/
```

Sodass sie vom Dockerfile gefunden werden können.


**Lokal testen ohne Docker**

Hier ist es am einfachsten das Repo zu clonen und die App mit `./gradlew bootRun` zu starten. Hier als Hinweis, wir nutzen die Java openjdk 11.

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
   
    Als Admin die folgende URL aufrufen: `localhost:8080/backup/export`. Die Export Datei ist dann im Docker Container im top Directory zu finden.
    
- **Spiel importieren**
    
    Hier muss die zu importierende Datei im top Directory des Docker Containers sein. Dann ein POST request mit path Feld wie im Folgenden als Beispiel gezeigt:
    ```
    curl -X POST -F 'path=export.json' http://localhost:8080/backup/import
    ```  
    
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
