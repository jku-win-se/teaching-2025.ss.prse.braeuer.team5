```mermaid
classDiagram
    class User {
        +String name
        +String email
        +String password
        +boolean isAdministrator
        +List <Invoice> invoices
        +void login()
        +void uploadInvoice(File file)
        +List<Invoice>: viewHistory()
        +void receiveNotification(String message)
        +void changeNotificationSettings()
    }

    class Administrator {
        +List<Invoice> viewAllInvoices()
        +void checkInvoices()
        +void flagAnomalies()
        +void manageUsers()
        +void sendNotification(String message)
        +void configureRefundAmounts(double restaurant, double supermarket)
    }

    class Invoice{
        +String id
        +Localdate date
        +double amount
        +Category category
        +Status status
        +File file
        +void calculateRefund()
        +User submittedBy
        +boolean checkAnomaly()
        +void updateStatus(Status newStatus)
    }

    class Category{
        <<enumaration>>
        RESTAURANT
        SUPERMARKET
    }

    class OCRService{
        +double scanAmount(File file)
        +Category scanCategory(File file)
    }

    class Status{
        <<enumaration>>
        APPROVED
        PROCESSING
        DECLINED
    }

    class Notification{
        +void sendAdminAlert(String message)
    }

    class AnomalyDetection{
        +boolean detectDuplicateUpload(User user, Invoice invoice)
        +boolean detectMismatch(Invoice invoice)
    }

    class Statistics{
        +int totalInvoices
        +double totalReimbursmment
        +Map<Category, int> categoryDistribution
        +void generateMonthlyReport()
    }
   
    User <|-- Administrator
    Invoice --> Category : uses
    Invoice --> Status : uses
    User --> Invoice : uses
    Administrator --> Notification : uses
    Administrator --> Statistics : uses
    Notification --> User : notifies
    Invoice --> OCRService : calls for scanning
    AnomalyDetection --> Invoice : checks
    OCRService --> Invoice : provides scanning
