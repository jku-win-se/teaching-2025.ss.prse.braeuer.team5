# 📘 Systemdokumentation – Lunchify

## 1. Einleitung

- **Systemname:** Lunchify  
- **Ziel:** Eigenentwickelte Softwarelösung zur Rückvergütung von Essensausgaben für Mitarbeiter:innen  
- **Auftraggeber:** Linzer Software-Entwicklungsunternehmen  
- **Projektstart:** SS 2025  
- **Entwicklungsansatz:** Agil  
- **Technologien:** Java + JavaFX  
- **Speicherung:** Relationale Datenbank  

---

## 2. Systemübersicht

**Lunchify** ermöglicht es Mitarbeiter:innen, Essensrechnungen (Restaurant/Supermarkt) digital einzureichen und automatisiert eine Rückerstattung zu erhalten.

### Hauptfunktionen:

- 🔐 Authentifizierung und Rollen (Benutzer, Admin)  
- 📤 Upload und automatische Erkennung von Rechnungen via OCR  
- 💸 Rückerstattungslogik abhängig von Typ und Betrag  
- 📈 Verlauf, Korrektur und grafische Auswertungen  
- 🧑‍💼 Admin-Dashboard mit Export und Anomalie-Erkennung  

---

## 3. Systemarchitektur 

- **Frontend:** JavaFX  
- **Backend:** Java  
- **Persistenz:** Relationale Datenbank  
- **OCR-Technologie:** Open-Source OCR  
- **Exportformate:** JSON, XML, CSV, PDF
  
<img width="654" alt="Bildschirmfoto 2025-06-26 um 16 34 05" src="https://github.com/user-attachments/assets/9866f877-47e5-4827-9083-e5a3618f89bb" />

---

## 4. Benutzerrollen und Berechtigungen

### 👤 Benutzer

- Upload von Rechnungen  
- Anzeige des Verlaufs  
- Korrektur bis Monatsende  
- Bestätigung von Einreichungen  

### 👩‍💼 Admin

- Übersicht über alle Anträge  
- Filter- & Suchfunktionen  
- Export und Konfiguration  
- Nutzerverwaltung  
- Anomalie-Erkennung  

---

## 5. Hauptprozesse

### 5.1 Upload & Klassifizierung

- 📷 Bild-Upload  
- 🔍 Automatische Erkennung von Betrag und Typ (Restaurant oder Supermarkt)  

### 5.2 Rückerstattungsregeln

| Typ         | Betrag ≤ Schwelle | Betrag > Schwelle |
|-------------|--------------------|-------------------|
| Restaurant  | Originalbetrag     | 3 €               |
| Supermarkt  | Originalbetrag     | 2,5 €             |

### 5.3 Verlauf & Korrektur

- Anzeige aller Anträge mit Status  
- Änderungen möglich bis Monatsende  

### 5.4 Admin-Funktionalitäten

- Suche, Filter, Korrektur und Löschung  
- **Berichte:** Anzahl, Durchschnitt, Verteilung, Gesamtsumme  
- **Export:** JSON/XML je Monat für Gehaltsabrechnung  
- **Konfiguration:** Erstattungsbeträge, Benutzerverwaltung  
- **Anomalie-Erkennung:** Abweichende Beträge, manuelle Änderungen  

---

## 6. Nicht-funktionale Anforderungen

- 🔒 **Sicherheit:** Zugriff nur nach Login, klare Rollentrennung  
- 🎨 **Benutzerfreundlichkeit:** Intuitive UI, visuelle Auswertungen  
- ⚡ **Performance:** Schnelle Bildverarbeitung, optimierte OCR  
- ✅ **Testabdeckung:** Unit-Tests  

---

## 7. Technische Umsetzung & Tools

- **Versionierung:** GitHub (Issues, Milestones, Pull Requests)  
- **Testing:** JUnit, Mockito  
- **Build-Tool:** Maven  
- **Code-Qualität:** SonarQube, PMD  

---


## 8. Überblick

Dieses Projekt ermöglicht die automatisierte Texterkennung auf eingescannten Rechnungen und deren Speicherung in einer Datenbank. Es nutzt die Google Cloud Vision API zur Texterkennung und bietet Funktionen zur Verwaltung von Benutzerkonten und Rechnungsdaten.

## Wichtige Klassen

### 🔍 8.1 CloudOCRService

#### Zweck:
Die Klasse CloudOCRService nutzt die Google Cloud Vision API zur automatischen Texterkennung auf eingescannten Belegen. Sie analysiert die Bilder und extrahiert relevante Daten wie Datum, Betrag und Kategorie von Rechnungen.

#### Hauptfunktionen:

analyzeImage(File imageFile)
Führt die Texterkennung durch und extrahiert Datum, Betrag und Kategorie aus dem OCR-Ergebnis.

encodeImageToBase64(File imageFile)
Wandelt das Bild in einen Base64-kodierten String zur Übertragung an die API um.

extractTextFromJson(String json)
Parst die JSON-Antwort der Vision API und extrahiert den reinen Text.

extractDate(String text) / extractAmount(String text)
Sucht nach typischen Datums- und Betragsformaten im Text.

detectCategory(String text)
Erkennt aus dem Textinhalt die passende Ausgabenkategorie (z. B. "RESTAURANT", "SUPERMARKET").

### 👤 8.2 UserRepository

#### Zweck:
Verwaltet alle persistenzbezogenen Operationen für Benutzerkonten in der Datenbank. Unterstützt die Funktionen Login, Benutzeranlage, -abfrage und -löschung.

#### Hauptfunktionen:

addUser(User user)
Legt einen neuen Benutzer in der Datenbank an.

deleteUser(String email)
Löscht einen Benutzer anhand seiner E-Mail-Adresse.

findByEmailAndPassword(String email, String password)
Führt einen Login-Abgleich durch.

getByEmail(String email)
Holt den vollständigen Benutzerdatensatz.

getAllAdminEmails() / getAllUserEmails()
Gibt Listen von Admin- bzw. Benutzer-E-Mails zurück.

getAllUsersWithoutLoggedAdmin(String email)
Gibt alle Benutzer-E-Mails außer der des eingeloggten Admins zurück.

### 📄 8.3 InvoiceRepository

#### Zweck:
Verwaltet alle Datenbankoperationen rund um Rechnungen, einschließlich Speicherung, Abfrage, Statusaktualisierung, Löschung und Statistikauswertung.

#### Hauptfunktionen:

saveInvoiceInfo(...)
Speichert neue Rechnungsdaten in der Datenbank und lädt das Bild in einen Bucket.

getAllInvoicesAdmin() / getAllInvoicesUser(String userEmail)
Gibt alle Rechnungen zurück (für Admins bzw. pro Benutzer).

updateInvoice(...) + Einzelmethoden
Aktualisiert Betrag, Datum, Kategorie, Status und Erstattung.

getAcceptedInvoicesCurrentMonth(...) / getDeclinedInvoicesCurrentMonth(...)
Liefert monatlich gefilterte Rechnungen nach Status.

deleteInvoice(...)
Löscht eine Rechnung anhand von Benutzer und Datum.

#### Statistikfunktionen:

getActiveUsersThisMonth()

getInvoiceCountForUserThisMonth()

getTotalReimbursementForUserThisMonth()
Diese Funktionen berechnen Kennzahlen für Dashboards oder Reports.



