```mermaid
classDiagram
    class User {
        +String name
        +String email
        +String password
        +Enum rollen
        +void login()
        +void insertInvoice
        +void viewHistory
    }
    class Administrator {
        +double balance
        +void deposit(amount: double)
    }
   
    User <|-- Administrator
