
<p align="center">
<img src="https://user-images.githubusercontent.com/47346598/221367348-29db07e9-33c1-4a5e-93d8-5e2aee32282b.jpeg" width="30%" >
</p>



# Trintel

- [Trintel](#trintel)
    - [Login Daten](#login-daten)
    - [Deployment](#deployment)
      - [Docker Image bauen](#docker-image-bauen)
      - [Docker Image ausführen](#docker-image-ausführen)
      - [Starten mit Docker-Compose](#starten-mit-docker-compose)
      - [Lokal testen ohne Docker](#lokal-testen-ohne-docker)
      - [Konfiguration](#konfiguration)
- [Implementierte Funktionen](#implementierte-funktionen)
  - [Allgemein](#allgemein)
    - [Erweiterbarer Sprachsupport](#erweiterbarer-sprachsupport)
    - [Login und Registrierung](#login-und-registrierung)
  - [Administrator](#administrator)
    - [Transaktionen](#transaktionen)
    - [PDF-Export](#pdf-export)
    - [Admin Panel](#admin-panel)
    - [Unternehmensverwaltung](#unternehmensverwaltung)
    - [Schülerverwaltung](#schülerverwaltung)
    - [Admin Statistik](#admin-statistik)
  - [Schüler](#schüler)
    - [Transaktionen](#transaktionen-1)
    - [PDF-Export](#pdf-export-1)
    - [Eigenes Unternehmen einsehen](#eigenes-unternehmen-einsehen)
    - [Schüler Statistik](#schüler-statistik)


### Login Daten

Beim start des Programms werden zwei URLs in den logs ausgegeben. Diese URLs können genutzt werden, um um sich zu registrieren.
```log
2023-02-07 10:58:57.989  INFO 1 --- [           main] sopro.TrintelApplication                 : The registration URL for ADMINS is: /signup/3e60eb52-f2c0-4f3d-9d50-ce63e578714b
2023-02-07 10:58:57.991  INFO 1 --- [           main] sopro.TrintelApplication                 : The registration URL for STUDENTS is: /signup/3bb82933-55b8-4975-b631-3ef322e39dc9
```

Als Administrator kann man dann im Admin Panel die Registrierungs-URL einsehen, mit der sich neue Schüler anmelden können (Achtung! Diese URL ist nicht persistent, gilt also nur für die aktuelle Laufzeit).
Zudem wird in der Konsole ein Link für den Registrierung für Admins geprintet, um dem Spiel weitere Admins hinzu zu fügen.

### Deployment

#### Docker Image bauen

In der Deploy-Pipeline wird ein Docker Image erstellt und nach Docker-Hub gepusht, das wir verwenden können.
Man kann das Image auch lokal bauen mit dem Befehl:
```sh
docker build -t trintel:1.0.0
```
Wichtig ist, dass die jars vorhanden sind. Das Projekt muss vorher also einmal mit Gradle gebaut werden.
Das Docker File ist so geschrieben, dass das gesamte `/build` Verzeichnis ins Image kopiert wird. Der Entrypoint ist aber Fest auf /`build/libs/trintel-0.0.1-SNAPSHOT.jar` gesetzt. Dies ist wichtig, falls die Version in der `build.gradle` angepasst wird. (Versionierung haben wir über Docker gemanaged und nicht über Gradle)

#### Docker Image ausführen
Wir laden das Docker Image und führen es aus. Dieses Vorgehen wird hier im Folgenden erklärt.

Nun müssen die folgenden Commands ausgeführt werden:

```sh
docker pull b3zz/trintel
docker run -d -p 8080:8080 --name trintel-container b3zz/trintel
```

Jetzt läuft die App im Container und ist über den Port 8080 erreichbar.

#### Starten mit Docker-Compose

Für den produktiven Deploy nutzen wir `docker-compose`. Außerdem nutzen wir für die Loganalyse den `Elastic Stack`.
Dieser besteht in unserer Konfiguration aus zwei Teilen.

- `Filebeat` läuft parallel zu der Trintel Instanz/ den Instanzen und sammelt alle logs der Docker Container ein.
Diese werden dann an einen anderen Server geschickt. Dies ist Konfiguriert in der `elastic-stack/filebeat.yml`.
 - `Logstash` läuft dann auf einem anderen Server, welcher die Logs empfängt, parsed und in einer ES-DB speichert.
Dieser Teil kann mit der `elastic-stack/docker-compose.yml` gestartet werden.

**Achtung!**: Viele Pfade sind relativ angegeben. Daher sollte docker-compose immer aus dem Ordner, in welchem die docker-compose.yml liegt aufgerufen werden.

```sh
docker-compose up -d
```
#### Lokal testen ohne Docker

Hier ist es am einfachsten, das Repository zu clonen und die App mit `./gradlew bootRun` zu starten. Hier als Hinweis: Wir nutzen die Java openjdk 11.

Zum einfacheren Testen haben wir eine spezielle Init Methode für die Datenbank geschrieben, welche die Datenbank initial mit ein paar Dummy Daten befüllt. Dies passiert, wenn das Spring Profile "dev" gesetzt ist. 

```sh
./gradlew bootRun --args='--spring.profiles.active=dev'
```
Eine bestehende Datenbank wird bei erneutem Start des Programms nicht überschrieben.

Wichtig sind hier vor allem die Konfiguration der Mailserver, da das Programm ansonsten nicht Kompiliert.
```properties
spring.mail.host=mailin.informatik.uni-kiel.de
spring.mail.port=587
spring.mail.username= #stu-Kennung
spring.mail.password= #stu-Passwort
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```
#### Konfiguration

Alle Werte in den `.properties` Dateien sind im Prinzip nur Defaults und können durch das setzen von Umgebungsvariablen überschrieben werden.
Dies nutzen wir mit `Docker` bzw. `docker-compose` aus.

Insbesondere die Konfiguration des Mail-Servers passiert erst in der `docker-compose.yml`.
Zum lokalen testen ohne Docker sollten die entsprechenden Werte in der `application-dev.properties` gesetzt werden.


# Implementierte Funktionen

## Allgemein

### Erweiterbarer Sprachsupport

- zur Zeit: Deutsch und Englisch

- festgelegt durch Sprache im Browser und Aufenthaltsort

### Login und Registrierung

- **Registrierung über einen Registrierungslink**

    - eigene Links jeweils für Schüler und Administratoren
    - Registrierungsbestätigung über einen Link, der per E-Mail an den Nutzer geschickt wird


## Administrator

### Transaktionen

Menüreiter "Transaktionen"

- **Übersicht aller Transaktionen in der Datenbank**

    - Angaben: Status, Käufer, Verkäufer, Produkt und letztes Update

- **Einsicht in den Verlauf jeder einzelnen Transaktion in der Datenbank**

    - Angaben für jede zugehörige Aktion: Aktionstyp, auslösendes Unternehmen, auslösender Schüler, Datum, Zeit, Menge, Preis und Nachricht

### PDF-Export

- **für den ganzen Transaktionsverlauf**

- **für einzelne Aktionen**

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