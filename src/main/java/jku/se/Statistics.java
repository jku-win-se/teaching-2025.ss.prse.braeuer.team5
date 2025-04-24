package jku.se;

import jku.se.repository.InvoiceRepository;
import jku.se.repository.UserRepository;
import org.jfree.data.time.Day;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Statistics {

    //ai
    //method für statistic invoices per month
    public Map<String, Integer> getInvoicesPerMonth() {
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();
        Map<Integer, Integer> countPerMonth = new TreeMap<>(); // 1=Januar, 2=Februar, usw.

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
            System.out.println("Rechnung Kategorie: " + invoice.getCategory());
            if(invoice.getCategory().equals(Category.SUPERMARKET)){
                countSupermarket++;
            }
            System.out.println("Anzahl der abgerufenen Rechnungen: " + invoices.size());
        }
        return countSupermarket;
    }

    public int getInvoicesPerRestaurantUser(String currentUser){
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesUser(currentUser);
        int countRestaurant = 0;

        for(Invoice invoice : invoices){
            System.out.println("Rechnung Kategorie: " + invoice.getCategory());
            if(invoice.getCategory().equals(Category.RESTAURANT)){
                countRestaurant++;
            }
            System.out.println("Anzahl der abgerufenen Rechnungen: " + invoices.size());
        }
        return countRestaurant;
    }
    //switch number of months to names
    private String getMonthName(int monthNumber) {
        return Month.of(monthNumber)
                .getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH);
    }
}
