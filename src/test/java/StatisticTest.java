

import jku.se.Category;
import jku.se.Invoice;
import jku.se.Statistics;
import jku.se.Status;
import jku.se.repository.InvoiceRepository;
import jku.se.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatisticTest {

    //ai
    private Statistics statistics;

    @BeforeEach
    void setUp() {
        statistics = new Statistics();
    }

    //test 1 total Reimbursement per Month
    @Test
    void testGetReimbursementPerMonth() {
        List<Invoice> invoices = List.of(
                new Invoice("user1", LocalDate.of(2024, 5, 1), 10.0, Category.RESTAURANT, Status.APPROVED, "", null, 3.0),
                new Invoice("user2", LocalDate.of(2024, 5, 2), 8.0, Category.SUPERMARKET, Status.APPROVED, "", null, 2.5)
        );

        try (MockedStatic<InvoiceRepository> mocked = mockStatic(InvoiceRepository.class)) {
            mocked.when(InvoiceRepository::getAllInvoicesAdmin).thenReturn(invoices);

            Map<String, Double> result = statistics.getReimbursementPerMonth();
            assertEquals(1, result.size());
            assertEquals(5.5, result.get("May"));
        }
    }

    //test 2 totalreimbursement for a year
    @Test
    void testGetReimbursementForAYear() {
        List<Invoice> invoices = List.of(
                new Invoice("user", LocalDate.now().minusMonths(2), 12.0, Category.SUPERMARKET, Status.APPROVED, "", null, 2.5),
                new Invoice("user", LocalDate.now().minusMonths(6), 15.0, Category.RESTAURANT, Status.APPROVED, "", null, 3.0)
        );

        try (MockedStatic<InvoiceRepository> mocked = mockStatic(InvoiceRepository.class)) {
            mocked.when(InvoiceRepository::getAllInvoicesAdmin).thenReturn(invoices);

            double total = statistics.getReimbursementForAYear();
            assertEquals(5.5, total);
        }
    }

    //test 3 check  restaurant vs. supermarket distribution
    @Test
    void testGetInvoicesPerSupermarketAndRestaurant() {
        List<Invoice> invoices = List.of(
                new Invoice("user", LocalDate.now(), 10.0, Category.SUPERMARKET, Status.APPROVED, "", null, 2.5),
                new Invoice("user", LocalDate.now(), 20.0, Category.RESTAURANT, Status.APPROVED, "", null, 3.0),
                new Invoice("user", LocalDate.now(), 15.0, Category.RESTAURANT, Status.APPROVED, "", null, 3.0)
        );

        try (MockedStatic<InvoiceRepository> mocked = mockStatic(InvoiceRepository.class)) {
            mocked.when(InvoiceRepository::getAllInvoicesAdmin).thenReturn(invoices);

            assertEquals(1, statistics.getInvoicesPerSupermarket());
            assertEquals(2, statistics.getInvoicesPerRestaurant());
        }
    }


    //test 4 View invoices submitted per user per month
    @Test
    void testGetAverageInvoicesPerUserPerMonth() {
        List<Invoice> invoices = List.of(
                new Invoice("user1", LocalDate.of(2024, 1, 15), 10.0, Category.RESTAURANT, Status.APPROVED, "", null, 3.0),
                new Invoice("user1", LocalDate.of(2024, 2, 15), 10.0, Category.SUPERMARKET, Status.APPROVED, "", null, 2.5),
                new Invoice("user2", LocalDate.of(2024, 2, 20), 8.0, Category.SUPERMARKET, Status.APPROVED, "", null, 2.5)
        );

        List<String> users = List.of("user1", "user2");

        try (
                MockedStatic<InvoiceRepository> mockedInvoice = mockStatic(InvoiceRepository.class);
                MockedStatic<UserRepository> mockedUser = mockStatic(UserRepository.class)
        ) {
            mockedInvoice.when(InvoiceRepository::getAllInvoicesAdmin).thenReturn(invoices);
            mockedUser.when(UserRepository::getAllUserEmails).thenReturn(users);

            double average = statistics.getAverageOfInvoicesPerUserPerMonth();
            assertTrue(average > 0, "Average should be > 0");
        }
    }
}
