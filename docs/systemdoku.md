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

## 3. Systemarchitektur (technisch)

- **Frontend:** JavaFX  
- **Backend:** Java  
- **Persistenz:** Relationale Datenbank  
- **OCR-Technologie:** Open-Source OCR  
- **Exportformate:** JSON, XML, CSV, PDF  

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

> © 2025 Lunchify – Internes Projekt der [Firma eintragen]



