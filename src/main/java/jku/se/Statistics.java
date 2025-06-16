package jku.se;

import jku.se.repository.InvoiceRepository;
import jku.se.repository.UserRepository;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * The {@code Statistics} class provides various statistical evaluations
 * based on invoices and reimbursements stored in the system.
 * <p>
 * It is primarily used by administrators to:
 * <ul>
 *   <li>Analyze invoice submission trends over time</li>
 *   <li>Track reimbursements by month and user</li>
 *   <li>Calculate averages and category-specific counts</li>
 *   <li>Generate reports for user-specific and monthly reimbursements</li>
 * </ul>
 * <p>
 * The data is retrieved from the {@link InvoiceRepository} and {@link UserRepository}.
 */
public class Statistics {

    //AI
    /**
     * Calculates the number of invoices submitted per calendar month.
     *
     * @return Map with month names as keys and number of invoices as values.
     */
    public Map<String, Integer> getInvoicesPerMonth() {
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();
        Map<Integer, Integer> countPerMonth = new TreeMap<>();

        for (Invoice invoice : invoices) {
            int monthNumber = invoice.getDate().getMonthValue(); // 1–12
            countPerMonth.put(monthNumber, countPerMonth.getOrDefault(monthNumber, 0) + 1);
        }
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : countPerMonth.entrySet()) {
            String monthName = getMonthName(entry.getKey());
            result.put(monthName, entry.getValue());
        }

        return result;
    }

    /**
     * Groups invoice counts per user for each month.
     *
     * @return Map with month names as keys and maps of user data as values.
     */
    public Map<String, Map<String, UserInvoiceData>> getInvoicesPerUserAndMonth() {
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();
        Map<String, Map<String, UserInvoiceData>> result = new LinkedHashMap<>(); // Month -> (UserEmail -> Data)

        for (Invoice invoice : invoices) {
            String monthName = getMonthName(invoice.getDate().getMonthValue());
            String userEmail = invoice.getUserEmail();

            result.putIfAbsent(monthName, new LinkedHashMap<>());
            Map<String, UserInvoiceData> userMap = result.get(monthName);

            if (!userMap.containsKey(userEmail)) {
                User user = UserRepository.getByEmail(userEmail);
                String userName = user != null ? user.getName() : "Unknown";
                userMap.put(userEmail, new UserInvoiceData(userName, userEmail, 1));
            } else {
                UserInvoiceData data = userMap.get(userEmail);
                data.setInvoiceCount(data.getInvoiceCount() + 1);
            }
        }
        return result;
    }

    /**
     * Calculates the total reimbursement amount per month.
     *
     * @return Map with month names as keys and reimbursement sums as values.
     */
    public Map<String, Double> getReimbursementPerMonth() {
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();
        Map<Integer, Double> sumPerMonth = new TreeMap<>(); // automatically sorted by month

        for (Invoice invoice : invoices) {
            int monthNumber = invoice.getDate().getMonthValue(); // 1–12
            double reimbursement = invoice.getReimbursement();
            sumPerMonth.put(monthNumber, sumPerMonth.getOrDefault(monthNumber, 0.0) + reimbursement);
        }

        Map<String, Double> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Double> entry : sumPerMonth.entrySet()) {
            String monthName = getMonthName(entry.getKey());
            result.put(monthName, entry.getValue());
        }

        return result;
    }

    /**
     * Calculates the total reimbursement over the last 12 months.
     *
     * @return Sum of reimbursements from the last year.
     */
    public double getReimbursementForAYear(){
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();
        LocalDate startDate = LocalDate.now().minusYears(1);
        double sumReimbursement = 0.0;

        for (Invoice invoice : invoices) {
            if (invoice.getDate().isAfter(startDate) && invoice.getDate().isBefore(LocalDate.now())) {
                sumReimbursement += invoice.getReimbursement();
            }
        }
        return sumReimbursement;
    }

    /**
     * Computes the average number of invoices submitted per user per month.
     *
     * @return Average invoices per user per month.
     */
    public double getAverageOfInvoicesPerUserPerMonth(){
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();
        List<String> users = UserRepository.getAllUserEmails();
        double countInvoices = invoices.size();
        double countUsers= users.size();

        //get sum of months for average
        LocalDate earliestDate = invoices.get(0).getDate();
        LocalDate latestDate = invoices.get(0).getDate();

        for (Invoice invoice : invoices) {
            LocalDate date = invoice.getDate();
            if (date.isBefore(earliestDate)) earliestDate = date;
            if (date.isAfter(latestDate)) latestDate = date;
        }

        //ai generated
        long monthsBetween = ChronoUnit.MONTHS.between(
                YearMonth.from(earliestDate),
                YearMonth.from(latestDate)
        ) + 1;

        return countInvoices / monthsBetween / countUsers;
    }

    /**
     * Counts all supermarket invoices from all users.
     *
     * @return Total number of supermarket invoices.
     */
    public int getInvoicesPerSupermarket(){
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();
        int countSupermarket = 0;

        for(Invoice invoice : invoices){
            if(invoice.getCategory().equals(Category.SUPERMARKET)){
                countSupermarket++;
            }
        }
        return countSupermarket;
    }

    /**
     * Counts all restaurant invoices from all users.
     *
     * @return Total number of restaurant invoices.
     */
    public int getInvoicesPerRestaurant(){
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();
        int countRestaurant = 0;

        for(Invoice invoice : invoices){
            if(invoice.getCategory().equals(Category.RESTAURANT)){
                countRestaurant++;
            }
        }
        return countRestaurant;
    }

    /**
     * Counts all supermarket invoices for a specific user.
     *
     * @param currentUser The user's email.
     * @return Number of supermarket invoices for the user.
     */
    public int getInvoicesPerSupermarketUser(String currentUser){
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesUser(currentUser);
        int countSupermarket = 0;

        for(Invoice invoice : invoices){
            if(invoice.getCategory().equals(Category.SUPERMARKET)){
                countSupermarket++;
            }
        }
        return countSupermarket;
    }

    /**
     * Counts all restaurant invoices for a specific user.
     *
     * @param currentUser The user's email.
     * @return Number of restaurant invoices for the user.
     */
    public int getInvoicesPerRestaurantUser(String currentUser){
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesUser(currentUser);
        int countRestaurant = 0;

        for(Invoice invoice : invoices){
            if(invoice.getCategory().equals(Category.RESTAURANT)){
                countRestaurant++;
            }
        }
        return countRestaurant;
    }

    /**
     * Converts a numeric month (1–12) to its English full name (e.g., "January").
     *
     * @param monthNumber The number of the month.
     * @return Name of the month.
     */
    private String getMonthName(int monthNumber) {
        return Month.of(monthNumber)
                .getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH);
    }

    /**
     * Retrieves invoice count and total reimbursement for all active users in the current month.
     *
     * @return Map of user emails to their invoice/reimbursement stats and a total.
     * @throws SQLException if a database error occurs.
     */
    public Map<String, Object> getUserReimbursementDetails() throws SQLException {
        Map<String, Object> result = new LinkedHashMap<>();
        double total = 0.0;

        for (String userEmail : InvoiceRepository.getActiveUsersThisMonth()) {
            int count = InvoiceRepository.getInvoiceCountForUserThisMonth(userEmail);
            double sum = InvoiceRepository.getTotalReimbursementForUserThisMonth(userEmail);

            result.put(userEmail, Map.of(
                    "invoice_count", count,
                    "total_reimbursement", sum
            ));
            total += sum;
        }

        result.put("total_all_users", total);
        result.put("month", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        return result;
    }

    /**
     * Delivers user reimbursement data grouped by month and user.
     * Each user map contains, for example, "name", "email", "total_reimbursement".
     */
    public Map<String, Map<String, Map<String, Object>>> getUserReimbursementDetailsPerMonth() throws SQLException {
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();

        Map<String, Map<String, Map<String, Object>>> result = new LinkedHashMap<>();

        for (Invoice invoice : invoices) {
            String monthName = getMonthName(invoice.getDate().getMonthValue());
            String userEmail = invoice.getUserEmail();

            User user = UserRepository.getByEmail(userEmail);
            String userName = user != null ? user.getName() : "Unknown";

            result.putIfAbsent(monthName, new LinkedHashMap<>());
            Map<String, Map<String, Object>> usersInMonth = result.get(monthName);

            usersInMonth.putIfAbsent(userEmail, new LinkedHashMap<>());
            Map<String, Object> userData = usersInMonth.get(userEmail);

            Object prevObj = userData.get("total_reimbursement");
            double prevSum;
            if (prevObj instanceof Double) {
                prevSum = (Double) prevObj;
            } else if (prevObj instanceof Number) {
                prevSum = ((Number) prevObj).doubleValue();
            } else {
                prevSum = 0.0;
            }

            userData.put("name", userName);
            userData.put("email", userEmail);
            userData.put("total_reimbursement", prevSum + invoice.getReimbursement());
        }

        return result;
    }

}
