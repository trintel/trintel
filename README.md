# Trintel

Laufende Instanz: [http://134.245.1.240:1200](http://134.245.1.240:1200)

## Implementierte Funktionen

Allgemein:
- erweiterbarer Sprachsupport
    - zur Zeit: Deutsch und Englisch
    - festgelegt durch Sprache im Browser und Aufenthaltsort

Administrator:
- Übersicht aller Transaktionen in der Datenbank
    - Status, Käufer, Verkäufer, Produkt und letztes Update
- Einsicht in den Verlauf jeder einzelnen Transaktion in der Datenbank
    - für jede zugehörige Aktion: Aktionstyp, auslösendes Unternehmen, auslösender Schüler, Datum, Zeit, Menge, Preis und Nachricht
- PDF-Export
    - für den ganzen Transaktionsverlauf
    - für einzelne Aktionen

### Admin Panel

Menüreiter “Admin Panel”

- **Einladungslink für Schüler*innen einsehen**
    
     Abschnitt “Einladungslink”
    
- **Spiel exportieren**
    
    WORKAROUND @LUCA
    
- **Spiel importieren**
    
    WORKAROUND @LUCA 
    
- **Aktionen einsehen**
    
    Abschnitt “Aktionen”
    
- **Aktion bearbeiten**
    
    auf “Bearbeiten” bei der jeweiligen Aktion klicken (nicht möglich bei Standardaktionen) ⇒ neue Seite öffnet sich
    
    1. Name bearbeiten
    2. Beschreibung bearbeiten
    3. Initiator bearbeiten
- **Neue Aktion hinzufügen**
    
    auf “Neue Aktion hinzufügen” klicken ⇒ neue Seite öffnet sich
    
    1. Name eingeben
    2. Beschreibung eingeben
    3. Initiator auswählen

### Unternehmensverwaltung

Menüreiter “Unternehmen”

- **Liste der Unternehmen einsehen**
- **Neues Unternehmen hinzufügen**
    
    auf “Unternehmen registrieren” klicken ⇒ neue Seite öffnet sich
    
    1. Unternehmensnamen eingeben
- **Unternehmen löschen**
    
    auf “Löschen” beim jeweiligen Unternehmen klicken ⇒ Löschen bestätigen
    
- **Unternehmensdetails einsehen**
    
    auf den Unternehmensnamen oder den nach rechts zeigenden Pfeil klicken
    
- **Unternehmen bearbeiten**
    
    Unternehmensdetails einsehen und auf “Bearbeiten” klicken
    
    1. Logo hochladen
    2. Name bearbeiten
    3. Signatur bearbeiten

### Schülerverwaltung

Menüreiter “Schüler*innen”

- Liste der Schüler einsehen
- Schüler suchen
    1. Namen in Suchfeld eingeben
    2. auf die Lupe klicken
- Schüler neu zuordnen
    
    auf “Neu zuordnen” bei der jeweiligen Person klicken ⇒ neue Seite öffnet sich
    
    1. Unternehmen auswählen
- Schülerunternehmen einsehen
    1. auf Unternehmen unterhalb des Personennamens klicken

Schüler:
- Erstellung neuer Transaktionen als Käufer
    - mit Auswahl eines Unternehmens als Handelspartner (Verkäufer)
- Durchführen von Transaktionen mit anderen Unternehmen auf der Handlsplattform
    - grundlegender Ablauf: Anfrage (durch Käufer) -> Angebot -> (Verkäufer) -> Annahme (Käufer) -> Lieferung/Rechnungsstellung (Verkäufer) -> Markieren als bezahlt (Verkäufer)
    - Verhandlungen möglich durch gegenseitiges Versenden von Angeboten bis das aktuellste davon angenommen wird
    - Berichtigungen von fehlerhaften Angeboten durch Versenden eines neuen Angebots durch das selbe Unternehmen möglich
- jederzeitige Nutzung der vom Administrator erstellten Freitext-Spezialaktionen
    - Auswahl verfügbar durch Button "Spezialaktionen"
- Übersicht aller Transaktionen, an denen das eigene Unternehmen beteiligt ist
    - Status, Käufer, Verkäufer, Produkt und letztes Update
- Einsicht in den Verlauf jeder einzelnen Transaktion in der Datenbank
    - für jede zugehörige Aktion: Aktionstyp, auslösendes Unternehmen, Datum, Zeit, Menge, Preis und Nachricht
- PDF-Export
    - für den ganzen Transaktionsverlauf
    - für einzelne Aktionen

### Eigenes Unternehmen einsehen

Menüreiter “Mein Unternehmen”

- **Unternehmen bearbeiten**
    
    Unternehmensdetails einsehen und auf “Bearbeiten” klicken
    
    1. Logo hochladen
    2. Name bearbeiten
    3. Signatur bearbeiten

**Anwendungsfälle im Pflichtenheft**

- Gesamtstatistik einsehen (Administrator): Implementiert
- Unternehmen erstellen (Administrator): Implementiert
- Registrierung (Administrator/Schüler): Implementiert
- Spiel exportieren (Administrator): Implementiert
- Spiel importieren (Administrator): Implementiert
- Transaktion durchführen (Schüler): Implementiert
- PDF zu Transaktionen generieren (Administrator/Schüler): Implementiert

## Automatische Tests

*Einzelne Beschreibungen der Tests entnehmen Sie bitte aus den Javadoc Kommentaren.*
Zum Ausführen der Tests: ./gradlew test

### CompanyTests

---

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

---

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

---

**Beschreibung: Testet die Funktionen des Backup Controller. Es wird überprüft ob ein Export und Import durchgeführt werden kann.**

- importExportTestAdmin()

### DatabaseTests

---

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

---

**Beschreibung: Testet, ob der HomeController existiert.**

- contextLoads()

### StatistikTests

---

**Beschreibung: Es wird getestet, ob die Statistik für Admin und User abrufbar sind.**

- listStatistikTestAdmin()
- listStatistikTestStudent1()
- listStatistikTestStudent2()
- listAdminStatistikTestAdmin()
- listAdminStatistikTestStudent()

### UserTests

---

**Beschreibung: Es wird getestet, ob sich Admins und User registrieren können, sowie nicht registrierte Nutzer ohne InviteLink keinen Zugriff haben. Außerdem, ob die Login/ Logout Feature funktioniert.**

- signUpRedirectTestStudent()
- signUpRedirectTestAdmin()
- signUpRedirectTestFalseString()
- loginAvailableForAll()
- adminLogout()
- studentLogout()
- testInvalidLoginDenied()
- testRedirectToLogin()

# Feature Statistic
**Ablauf:**

[Admin]:
Seitenleiste [Statistik] -> Gesamtübersicht -> sortierbar durch Klick auf Kategorie -> mehr Details durch Klick auf Unternehmen

[Schüler]:
Seitenleiste [Statistik] -> Unternehmensstatistiken -> sortierbar durch Klick auf Kategorie

**Beschreibung:**

Das geforderte Muss-Feature Statistik wurde sowohl für den Admin als auch den Schüler in der Seitenleiste unseres Programms unter dem 
Punkt Statistik integriert. Jeder Nutzer hat hierbei eine eigene Ansicht der Statistiken, sodass der Admin die Statistiken aller
existeirenden Unternehmen einsehen kann. Der Schüler hingegen hat nur die Einsicht in die Statistiken des eigenen Unternehmens,
um die Datensicherheit zu gerwährleisten.

Sobald der Admin die Statistikkategorie aus der Seitenleiste auswählt sieht er eine Tabelle, in welcher alle erstellten Unternehmen 
sichtbar sind. Hier werden nun die allgemeinen Daten angezeigt, um dem Admin eine Übersicht zu schaffen. Zusätzlich kann jede Spalte durch einen 
Klick auf die Kategorie auf- und absteigen sortiert werden, um die Unternehmen vergleichen zu können. Möchte der Admin nun detailiertere Daten zu 
jedem Unternehmen einsehen, kann er in der linken Spalte der Unternehmen ein Unternehmen anklicken und wird auf die entsprechenden 
Statisktikseiten weitergeleited.

Der Schüler hat auf seiner Staistikseite eine Übersicht über die Finanzen sowie Transaktionsstatistiken um das eigene Agieren auf dem Markt zu 
analysieren. Zusätzlich lässt sich auch hier die Tabelle der Transaktionbeziehungen auf- und absteigend sortieren. Allgemein ist auch hier von der
Security abgesichert, dass eine Schüler nicht durch Url-Raten Statistiken anderer Unternhemen einsehen kann, denn dann wird ein FORBIDDEN-ERROR erzeugt.

## Login Daten

Bei uns meldent man sich mit mail und pw an.
```
email: admin@admin
password: password
```

Als Admin kann man dann im Admin Panel die signup url einsehen mit der sich neue Schüler anmelden können. (Achtung, der ist nicht persistent, gilt nur für die aktuelle Laufzeit).
Zudem wir in den logs ein Admin signup link geprintet für weitere Admins.

## Deployment

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

---

Will man das Docker Image lokal bauen muss man beachten, dass die folgenden Directories gemoved werden:

```
mv build/libs/trintel-0.0.1-SNAPSHOT.jar target/app.jar
mv build/resources target/
```

Sodass sie vom Dockerfile gefunden werden können.


**Lokal testen ohne Docker**

Hier ist es am einfachsten das Repo zu clonen und die App mit `./gradlew bootRun` zu starten. Hier als Hinweis, wir nutzen die Java openjdk 11.
