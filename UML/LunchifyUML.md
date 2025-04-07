```mermaid
classDiagram
    class User {
        +String name
        +String email
        +String password
        +boolean isAdministrator
        +List <Invoice> invoices
        +String preferredNotificationMethod
        +Notification notification
        +String getName()
        +String getEmail()
        +String getPassword()
        +boolean isAdministrator()
        +String getPreferredNotificationMethod()
        +void login()
        +void uploadInvoice(Invoice invoice)
        +List<Invoice> viewAllInvoices()
        +List<Invoice> viewHistory()
        +void receiveNotification(String message)
        +void changeNotificationSettings(String newPreferende)
        +void setNotification
    }

    class Administrator {
        +List<Invoice> viewAllInvoices()
        +void checkInvoices()
        +void flagAnomalies()
        +void sendNotification(String message)
        +void configureRefundAmounts(double restaurant, double supermarket)
        +void addUser(User user)
        +void addAdministrator(Administrator administrator)
        +boolean deleteUser(String email)
        +void approveInvoice(Invoice invoice)
        void declinedInvoice(Invoice invoice)
        +void correctInvoice(Invoice invoice, double newAmount, Category newCategory, LocalDate             newDate)
    }

    class Invoice{
        +String userName
        +Localdate date
        +double amount
        +Category category
        +Status status
        +String file_Url
        +LocalDateTime createdAt
        +double reimbursement
        +long MAX_FILE_SIZE
        +validateDate(LocalDate date)
        +validateAmount(double amount)
        +validateFile(File file)
        +String getUserEmail()
        +double getAmount()
        +void setAmount(double amount)
        +Category getCategory()
        +void setCategory(Category category)
        +void setFileUrl(String fileUrl)
        +void setDate(LocalDate date)
        +LocalDate getDate()
        +String getFile_Urls()
        +LocalDateTime getCreatedAt()
        +double getReimbursement()
        +void setReimbursement(double reimbursement)
        +void setStatus(Status status)
        +Status getStatus()
        +double calculateRefund()
        +String getStatusString()
        +String getCategoryString()
        +void approve()
        +void declined()
        +void correct(double newAmount, Category newCategory, LocalDate newDate)
        +String toString()
        +boolean isEditable()
    }

    class Category{
        <<enumaration>>
        RESTAURANT
        SUPERMARKET
        +String displayName
        +double costumRefundAmount
        +double getRefundAmount()
        +void setCustomRefundAmount(Category category, double amount)
        +double getDefaultRefund()
        +String getDisplayName()
    }

    class OCRService{
    }

    class Status{
        <<enumaration>>
        APPROVED
        PROCESSING
        DECLINED
    }

    class Notification{
        +List<String> messagesSent
        +public void sendInApp(User user, String message)
        +public void sendEmail(User user, String message)
        +void clearMessages()
    }

    class AnomalyDetection{
        +boolean detectDuplicateUpload(User user, Invoice invoice)
        +boolean detectMismatch(Invoice invoice)
    }

    class Statistics{
    }

    class DatabaseConnection{
        +String USER
        +String PWD
        +String URL_JDBC
        +String BUCKET
        +String API_KEY
        +String URL_SUPABASE
        +Connection getConnection()
        +String uploadFileToBucket(File imageFile)
        +String getPublicFileUrl(String filePath)
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
