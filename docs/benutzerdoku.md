# Benutzerdokumentation "Lunchify" Team 5

## 1. Was ist Lunchify?
Lunchify dient zur Weitergabe von Rechnungen an die Personalabteilung. In weiterer Folge kann auf Basis dieser, die Rückvergütung berechnet werden und sie wird anschließend mit der Gehaltsabrechnung rückerstattet. Dieses Programm ist in einer Desktopversion verfügbar und wird in den folgenden Abschnitten präzise erläutert. 

Das Programm ist grundsätzlich in zwei Teile geteilt. Der erste Teil ist ausschließlich für Administratoren zugänglich, dieser umgreift das Management der User, der Rechnungen und die Übersicht der erforderlichen Statistiken. Der zweite Teil ist für Administratoren und User bestimmt, welcher auch zur Einreichung und Bearbeitung der eigenen Rechnungen dient. 

### 1.1. Installationsanleitung
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

## 2. USER
### 2.1. Login
![image](https://github.com/user-attachments/assets/ba5dce5a-76cb-4253-8952-5e64d328d70c)

Wenn sie das Programm öffnen, finden sie eine Anmeldeseite. Bitte verwenden sie als User den linken Login (1). Ihre Zugangsdaten erhalten sie von der Personalabteilung oder von einem Administrator. In das erste Feld “E-Mail” (2) fügen sie die zugewiesene E-Mail-Adresse ein und bei dem zweiten Feld “Password” (3) geben sie bitte ihr Passwort ein. Anschließend drücken sie auf Login (4).  

Falls die Anmeldung nicht erfolgreich war, versuchen sie diesen Vorgang erneut. Bei Aufscheinen einer weiteren Fehlermeldung, melden sie sich bitte bei dem Administrator. 

Wenn sie das System einfach schließen möchten, klicken sie auf das X (5) oben rechts.  

### 2.2. Benutzer-Dashboard
![image](https://github.com/user-attachments/assets/c5e16e90-8b24-4b6f-8491-4025f4c9e4ef)

Wenn die Anmeldung erfolgreich war, befinden sie sich im Benutzer-Dashboard. In der linken Spalte haben sie vier Buttons, wo sie zwischen Edit invoice (1), Upload invoice (2), Logout (3) und einem Größer-Button (4) wählen können. Die Grafik die sie in der Mitte finden zeigt die Verteilung ihrer Rechnungen zwischen den Kategorien „Restaurant“ und „Supermarket“ an. Die Tabelle unten listet ihnen ihre bereits eingereichten Rechnungen auf. Sie erhalten einen Überblick über das Einreichungsdatum, Rechnungsbetrag, Kategorie (Supermarket, Restaurant), Status (approved, processing, declined) und Rückerstattung. Sie können durch die Tabelle scrollen, indem sie den Balken rechts (5) nach oben oder unten ziehen. Da sie vorerst noch keine Rechnungen hochgeladen haben, werden die Grafik und die Tabelle aktuell noch leer sein. 

Ist eine ihrer Rechnungen fehlerhaft oder haben sie eine Rückmeldung vom Administrator zu dieser Rechnung erhalten klicken sie bitte auf Edit invoice (1). Genaueres wird im Punkt 2.3. erklärt. 
Falls sie eine Rechnung hochladen möchten, klicken sie bitte auf Upload invoice (2). Die genauen Details dazu werden im Punkt 2.4. erklärt. 
Wenn sie sich wieder vom System abmelden wollen, klicken sie bitte auf Logout (3). 
Wenn Sie die Liste größer machen wollen, um alle Rechnungen auf einen Blick zu sehen, drücken Sie bitte auf den Größer-Button (4).

### 2.3. Rechnung bearbeiten
![image](https://github.com/user-attachments/assets/f7920f7e-f252-4fdb-b7b9-f33785f7f94d)

Nachdem sie erfolgreich weitergeleitet wurden vom Benutzer-Dashboard zu Edit invoice, können sie hier ihre bereits eingereichten Rechnungen bearbeiten.  

Zuerst wählen sie bei Select invoice (1) die zu bearbeitende Rechnung aus. Hier wird vorab die Anzahl der abgelehnten Rechnungen angezeigt. Wenn sie auf das Drop-Down-Menü klicken, werden ihnen alle Rechnungen nach Datum aufgelistet. Sie können ihre Rechnung auch nur per Datum auswählen. Anschließend werden die Werte der Rechnung automatisch in den einzelnen Felder eingefügt.  

Wenn sie in das Kästchen neben Amount (2) klicken können sie den Rechnungsbetrag ändern (Achtung: Geldbeträge immer mit Punkt eingeben z.B. 2.99).  

Falls das Rechnungsdatum falsch gewählt wurde und sie es ändern möchten, klicken sie bitte auf das Kalendersymbol (4) neben Invoice date (3). Es scheint ihnen ein Kalender auf, bitte wählen sie das richtige Datum (Achtung: Sie dürfen nur Rechnungen im selben Monat ändern! Sie dürfen nur eine Rechnung am Tag und auch nur wochentags (Mo-Fr) hochladen!). 

Die Kategorie ihrer Rechnung können sie ändern, indem sie auf das Drop-Down-Menü neben Category (5) klicken. Hier können sie zwischen Supermarket und Restaurant wählen. Je nach Kategorie und auch Rechnungsbetrag erhalten sie einen anderen Rückerstattungsbetrag. 

Wenn sie schlussendlich auf Change (6) klicken, wird die Rechnung geändert und aktualisiert. Sie erhalten zusätzlich eine positive Meldung. Bei einer Fehlermeldung überprüfen sie bitte ihre Eingabedaten und klicken sie erneut auf Change (6).  

Haben sie die Rechnung erfolgreich geändert und wollen die Seite wieder verlassen wählen sie Exit (7).  

Wenn sie die Rechnung doch nicht ändern wollen, klicken sie auf Exit (7), ohne vorher Change (6) zu wählen. 

### 2.4. Rechnung hochladen
![image](https://github.com/user-attachments/assets/a232a8ce-8dbe-495c-906b-4ad4337d5af1)

Nachdem sie erfolgreich weitergeleitet wurden vom Benutzer-Dashboard zu Add Invoice, können sie hier ihre neuen Rechnungen hochladen.  

Das Rechnungsdatum, welches auf ihrer Rechnung angegeben ist, geben sie bitte im ersten Feld Invoice date (1) ein. Klicken sie auf das Kalendersymbol 2) und wählen sie anschließend das Datum aus. (Achtung: Sie dürfen nur Rechnungen im selben Monat ändern! Sie dürfen nur eine Rechnung am Tag und auch nur wochentags (Mo-Fr) hochladen!). 

Geben sie ihren Rechnungsbetrag bei Amount (3) ein (Achtung: Geldbeträge immer mit Punkt eingeben z.B. 2.99).  

Anschließend können sie bei Category (4) auf das Drop-Down-Menü klicken, hier können sie zwischen Supermarket oder Restaurant wählen. Je nach Kategorie und auch Rechnungsbetrag erhalten sie einen anderen Rückerstattungsbetrag. 

Sie müssen ihre Rechnung auch hochladen (Achtung: nur in folgenden Formaten möglich: JPEG, PNG und PDF). Klicken sie dazu auf Select Image (5), wählen sie die richtige Datei aus und wählen sie anschließen Öffnen. Sie erhalten hier eine Rückmeldung, ob der Upload erfolgreich war.  

Beachten sie, dass sie alle Felder befüllt haben, ansonsten können sie die Rechnung nicht hochladen. Ist dies der Fall, wählen sie Upload (6). Falls sie eine Fehlermeldung erhalten, überprüfen sie alle eingegebenen Daten und versuchen sie es erneut.  

Um den Rechnungs-Upload abzubrechen oder nach erfolgreichem Upload klicken sie bitte auf Exit (7), um wieder zurück auf das User-Dashboard zu gelangen.  

## 3. Administrator
### 3.1. Login
![image](https://github.com/user-attachments/assets/8db1beb7-2826-4cf4-bc8f-28ba0d18c739)

Wenn sie das Programm öffnen, finden sie eine Anmeldeseite. Bitte verwenden sie als Admin den rechten Login (1). Ihre Zugangsdaten erhalten sie von der Personalabteilung oder von einem anderen Administrator. In das erste Feld E-Mail (2) fügen sie bitte die zugewiesene E-Mail-Adresse ein und bei dem zweiten Feld Password (3) geben sie bitte ihr Passwort ein. Anschließend drücken sie auf Login (4).  

Falls die Anmeldung nicht erfolgreich war, versuchen sie diesen Vorgang erneut. Bei Aufscheinen einer weiteren Fehlermeldung, melden sie sich bitte bei dem Administrator. 

### 3.2. Admin-Dashboard
![image](https://github.com/user-attachments/assets/18572787-5f3e-4843-93f7-a51578d6348b)

Als Administrator gelangen sie nach erfolgreicher Anmeldung weiter auf das Admin-Dashboard. In der linken Spalte sehen sie sechs Buttons zur Auswahl. In der Mitte findet sich eine Übersicht mit allen Rechnungen, die eingereicht wurden. Sie können die Rechnung inklusive User, Einreichungsdatum, Rechnungsbetrag, Kategorie, Status und Rückerstattung einsehen.  

Weiters können sie auch Rechnungen von Mitarbeitern bearbeiten, dazu klicken sie auf Edit invoice (1). Die genaue Durchführung wird in 3.3. beschrieben 

Falls sie Statistiken über die eingereichten Rechnungen und Rückzahlungen sehen wollen, klicken sie bitte auf Statistics (2). Genaueres erfahren sie in Punkt 3.4. 

Wollen sie einen weiteren User oder Administrator anlegen, dazu wählen sie Add Admin/User (3), wie sie weiter vorgehen müssen, erfahren sie in Punkt 3.5. 

Nach Ausscheiden im Unternehmen können sie User und Administratoren auch wieder entfernen. Hierzu klicken sie bitte auf Delete Admin/User (4). Die genauen Schritte erfahren sie in Punkt 3.6. 

Sie können auch die Rückzahlungen je nach Kategorie automatisch ändern. Dazu klicken sie bitte auf Change Reimbursement (5). Den Ablauf erfahren sie in Punkt 3.7. 

Wenn sie sich wieder abmelden möchten, drücken sie bitte einfach auf Logout (6). 

### 3.3. Rechnung bearbeiten - Admin
![image](https://github.com/user-attachments/assets/f5a75025-4a75-41cb-9da3-aead9d62cfd3)

Wenn Sie Edit Invoice im Admin-Dashboard ausgewählt haben, bekommen sie die Möglichkeit als Administrator, Rechnungen von Ihnen zugeteilten Users zu bearbeiten, akzeptieren und ablehnen. Um einen User, dessen Rechnung bearbeitet werden soll, auszuwählen, klicken Sie die Drop-Down-Liste neben Select User (1). Sie können nun einen User auswählen, der bis zu diesem Zeitpunkt eine Rechnung hochgeladen hat. Folgend können Sie eine spezifische Rechnung anhand des Datums im Select Invoice (2) Drop-Down wählen. 
Es werden alle Daten der Rechnung automatisch in die restlichen Felder geladen. Nun können Sie, falls nötig, die Daten in den Feldern Amount (3), Invoice Date (4) mit dem Kalendersymbol (5) oder Category (6) in SUPERMARKET oder RESTAURANT ändern. 

Nachdem die Änderungen erfolgt sind drücken Sie auf den Button Change (8). Es erscheint eine Bestätigung nach erfolgter und eine Error-Nachricht nach fehlgeschlagener Änderung. Wenn dieser Schritt erledigt wurde, können Sie nun die Rechnung akzeptieren indem Sie auf Accept (9) klicken.

Falls keine Änderungen bei der Rechnung notwendig sind, können Sie den User (1) sowie die Rechnung (2) auswählen und ohne weitere Änderungen akzeptieren (9).

Wenn die Rechnung abgelehnt werden soll, können Sie den User (1) sowie die Rechnung (2) auswählen und auf Declined (10) um diese abzulehnen.

Sie können den Änderungs-Vorgang auch abbrechen in dem Sie auf Exit (11) klicken, um die Ansicht wieder zu verlassen.

### 3.4. Statistiken
![image](https://github.com/user-attachments/assets/bab3eee1-110a-48b1-aaad-8f0d05514b45)

Nach erfolgtem Klick auf den Statistics Button im Admin-Dashboard gelangen Sie auf diese Ansicht. Hier können Sie für verschiedenste Zwecke Statistiken betrachten oder sogar exportieren. 
aus
Die Ansicht gibt Auskunft über die durchschnittliche Anzahl der hochgeladenen Rechnungen pro User in diesem Monat (1) und die Summe der Rückerstatteten Beträge in den letzten 12 Monaten (2). Um diese Auswertungen zu exportieren, können sie die Datei-Art (CSV, PDF) (4) wählen und auf Save (5) klicken um diese zu speichern. 

Falls Sie ohne weiteres die Ansicht verlassen wollen, klicken Sie auf Exit (3).

Zudem gibt es folgende Statistiken zur Auswahl: Anzahl der Rechnungen im Monat (6), Verteilung zwischen Supermarkt und Restaurant (7) und Rückerstattungen pro Monat (8). Hier können Sie einer der Statistiken anklicken und gelangen zu diesen Fenstern:

### Number of invoices per Month
![image](https://github.com/user-attachments/assets/a16d6554-e888-4af6-a74a-a7fd3a430526)

In der Mitte des Fensters sehen Sie ein Balkendiagramm (1), welches indiziiert, wie viele Rechnungen pro Monat von allen Usern hochgeladen worden sind. Diese Auswertung können Sie ebenfalls in verschiedene Formate (JSON, XML) zugunsten der Gehaltsabrechnung exportieren, indem Sie ein Format wählen (2) und dann auf Save (3) klicken. Nach erfolgtem Export, erscheint eine Bestätigung nach erfolgter und eine Error-Message nach fehlgeschlagener Änderung. Um die Ansicht wieder zu verlassen und auf die Statistikansicht zu gelangen, klicken Sie Exit (4). 

### Distribution restaurant or supermarket
![image](https://github.com/user-attachments/assets/ee1bc3b1-799c-4bf5-9800-f106b1653710)

In der Mitte des Fenster sehen Sie ein Tortendiagramm (1), welches anzeigt, wie die Verteilung der hochgeladenen Rechnungen zwischen Restaurant und Supermarkt aussieht. Diese Auswertung können Sie ebenfalls in verschiedene Formate (CSV, PDF) zugunsten der Berichtserstattung exportieren, indem Sie ein Format wählen (2) und dann auf Save (3) klicken. Nach erfolgtem Export, erscheint eine Bestätigung nach erfolgter und eine Error-Message nach fehlgeschlagener Änderung. Um die Ansicht wieder zu verlassen und auf die Statistikansicht zu gelangen, klicken Sie Exit (4). 

### Reimbursement per month
![image](https://github.com/user-attachments/assets/5090cc33-0173-4347-a30a-f3fff4092145)

In der Mitte des Fenster sehen Sie ein Balkendiagramm (1), welches anzeigt, wie hoch die durchschnittliche Rückvergütung aller hochgeladenen und akzeptierten Rechnungen im Monat ist. Diese Auswertung können Sie ebenfalls in verschiedene Formate (CSV, PDF) zugunsten der Berichtserstattung exportieren, indem Sie ein Format wählen (2) und dann auf Save (3) klicken. Nach erfolgtem Export, erscheint eine Bestätigung nach erfolgter und eine Error-Message nach fehlgeschlagener Änderung. Um die Ansicht wieder zu verlassen und auf die Statistikansicht zu gelangen, klicken Sie Exit (4). 

### 3.5. Benutzer hinzufügen
![image](https://github.com/user-attachments/assets/4aacfa1e-4175-41d4-a5c0-942ed5967309)

Nachdem sie erfolgreich vom Admin-Dashboard weitergeleitet wurden zu Add Admin/User, können sie nun eine Person hinzufügen.  

Zuerst wählen sie eine E-Mail-Adresse (Achtung: Im Normalfall wird sie so VORNAMEN.NACHNAMEN@lunchify.com). Geben sie die E-Mail-Adresse im Feld E-Mail (1) ein und den Namen im Feld Namen (2) (Achtung: Hier auch VORNAME und NACHNAMEN).  

Sie können sich ein passendes Passwort überlegen und in Password (3) eingeben. Zuletzt müssen sie noch zwischen Admin (4) und User (5) wählen. Administratoren können, so wie sie agieren und Rechnungen bearbeiten, User hinzufügen und Löschen und alle Rückerstattungen einsehen. 

Beachten sie, dass sie alle Felder befüllt haben. Drücken sie anschließend auf Add (6). War das hinzufügen einer Person erfolgreich, bekommen sie eine positive Rückmeldung.  

Wenn sie auf Exit (7) klicken, kommen sie wieder zurück auf die Admin-Dashboard-Seite. 

### 3.6. Benutzer löschen
![image](https://github.com/user-attachments/assets/b12b43d6-adc0-47a0-ba40-aab5b91e6696)

Um einen Account zu löschen, wählen Sie zuerst bei Type (1) die Art von Account die Sie entfernen wollen. Hier sind Admin und User zur Auswahl gegeben. Danach suchen Sie im E-Mail (2) Drop-Down einen Account aus welchen Sie entfernen wollen. Nach erfolgter Auswahl drücken Sie Delete (3) um den Vorgang durchzuführen.

Wenn Sie ohne Weiteres die Ansicht verlassen möchten, drücken Sie auf Exit (4) um wieder auf den User-Dashboard zu gelangen.

### 3.7. Rückerstattungsbetrag ändern
![image](https://github.com/user-attachments/assets/02880906-cc81-4863-9121-08e02c216557)

Wenn es intern zu Änderungen des allgemeinen Rückerstattungsbetrags kommen sollte, kann man diese ohne weiteres ändern. Im Fenster Change Reimbursement werden die aktuellen Beträge angezeigt, die Sie hier ändern können. 

Im Feld Restaurant (1) ist nun ein Betrag von 3,00€ vorgegeben. Um diesen zu ändern, klicken Sie in das Feld und geben den neuen Wert ein (Achtung!: Geld-Beträge bitte mit Punkt (3.5) eintragen).

Im Feld Supermarket (2) ist nun ein Betrag von 2,50€ vorgegeben. Um diesen zu ändern, klicken Sie in das Feld und geben den neuen Wert ein (Achtung!: Geld-Beträge bitte mit Punkt (2.0) eintragen).

Um die Änderungen zu speichern und für zukünftige Rechnungen gelten zu machen, klicken Sie auf Save (3). Es erscheint eine Bestätigung nach erfolgter und eine Error-Nachricht nach fehlgeschlagener Änderung.

Um die Ansicht ohne oder nach Änderung zu verlassen klicken Sie auf Exit (4).
