```mermaid
classDiagram
    class Administrator {
        +Administrator(String name, String email, String password)
        +List<Invoice> viewAllInvoices()
        +void addUser(String name, String email, String password, boolean isAdministrator)
        +void addAdministrator(String name, String email, String password, boolean isAdministrator)
        +boolean deleteUser(String email)
        +void approveInvoice(Invoice invoice)
        +void declinedInvoice(Invoice invoice)
        +void correctInvoice(Invoice invoice, double newAmount, Category newCategory, LocalDate newDate)
    }

     class AnomalyDetection{
        +static boolean detectMismatch(Invoice invoice)
        -static String normalizeDate(String ocrDate)
        -static double parseAmount(String amount)
    }

    class Category {
        <<enumeration>>
        RESTAURANT
        SUPERMARKET
        -final String displayName
        +double customRefundAmount
        +double getRefundAmount()
        +static void setCustomRefundAmount(Category category, double amount)
        -double getDefaultRefund()
    }

    class CloudOCRService {
        -static final Logger LOGGER
        -static final String API_KEY
        -static final String ENDPOINT
        +OCRResult analyzeImage(File imageFile)
        +String encodeImageToBase64(File imageFile)
        +String buildRequestJson(String base64Image)
        +String extractTextFromJson(String json)
        +String extractDate(String text)
        +String extractAmount(String text)
        +String detectCategory(String text)
    }

    class DatabaseConnection{
        -static final String USER
        -static final String PWD
        -static final String URL_JDBC
        +static final String BUCKET
        +static final String API_KEY
        +static final String URL_SUPABASE

        +static Connection getConnection()
        +static String uploadFileToBucket(File imageFile)
        +static String getPublicFileUrl(String filePath)
    }

    class Invoice{
        -String userEmail
        -LocalDate date
        -double amount
        -Category category
        -Status status
        -String fileUrl
        -LocalDateTime createdAt
        -double reimbursement
        -static final long MAX_FILE_SIZE
        +String ocrDate
        +String ocrAmount
        +String ocrCategory
        -boolean anomalyDetected

        +Invoice(String userEmail, LocalDate date, double amount, Category category, Status status, String fileUrl, LocalDateTime createdAt, double reimbursement)
        +LocalDate validateDate(LocalDate date)
        +static void validateFile(File file)
        +String getUserEmail()
        +double getAmount()
        +void setAmount(double amount)
        +Category getCategory()
        +void setCategory(Category category)
        +void setFileUrl(String fileUrl)
        +void setDate(LocalDate date)
        +LocalDate getDate()
        +String getFileUrl()
        +LocalDateTime getCreatedAt()
        +double getReimbursement()
        +void setReimbursement(double reimbursement)
        +void setStatus(Status status)
        +Status getStatus()
        +String getCreatedAtString()
        +double calculateRefund()
        +String getStatusString()
        +String getCategoryString()
        +void approve()
        +void declined()
        +void correct(double newAmount, Category newCategory, LocalDate newDate)
        +boolean isEditable()
        +boolean isDateOnWeekday(LocalDate date)
        +boolean isInCurrentMonth(LocalDate date, LocalDate now)
        +boolean isValidAmount(double amount)
        +void setOcrData(String date, String amount, String category)
        +void setAnomalyDetected(boolean anomalyDetected)
        +String toString()
    }

     class Notification{
        -static final Logger LOGGER
        +static final List~String~ MESSAGES_SENT
        -String id
        -String message
        -LocalDateTime timestamp
        -boolean isForAdmin
        -String targetUserEmail

        +Notification()
        +Notification(String message)
        +Notification(String message, boolean isForAdmin, String targetUserEmail)
        +void sendInApp(User user, String message)
        +static void clearMessages()
        +String getMessage()
        +String getId()
        +LocalDateTime getTimestamp()
        +String getFormattedTimestamp()
        +String getFullMessage()
        +String getUserMessage()
        +void setId(String id)
        +void setTimestamp(LocalDateTime timestamp)
    }

    class OCRService{
        +String date
        +String amount
        +String category
        +OCRResult(String date, String amount, String category)
        +String toString()
    }

    class Statistics{
        +Map<String, Integer> getInvoicesPerMonth()
        +Map<String, Map<String, UserInvoiceData>> getInvoicesPerUserAndMonth()
        +Map<String, Double> getReimbursementPerMonth()
        +double getReimbursementForAYear()
        +double getAverageOfInvoicesPerUserPerMonth()
        +int getInvoicesPerSupermarket()
        +int getInvoicesPerRestaurant()
        +int getInvoicesPerSupermarketUser(String currentUser)
        +int getInvoicesPerRestaurantUser(String currentUser)
        +Map<String, Object> getUserReimbursementDetails()
        +Map<String, Map<String, Map<String, Object>>> getUserReimbursementDetailsPerMonth()
        -String getMonthName(int monthNumber)
    }

    class Status{
        <<enumaration>>
        APPROVED
        PROCESSING
        DECLINED
    }

    class User {
        -String name
        -String email
        -String password
        -boolean isAdministrator
        -List~Invoice~ invoices
        -String preferredNotificationMethod
        -Notification notification

        +User(String name, String email, String password, boolean isAdministrator)
        +String getName()
        +String getEmail()
        +String getPassword()
        +boolean isAdministrator()
        +void login()
        +List~Invoice~ viewAllInvoices()
        +List~Invoice~ viewHistory()
        +void receiveNotification(String message)
        +void setNotification(Notification notification)
    }

    class UserInvoiceData {
        -String name
        -String email
        -int invoiceCount

        +UserInvoiceData(String name, String email, int invoiceCount)
        +String getName()
        +String getEmail()
        +int getInvoiceCount()
        +void setInvoiceCount(int invoiceCount)
    }

    class UserSession {
        -static User currentUser

        +static void setCurrentUser(User user)
        +static User getCurrentUser()
    }

    class AddAdminController{
        -TextField txtEmail
        -TextField txtName
        -TextField txtPassword
        -CheckBox chkAdmin
        -CheckBox chkUser

        +void addAdminUser()
        -void clearFields()
        +void cancelAddAdminUser(ActionEvent event)
        -void loadPage(String fxmlFile, ActionEvent event)
        -void showAlert(Alert.AlertType type, String title, String message)

    }

    class AddInvoiceController {
        -DatePicker datePicker
        -TextField amountField
        -Label statusLabel
        -ComboBox~String~ categoryCombo
        -Button uploadButton
        -File selectedFile
        -String ocrAmount
        -String ocrDate
        -String ocrCategory
        -Set~LocalDate~ uploadedDates
    
        +void cancelAdd(ActionEvent event)
        -void loadPage(String fxmlFile, ActionEvent event)
        +void handleFileSelect(ActionEvent event)
        +void handleUpload()
        -void resetForm()
        -void showAlert(Alert.AlertType type, String title, String message)
    }

    class AdminDashboardController {
        -TableView~Invoice~ invoiceTable
        -TableColumn~Invoice, String~ userColumn
        -TableColumn~Invoice, String~ submissionDateColumn
        -TableColumn~Invoice, Double~ amountColumn
        -TableColumn~Invoice, Category~ categoryColumn
        -TableColumn~Invoice, Status~ statusColumn
        -TableColumn~Invoice, Double~ reimbursementColumn
    
        +void initialize()
        -void loadInvoices()
        +void handleEditInvoice(ActionEvent event)
        +void handleStatistics(ActionEvent event)
        +void handleAddAdminUser(ActionEvent event)
        +void handleDeleteAdminUser(ActionEvent event)
        +void handleLogoutAdmin(ActionEvent event)
        +void changeReimbursement(ActionEvent event)
        +void openNotifications(ActionEvent event)
    }

    class AdminInvoiceManagementController {
        -ComboBox~Invoice~ selectInvoice
        -ComboBox~String~ selectUser
        -TextField amountField
        -TextField reimbursementField
        -DatePicker invoiceDateField
        -ComboBox~String~ categoryCombobox
        -Button changeButton
        -Button invoiceAcceptButton
        -Button declinedButton
        -Button deleteButton1
        -Label statusLabel
    
        +void initialize()
        +void searchUser(ActionEvent event)
        +void searchInvoice(ActionEvent event)
        +void changeInvoiceDetails(ActionEvent event)
        +void handleAcceptInvoice(ActionEvent event)
        +void handleDeclinedInvoice(ActionEvent event)
        +void handleDeleteInvoice(ActionEvent event)
        +void cancelEditAdmin(ActionEvent event)
        -void showAlert(Alert.AlertType type, String title, String message)
    }

    class BaseStatisticController {
        <<abstract>>
    
        #void exportSingleFormat(Text statusText, String fileName, Map~String, ?~ data, String title, String format)
        #void exportReimbursementJson(Text statusText, String fileName, Map~String, Object~ userDetails, double total)
        #void exportData(Map~String, ?~ data, String fileNamePrefix, String format, Text statusText)
        #void showSuccess(Text statusText, String message)
        #void showError(Text statusText, String message)
    }

     class ChangeReimbursementController {
        -TextField restaurantField
        -TextField supermarketField
    
        +void initialize()
        +void handleSave()
        +void handleExit(ActionEvent event)
        -void loadPage(String fxmlFile, ActionEvent event)
        -double validateInput(String input)
        -void showAlert(Alert.AlertType alertType, String title, String message)
    }

    class DeleteAdminUserController{
        -ChoiceBox type
        -ChoiceBox email
        -Label message

        +void initialize()
        +void updateEmailList()
        +void deleteAdminUser(ActionEvent event)
        +void cancelDeleteAdminUser(ActionEvent event)
        -void loadPage(String fxmlFile, ActionEvent event)
    }

    class EditInvoiceController {
        -Invoice invoice
        -ComboBox~Invoice~ invoiceComboBox
        -TextField amountField
        -DatePicker datePicker
        -ComboBox~String~ categoryComboBox
        -Button changeButton
    
        +void initialize()
        +void handleSaveChanges(ActionEvent event)
        +void handleResubmit(ActionEvent event)
        +void cancelEdit(ActionEvent event)
        -void configureComboBox(List~Invoice~ invoices)
        -void fillFields(Invoice invoice)
        -List~Invoice~ loadInvoicesFromResultSet(ResultSet rs)
        -void updateInvoiceInDatabase(Connection con, Invoice invoice)
        -void showAlert(String title, String message)
        -void loadPage(String fxmlFile, ActionEvent event)
    }

    class NotificationsController {
        -VBox notificationContainer
        -Logger LOGGER
    
        +void initialize()
        -void addNotificationToContainer(Notification notification)
        -boolean isInvoiceUploadNotification(String message)
        -void handleAcceptInvoice(Notification notification)
        -void handleDeclineInvoice(Notification notification)
        -String extractUserEmail(String message)
        -LocalDate extractInvoiceDate(String message)
        -void addInfoMessage(String message)
        -void deleteNotification(String notificationId, HBox notificationBox)
        +void closeWindow(ActionEvent event)
    }

    class NotificationsUserController {
        +void initialize()
        -void addMissingInvoiceNotifications(String email, List~Notification~ existingNotifications)
        -void addNotificationToContainer(Notification notification, String userEmail)
        -void addInfoMessage(String message)
        -void deleteNotification(String notificationId, HBox notificationBox, String userEmail)
        -void closeWindow()
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

    class StatisticNumberOfInvoicesController {
        +void initialize()
        +void handleExport()
        +void exportPdf() IOException
        +void exportCsv() IOException
        +void exportJson() IOException
        -void showAlert(Alert.AlertType, String, String)
        -void cancelNumberOfInvoices(ActionEvent) IOException
        Statistics statistics
    }

    class StatisticReimbursementPerMonthController {
        +void initialize()
        +void handleExport() SQLException
        +void cancelReimbursement(ActionEvent) IOException
        -void loadPage(String, ActionEvent) IOException
        Statistics statistics
        PauseTransition statusTimer
    }

    class StatisticsController {
        +void initialize()
        +void statisticDistributionRestaurantSupermarket(ActionEvent) IOException
        +void statisticNumberInvoicesPerMonth(ActionEvent) IOException
        +void statisticReimbursementPerMonth(ActionEvent) IOException
        +void cancelStatistics(ActionEvent) IOException
        -void loadPage(String, ActionEvent) IOException
        Statistics statistics
    }

    class StatisticSupermarketRestaurantController {
        +void initialize()
        +void handleExport(ActionEvent)
        +void cancelDistribution(ActionEvent) IOException
        -void loadPage(String, ActionEvent) IOException
        Statistics statistics
        PauseTransition statusTimer
    }

    class UserDashboardController {
        +void setCurrentUserEmail(String)
        +static String getCurrentUserEmail()
        +void initialize()
        -void loadInvoices()
        -void loadPieChart()
        +void handleEditInvoiceUser(ActionEvent) IOException
        +void handleUploadInvoice(ActionEvent) IOException
        +void handleLogoutUser(ActionEvent) IOException
        +void openNotifications(ActionEvent)
        static String currentUserEmail
        Statistics statistics
    }

    class CsvExporter {
        -String delimiter
        +CsvExporter()
        +CsvExporter(String)
        +void export(List<Map<String, String>>, String) throws IOException
        -String escapeCsv(String)
        -String generateFileName(String, String)
    }

    class JsonExporter {
        +void export(Map<String, ?>, String) throws IOException
        -String generateFileName(String, String)
    }

    class PdfExporter {
        +PdfExporter()
        +PDDocument getDocument()
        +PDPageContentStream getContentStream()
        +int getYPosition()
        +void setYPosition(int)
        +void startPage() throws IOException
        +void end() throws IOException
        +void addTitle(String) throws IOException
        +void addParagraph(String) throws IOException
        +void addTable(List<String>, List<List<String>>) throws IOException
        +void addImage(BufferedImage, float, float, float, float) throws IOException
        +void addImageAndMovePosition(BufferedImage, float, float, float, float) throws IOException
        +void saveToFile(String) throws IOException
        -void writeText(String) throws IOException
        -String generateFileName(String, String)
    }

    class AdminNotificationRepository {
        +static void addAdminNotification(Notification)
        +static List<Notification> getAllAdminNotifications()
        +static void deleteAdminNotification(String)
    }

    class DeletedNotificationRepository {
        +static void addDeletedNotification(String message, String userEmail)
        +static Set~String~ getDeletedNotifications(String userEmail)
        +static void clearDeletedNotificationsForUser(String userEmail)
        +static void clearAllDeletedNotifications()
    }

    class InvoiceRepository{
        +List~Invoice~ getAllInvoicesAdmin()
        +List~Invoice~ getAllInvoicesUser(String userEmail)
        +void saveInvoiceInfo(Connection, String, Date, double, Category, Status, String, LocalDateTime, double, File)
        +boolean invoiceExists(Connection, String, Date)
        +void updateInvoiceDate(Invoice)
        +void updateInvoiceAmount(Invoice)
        +void updateInvoiceStatus(Invoice)
        +void updateInvoiceCategory(Invoice)
        +void updateInvoiceReimbursement(Invoice)
        +List~Invoice~ getDeclinedInvoicesCurrentMonth(String userEmail)
        +List~Invoice~ getAcceptedInvoicesCurrentMonth(String userEmail)
        +void updateInvoice(Invoice)
        +List~String~ getActiveUsersThisMonth()
        +int getInvoiceCountForUserThisMonth(String userEmail)
        +double getTotalReimbursementForUserThisMonth(String userEmail)
        +double getTotalReimbursementThisMonth()
        +void deleteInvoice(Invoice)
    }

    class UserRepository {
        +void addUser(User user)
        +List~String~ getAllAdminEmails()
        +List~String~ getAllUserEmails()
        +List~String~ getAllUsersWithoutLoggedAdmin(String eMail)
        +boolean deleteUser(String email)
        +User findByEmailAndPassword(String email, String password)
        +User getByEmail(String email)
    }

    class ExportUtils {
        <<utility>>
        +boolean exportToJson(Map~String, ?~ data, String fileName)
        +boolean exportToCsv(Map~String, ?~ data, String baseFileName)
        +boolean exportToPdf(Map~String, ?~ data, String title, String baseFileName)
    }

    class NotificationManager {
        -List~Notification~ globalNotifications
        -Map~String, List~Notification~~ userNotifications
        -Map~String, Set~String~~ deletedNotificationMessages
        -NotificationManager instance
        -NotificationManager()

        +static NotificationManager getInstance()
        +void loadDeletedNotificationsForUser(String userEmail)
        +void addNotification(Notification notification)
        +void addNotificationForUser(String userEmail, Notification notification)
        +List~Notification~ getNotifications()
        +List~Notification~ getNotificationsForUser(String userEmail)
        +List~Notification~ getUserSpecificNotifications(String userEmail)
        +boolean isNotificationDeleted(String userEmail, String message)
        +void markNotificationAsDeleted(String userEmail, String message)
        +boolean removeNotification(String notificationId)
        +boolean removeNotification(String notificationId, boolean markAsDeleted)
        +boolean removeUserNotification(String userEmail, String notificationId)
        +boolean removeNotificationForUser(String userEmail, String notificationId)
        +void clearAllNotifications()
        +void clearUserNotifications(String userEmail)
        +void clearDeletedNotificationsForUser(String userEmail)
        +void clearAllDeletedNotifications()
    }

    User <|-- Administrator
    BaseStatisticController <|-- StatisticNumberOfInvoicesController
    BaseStatisticController <|-- StatisticReimbursementPerMonthController
    BaseStatisticController <|-- StatisticSupermarketRestaurantController
    Application <|-- StartApplication

    Administrator --> User : manages
    Administrator --> Invoice : processes
    Administrator --> Statistics : uses
    Administrator --> Notification : sends
    Administrator --> Category : modifies
    Administrator --> Status : sets
    
    AddAdminController --> User : creates
    AddInvoiceController --> Invoice : creates
    AddInvoiceController --> File : uploads
    AddInvoiceController --> OCRService : analyzes
    
    AdminDashboardController --> Invoice : views
    AdminDashboardController --> Administrator : controls
    AdminDashboardController --> Notification : opens
    
    AdminInvoiceManagementController --> Invoice : modifies
    AdminInvoiceManagementController --> User : selects
    
    AnomalyDetection --> Invoice : checks
    AnomalyDetection --> OCRService : compares
    
    ChangeReimbursementController --> Category : updates
    
    CloudOCRService --> OCRResult : returns
    CloudOCRService --> File : reads
    
    DatabaseConnection --> Connection : provides
    
    DeletedNotificationRepository --> DatabaseConnection : uses
    AdminNotificationRepository --> DatabaseConnection : uses
    UserRepository --> DatabaseConnection : uses
    InvoiceRepository --> DatabaseConnection : uses
    
    EditInvoiceController --> Invoice : edits
    EditInvoiceController --> Connection : commits
    
    Invoice --> Category : uses
    Invoice --> Status : uses
    Invoice --> File : validates
    
    InvoiceRepository --> Invoice : persists
    InvoiceRepository --> Connection : uses
    InvoiceRepository --> ResultSet : uses
    
    JsonExporter --> Map : formats

    Notification --> User : notifies
    Notification --> List~String~ : stores
    
    NotificationManager --> Notification : manages
    NotificationManager --> DeletedNotificationRepository : tracks
    NotificationManager --> AdminNotificationRepository : syncs
    
    OCRService --> DatabaseConnection : stores
    OCRService --> OCRResult : returns
    
    PdfExporter --> BufferedImage : inserts
    PdfExporter --> Invoice : renders
    
    StartController --> User : logs in
    StartController --> Administrator : logs in
    
    Statistics --> UserInvoiceData : aggregates
    Statistics --> Invoice : analyzes
    Statistics --> Category : aggregates
    Statistics --> Status : filters
    
    StatisticsController --> Statistics : uses
    
    StatisticNumberOfInvoicesController --> Statistics : analyzes
    StatisticReimbursementPerMonthController --> Statistics : analyzes
    StatisticSupermarketRestaurantController --> Statistics : analyzes
    
    User --> Invoice : owns
    User --> Notification : receives
    
    UserDashboardController --> Invoice : shows
    UserDashboardController --> Statistics : uses
    UserDashboardController --> Notification : opens
    
    UserInvoiceData --> User : summarizes
    
    UserSession --> User : stores

