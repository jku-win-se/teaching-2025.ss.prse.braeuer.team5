```mermaid
classDiagram
    class User {
        +String name
        +String email
        +String password
        +Enum rollen
        +List <Invoice> invoices
        +void login()
        +void uploadInvoice()
        +List<Invoice>: viewHistory()
    }

    class Administrator {
        +List<Invoice> viewAllInvoices()
        +void checkInvoices()
    }

    class Invoice{
        +String id
        +Localdate date
        +double amount
        +Category category
        +STATUS status
        +image
        +void calculateRefund()
    }

    class Category{
        <<enumaration>>
        RESTAURANT
        SUPERMARKET
    }

    class OCRService{
        +scanAmount()
        +scanCategory()
    }

    class Status{
        <<enumaration>>
        APPROVED
        PROCESSING
        DECLINED
    }
   
    User <|-- Administrator
    Invoice --> Category : uses
    Invoice --> Status : uses
