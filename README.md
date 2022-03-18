# Trintel

**Implementierte Funktionen**

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

**Anwendungsfälle im Pflichtenheft**

- Gesamtstatistik einsehen (Administrator): Implementiert
- Unternehmen erstellen (Administrator): Implementiert
- Registrierung (Administrator/Schüler): Implementiert
- Spiel exportieren (Administrator): Implementiert
- Spiel importieren (Administrator): Implementiert
- Transaktion durchführen (Schüler): Implementiert
- PDF zu Transaktionen generieren (Administrator/Schüler): Implementiert
