```mermaid
classDiagram
    class User {
        +String name
        +void login()
    }
    class Account {
        +double balance
        +void deposit(amount: double)
    }
    User --> Account : owns
