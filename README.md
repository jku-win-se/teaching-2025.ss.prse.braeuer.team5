
# UML Diagramme

### draw.io/ diagrams.net

**Beispiel**

Bild

Diagrams.net (oder früher: draw.io) ist eine kostenlose Software zur Erstellung von Diagrammen wie Flussdiagrammen, UML-Diagrammen, Organigrammen und Netzwerkdiagrammen. Sie ist als Online-Anwendung verfügbar, die direkt im Browser genutzt werden kann. Zudem bietet es eine Desktop-Version für verschiedenste Betriebssysteme.

Verfügbar für: Windows, macOS, Linux und Chrome OS

### StarUML


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
