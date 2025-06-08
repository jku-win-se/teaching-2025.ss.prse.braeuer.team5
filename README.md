# Lunchify

Lunchify ist eine Anwendung zur Erfassung, Verwaltung und Auswertung von Rechnungen (Einkäufe, Restaurant) in Unternehmen. Mitarbeiter können ihre Rechnungen digital hochladen, wobei ein automatischer OCR-Prozess (Texterkennung) relevante Informationen wie Datum, Betrag und Kategorie (z. B. Supermarkt oder Restaurant) extrahiert. Die hochgeladenen Daten werden überprüft und gespeichert, wobei nur eine Rechnung pro Tag erlaubt ist.

Administratoren haben Zugriff auf ein Dashboard, um Rechnungen zu genehmigen oder abzulehnen sowie Benutzer zu verwalten. Zudem stehen verschiedene Statistiken und Exportfunktionen (PDF, CSV, JSON) zur Verfügung, um Ausgaben pro Monat oder Kategorie auszuwerten.

Alle Daten werden sicher in einer PostgreSQL-Datenbank gespeichert, die über Supabase gehostet wird. Damit bietet Lunchify eine vollständige Lösung zur digitalen Belegverwaltung und Rückerstattung von Verpflegungskosten.

# Umgesetzte Anforderungen
Alle Anforderungen inklusive Verantwortlichkeiten findest du unter:  
https://github.com/jku-win-se/teaching-2025.ss.prse.braeuer.team5/issues?q=is%3Aissue%20state%3Aclosed

# Überblick über die Applikation aus Benutzersicht

[Benutzerdokumentation](https://github.com/jku-win-se/teaching-2025.ss.prse.braeuer.team5/blob/main/docs/benutzerdoku.md).

# Übersicht über die Applikation aus Entwicklersicht

[Systemdokumentation](https://github.com/jku-win-se/teaching-2025.ss.prse.braeuer.team5/blob/main/docs/systemdoku.md).

## Entwurf
[Lunchify - UML-Diagramm](https://github.com/jku-win-se/teaching-2025.ss.prse.braeuer.team5/blob/main/UML/LunchifyUML.md).

### Überblick über die Applikation
Lunchify basiert auf einer JavaFX-Anwendung mit Anbindung an eine PostgreSQL-Datenbank über Supabase. Die Architektur folgt dem **Model-View-Controller (MVC)**-Muster.

### Wichtige Design Entscheidungen

#### Entscheidung 1: Einsatz von Supabase (statt lokalem DB-Server)  
**Begründung:** Einfache Cloud-Integration und Verwaltung über Web-UI, Empfehlung von Kollegen
**Alternativen:** Lokaler PostgreSQL-Server
**Annahmen:** Internetzugang steht zur Verfügung  
**Konsequenzen:** Hohe Verfügbarkeit, einfache Zusammenarbeit im Team, keine lokale Datenbanken 

#### Entscheidung 2: JavaFX als UI-Technologie  
**Begründung:** Desktop-App, leichtgewichtig, gute Integration mit Java  
**Alternativen:** 
**Annahmen:** Installation lokal möglich  
**Konsequenzen:** Geringere Einstiegshürde für Benutzer, keine Deployment-Infrastruktur nötig

#### Entscheidung 3: Scenebuilder für Prototype Erstellung und GUI  
**Begründung:** Desktop-App, vereinfacht Codeproduktion
**Alternativen:** Figma 
**Annahmen:** Installation lokal möglich  
**Konsequenzen:** schwer verständlich, anschließend einfache Integration in den Code

## Implementierung

## Code Qualität

## Testen
[Testplan](https://github.com/jku-win-se/teaching-2025.ss.prse.braeuer.team5/blob/main/docs/Testplan).

Testabdeckung:
--noch Foto einfügen

# JavaDoc für wichtige Klassen, Interfaces und Methoden


# Installationsanleitung

#### Schritt 1: Java installieren
Um Lunchify verwenden zu können ist es erforderlich Zugriff auf eine Entwicklungsumgebung zu haben. Falls diese nocht nicht installiert wurde bitte herunterladen: 
https://www.oracle.com/at/java/technologies/downloads/#java21

#### Schritt 2: JavaFX SDK installieren
Anschließend sollte auch überprüft werden, ob ein Zugriff auf JavaFX SDK besteht. Ansonsten muss dieses Tool auch heruntergeladen werden (Bitte die passende Version zu der Entwicklungsumgebung wählen):
https://gluonhq.com/products/javafx/

#### Schritt 3: Lunchify-Installationspaket vorbereiten
Sie haben eine Zip-Datei mit der Lunchify erhalten. Bitte herunterladen und entpacken. Bitte öffnen sie die Datei "Lunchify-0.0.1.beta" mit Doppelklick. 

Falls dieser Vorgang nicht durchgeführt werden kann, öffnen Sie den Ordner, in dem sie die JavaFX SDK entpackt haben. Kopieren Sie den Pfad zum Ordner "lib". Öffnen sie die Datei "start-lunchify.bat" per Rechtsklick und klicken sie auf "bearbeiten". Ersetzen sie in der Datei den Pfad "PATH_TO_FX" mit ihrem Link. Speichern und schließen sie die Datei.
Danach können sie die Datei mit Doppelklick öffnen und Lunchify starten.

### Wichtige Unterlagen: 

[docs](https://github.com/jku-win-se/teaching-2025.ss.prse.braeuer.team5/tree/e76f232010a10e7386eb4bc3df6dd038941aa3dd/docs)
