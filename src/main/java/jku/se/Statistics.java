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

public class Statistics {

    //ai
    //method für statistic invoices per month
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

    public Map<String, Map<String, UserInvoiceData>> getInvoicesPerUserAndMonth() {
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();
        Map<String, Map<String, UserInvoiceData>> result = new LinkedHashMap<>(); // Monat -> (UserEmail -> Daten)

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



    //method für statistic reimbursement per month
    public Map<String, Double> getReimbursementPerMonth() {
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();
        Map<Integer, Double> sumPerMonth = new TreeMap<>(); // automatisch nach Monat sortiert

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

    //refund for the last 12 months
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

    //Invoices submitted on average per user per month
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

    //for statistic overview invoices from supermarket, restaurant
    public int getInvoicesPerSupermaket(){
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();
        int countSupermarket = 0;

        for(Invoice invoice : invoices){
            if(invoice.getCategory().equals(Category.SUPERMARKET)){
                countSupermarket++;
            }
        }
        return countSupermarket;
    }

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

    //for statistic overview invoices from supermarket, restaurant - UserDashboard
    public int getInvoicesPerSupermaketUser(String currentUser){
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesUser(currentUser);
        int countSupermarket = 0;

        for(Invoice invoice : invoices){
            if(invoice.getCategory().equals(Category.SUPERMARKET)){
                countSupermarket++;
            }
        }
        return countSupermarket;
    }

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
    //switch number of months to names
    private String getMonthName(int monthNumber) {
        return Month.of(monthNumber)
                .getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH);
    }

    // User-Details für Reimbursement-Export
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
     * Liefert User-Reimbursement-Daten gruppiert nach Monat und User.
     * Jede User-Map enthält z.B. "name", "email", "total_reimbursement".
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
