package jku.se;

import jku.se.repository.InvoiceRepository;
import jku.se.repository.UserRepository;
import org.jfree.data.time.Day;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Statistics {
    //überarbeiten
    public Map<String, Integer> getInvoicesPerMonth() {
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();
        Map<String, Integer> countPerMonth = new TreeMap<>();

        for (Invoice invoice : invoices) {
            Month month = invoice.getDate().getMonth();
            String monthName = month.toString(); // z.B. "JANUARY"
            countPerMonth.put(monthName, countPerMonth.getOrDefault(monthName, 0) + 1);
        }

        return countPerMonth;
    }
    //überarbeiten
    public Map<String, Double> getReimbursementPerMonth() {
        List<Invoice> invoices = InvoiceRepository.getAllInvoicesAdmin();
        Map<String, Double> sumPerMonth = new TreeMap<>();

        for (Invoice invoice : invoices) {
            String month = invoice.getDate().getMonth().toString();
            double amount = invoice.getAmount(); // oder getReimbursedAmount()
            sumPerMonth.put(month, sumPerMonth.getOrDefault(month, 0.0) + amount);
        }

        return sumPerMonth;
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
}
