

# UML Diagramme

### draw.io/ diagrams.net

**Beispiel**

https://app.diagrams.net/ (oder früher: draw.io) ist eine kostenlose Software zur Erstellung von Diagrammen wie Flussdiagrammen, UML-Diagrammen, Organigrammen und Netzwerkdiagrammen. Sie ist als Online-Anwendung verfügbar, die direkt im Browser genutzt werden kann. Zudem bietet es eine Desktop-Version für verschiedenste Betriebssysteme.

![draw.io](/UML/draw.io-Beispiel.jpg)

Verfügbar für: Windows, macOS, Linux und Chrome OS

### StarUML
StarUML ist ein kostenloses UML-Moodeling-Tool, dessen Basisversion gratis verfügbar ist. Das Tool kann online heruntergeladen werden und benötigt für die Basisversion keine Registrierung. 

https://staruml.io
Verfügbar für: Windows, macOS and Linux
![Bildschirmfoto 2025-03-13 um 07 04 10](https://github.com/user-attachments/assets/83d5ef3f-07ec-4189-8c39-242f99d1de9f)

### PlantUML
PlantUML ist ein kostenloses textbasiertes Tool zur Erstellung von UML-Diagrammen. Das Tool kann ganz einfach online aufgerufen werden und zusätzlich wird keine Registrierung benötigt. Unter diesem Link kann die Seite aufgerufen werden: https://www.planttext.com/
Um einen Überblick über die Funktionen zur Erstellung eines UML-Diagramm zu bekommen, bieten sie eine Informationsseite an: https://plantuml.com/de/

Beispiel UML:
![PlantUml](https://github.com/user-attachments/assets/6cf3a8ea-c6eb-4ee3-b901-79b012e72686)

Code:
@startuml
skinparam groupInheritance 2
abstract class Person{
  + name: String
  + alter: Integer
}

class Student{
  + matrikelNr: Integer
}

class LVALeiter{
  + mitarbeiterID: Integer
  + trinktKaffe(): void
}

class LVA{
  + Titel: String
}

Person <|-- Student
Person <|-- LVALeiter

LVALeiter --> LVA: leitet >
Student --> LVA: besucht >

@enduml

## Weitere Tools: https://t2informatik.de/wissen-kompakt/uml/
