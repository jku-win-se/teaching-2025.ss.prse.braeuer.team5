```mermaid
classDiagram
    class Utilities {
        +Administrator
        +Category
        +Invoice
        +User
        +etc...
    }

    class Controller {
        +StartController
        +NotificationsController
        +AddInvoiceController
        +etc...
    }

    class Ressources {
        +AddInvoice.fxml
        +Notifications.fxml
        +Statistics.fxml
        +etc...
    }

    class Repository {
        +AdminNotificationRepository
        +DeletedNotificationRepository
        +InvoiceRepository
        +UserRepository
    }

    class Export {
        +CSVExporter
        +JSONExporter
        +PDFExporter
    }

    class DatabaseConnection {
        +SupaBase
    }

    Controller --> Utilities : benutzt
    Controller --> Ressources : lädt/aktualisiert
    Utilities --> Repository : speichert/lädt Daten
    Repository --> DatabaseConnection : verbindet mit DB
    Controller --> Export : exportiert Daten
