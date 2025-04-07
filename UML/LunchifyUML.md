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

    class InvoiceRepository{
        +String SELECT_ALL_INVOICES
        +String SELECT_ALL_INVOICES_USER
        +String UPDATE_REIMBURSEMENT
        +String FIND_BY_ID
        +String FIND_BY_USER
        +String UPDATE_CATEGORY_REFUND
        +String UPDATE_AMOUNT
        +String UPDATE_STATUS
        +String UPDATE_DATE
        +String UPDATE_CATEGORY
        +List<Invoice> getAllInvoicesAdmin()
        +List<Invoice> getAllInvoicesUser(String userEmail)
        +void saveInvoiceInfo(Connection connection, String user_eemail, Date date, double amount,         Category category, Status status, String file_url, LocalDateTime createdAt, double                 reimbursement, File imageFile)
        +boolean invoiceExists(Connection connection, String user_email, Date date)
        +Invoice createInvoiceFromResultSet(ResultSet rs)
        +void updateInvoiceDate(Invoice invoice)
        +void updateInvoiceAmount(Invoice invoice)
        +void updateInvoiceStatus(Invoice invoice)
        +void updateInvoiceCategory(Invoice invoice)
        +void updateInvoiceReimbursement(Invoice invoice)
        +boolean deleteInvoiceIfEditable(int invoiceid)
    }

    class UserRepository{
        +String INSERT_USER_SQL
        +void addUser(User user)
        +String GET_ALL_ADMINS_SQL
        +String GET_ALL_USERS_SQL
        +List<String> getAllAdminEmails()
        +List<String> getAllUserEmails()
        +List<String> getEmails(String query)
        +String DELETE_USER_SQL
        +boolean deleteUser(String email)
        +User findByEmailAndPassword(String email, String password)
    }

    class AddAdminController{
        +Textfield txtEmail
        +Textfield txtName
        +Textfield txtPassword
        +CheckBox chkAdmin
        +CheckBox chkUser
        +Label message
        +void addAdminUser(ActionEvent event)
        +void clearFields
        +void cancelAddAdminUser(ActionEvent event)
        +void loadPage(String fxmlFile, ActionEvent event)
    }

    class AddInvoiceController{
        +DatePicker datePicker
        +TextField amountField
        +Label statusLabel
        +Button removeImageBtn
        +ComboBox<String> categoryCombo
        +Label cancelAdd
        +Button uploadButton
        +File selectedFile
        +double reimbursement
        +Set<LocalDate> uploadDates
        +void cancelAdd(ActionEvent event)
        +void loadPage(String fxmlFile, ActionEvent event)
        +void handFileSelect(Action Event event)
        +void handleUpload(ActionEvent event)
        +void resetForm()
    }

    class AdminDashboardController{
        +TableView<Invoice> invoiceTable
        +TableColumn<Invoice, String> userColumn
        +TableColumn<Invoice, String> submissionDateColumn
        +TableColumn<Invoice, Double> amountColumn
        +TableColumn<Invoice, Category> categoryColumn
        +TableColumn<Invoice, Status> statusColumn
        +TableColumn<Invoice, Double> reimbursementColumn
        +void initialize()
        +void loadInvoices()
        +void handleEditInvoice(ActionEvent event)
        +void handleStatistics(ActionEvemt event)
        +void handleAddAdminUser(ActionEvent event)
        +void handleDeleteAdminUser(ActionEvent event)
        +void handleLogoutAdmin(ActionEvemt event)
        +void changeReimbursement(ActionEvent event)
    }

    class AdminInvoiceManagementController{
        +ComboBox<Invoice> selectInvoice
        +ComboBox<String> selectUser
        +TextField amountField
        +TextField reimbursementField
        +DatePicker invoiceDateField
        +ComboBox<String> categoryCombobox
        +Button changeButton
        +Button invoiceAcceptButton
        +Button declinedButton
        +Label statusLabel
        +void initialize
        +void searchUser(ActionEvent event)
        +void searchInvoice(ActionEvent event)
        +void changeInvoiceDetails(ActionEvent event)
        +void handleAcceptInvoice(ActionEvent event)
        +void handleDeclinedInvoice(ActionEvent event)
        +void cancelEditAdmin(ActionEvet event)
    }

    class ChangeReimbursementController{
        +TextField restaurantField
        +TextField supermarketField
        +Button saveButton
        +Button exitButton
        +void initialize()
        +void handleSave()
        +void handleExit(ActionEvent event)
        +void loadPage(String fxmlFile, ActionEvemt event)
        +double validateInput(String input)
        +void showAlert(Alert.AlertType alertTye, String title, String message)
    }

    class DeleteAdminUserController{
        +ChoiceBox type
        +ChoiceBox email
        +Label message
        +void initialize()
        +void updateEmailList
        +void deleteAdminUser(ActionEvent event)
        +void cancelDeleteAdminUser(ActionEvent event)
        +void loadPage(String fxmlFile, ActionEvent event)
    }

    class EditInvoiceController{
        +Invoice invoice
        +void cancelEdit(ActionEvent event)
    }

    class StartApplication extends Application{
        +start()
    }

    class StartController{
        +TextField emailFieldUser
        +TextField passwordFieldUser
        +TextField emailFieldAdmin
        +TextField passwordFieldAdmin
        +Label errorLabel
        +Label errorLabel1
        +void handleAdminLogin(ActionEvent event)
        +void handleUserLogin(ActionEvent event)
    }

    class StatisticsController{
        +Label cancelButton
        +void cancekStatistics(ActionEvent event)
    }

    class UserDashboardController{
        +TableView<Invoice> invoiceTable
        +TableColumn<Invoice, String> submissionDateColumn
        +TableColumn<Invoice, Double> amountColumn
        +TableColumn<Invoice, Category> categoryColumn
        +TableColumn<Invoice, Status> statusColumn
        +TableColumn<Invoice, Double> reimbursementColumn
        +String currentUserEmail
        +void setCurrentUserEmail(String email)
        +String getCurrentUserEmail()
        +void initialize()
        +void loadInvoices()
        +void handleEditInvoiceUser(ActionEvent event)
        +void handleUploadInvoice(ActionEvent event)
        +void handleLogoutUser(ActionEvemt event)
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
    UserDashboardController --> Invoice : shows
    AdminDashboardController --> Invoice : shows
    AdminDashboardController --> Administrator : controls
    AdminInvoiceManagementController --> Invoice : edits
    AdminInvoiceManagementController --> User : selects
    ChangeReimbursementController --> Category : configures
    InvoiceRepository --> Invoice : persists
    InvoiceRepository --> DatabaseConnection : uses
    UserRepository --> User : persists
    UserRepository --> DatabaseConnection : uses
    AddAdminController --> User : creates
    AddInvoiceController --> Invoice : creates
    AdminDashboardController --> Administrator : controls
    EditInvoiceController --> Invoice : edits
    StartController --> User : logs in
    StartController --> Administrator : logs in
    StatisticsController --> Statistics : displays
    OCRService --> DatabaseConnection : accesses
    Invoice --> File : validates
    InvoiceRepository --> ResultSet : uses
    DatabaseConnection --> Connection : returns
    Notification --> List~String~ : stores messages

