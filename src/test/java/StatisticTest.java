

import jku.se.*;
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

    @Test
    void testGetInvoicesPerMonth() {
        List<Invoice> invoices = List.of(
                new Invoice("user1", LocalDate.of(2024, 3, 10), 12.0, Category.RESTAURANT, Status.APPROVED, "", null, 3.0),
                new Invoice("user2", LocalDate.of(2024, 3, 15), 18.0, Category.SUPERMARKET, Status.APPROVED, "", null, 2.5),
                new Invoice("user2", LocalDate.of(2024, 4, 5), 20.0, Category.SUPERMARKET, Status.APPROVED, "", null, 2.0)
        );

        try (MockedStatic<InvoiceRepository> mocked = mockStatic(InvoiceRepository.class)) {
            mocked.when(InvoiceRepository::getAllInvoicesAdmin).thenReturn(invoices);

            Map<String, Integer> result = statistics.getInvoicesPerMonth();

            assertEquals(2, result.size());
            assertEquals(2, result.get("March"));
            assertEquals(1, result.get("April"));
        }
    }

    @Test
    void testGetInvoicesPerUserAndMonth() {
        List<Invoice> invoices = List.of(
                new Invoice("user1@example.com", LocalDate.of(2024, 5, 10), 15.0, Category.RESTAURANT, Status.APPROVED, "", null, 3.0),
                new Invoice("user2@example.com", LocalDate.of(2024, 5, 12), 20.0, Category.SUPERMARKET, Status.APPROVED, "", null, 2.0),
                new Invoice("user1@example.com", LocalDate.of(2024, 5, 15), 10.0, Category.RESTAURANT, Status.APPROVED, "", null, 3.0)
        );

        try (
                MockedStatic<InvoiceRepository> mockedInvoice = mockStatic(InvoiceRepository.class);
                MockedStatic<UserRepository> mockedUser = mockStatic(UserRepository.class)
        ) {
            mockedInvoice.when(InvoiceRepository::getAllInvoicesAdmin).thenReturn(invoices);
            mockedUser.when(() -> UserRepository.getByEmail("user1@example.com"))
                    .thenReturn(new User("User One", "user1@example.com", "", false));
            mockedUser.when(() -> UserRepository.getByEmail("user2@example.com"))
                    .thenReturn(new User("User Two", "user2@example.com", "", false));

            Map<String, Map<String, UserInvoiceData>> result = statistics.getInvoicesPerUserAndMonth();

            assertEquals(1, result.size());
            assertTrue(result.containsKey("May"));
            assertEquals(2, result.get("May").size());
            assertEquals(2, result.get("May").get("user1@example.com").getInvoiceCount());
        }
    }

    @Test
    void testGetInvoicesPerSupermarketUser() {
        String userEmail = "test@lunchify.com";
        List<Invoice> invoices = List.of(
                new Invoice(userEmail, LocalDate.now(), 10.0, Category.SUPERMARKET, Status.APPROVED, "", null, 2.5),
                new Invoice(userEmail, LocalDate.now(), 20.0, Category.RESTAURANT, Status.APPROVED, "", null, 3.0)
        );

        try (MockedStatic<InvoiceRepository> mocked = mockStatic(InvoiceRepository.class)) {
            mocked.when(() -> InvoiceRepository.getAllInvoicesUser(userEmail)).thenReturn(invoices);

            assertEquals(1, statistics.getInvoicesPerSupermarketUser(userEmail));
            assertEquals(1, statistics.getInvoicesPerRestaurantUser(userEmail));
        }
    }

    @Test
    void testGetUserReimbursementDetails() throws Exception {
        try (
                MockedStatic<InvoiceRepository> mocked = mockStatic(InvoiceRepository.class)
        ) {
            mocked.when(InvoiceRepository::getActiveUsersThisMonth).thenReturn(List.of("user1@example.com"));
            mocked.when(() -> InvoiceRepository.getInvoiceCountForUserThisMonth("user1@example.com")).thenReturn(3);
            mocked.when(() -> InvoiceRepository.getTotalReimbursementForUserThisMonth("user1@example.com")).thenReturn(12.5);

            Map<String, Object> result = statistics.getUserReimbursementDetails();

            assertEquals(3, result.get("user1@example.com") instanceof Map ? ((Map<?, ?>) result.get("user1@example.com")).get("invoice_count") : null);
            assertTrue(result.containsKey("total_all_users"));
            assertTrue(result.containsKey("month"));
        }
    }

    @Test
    void testGetUserReimbursementDetailsPerMonth() throws Exception {
        List<Invoice> invoices = List.of(
                new Invoice("user1@example.com", LocalDate.of(2024, 3, 10), 10.0, Category.RESTAURANT, Status.APPROVED, "", null, 3.0),
                new Invoice("user1@example.com", LocalDate.of(2024, 3, 20), 15.0, Category.SUPERMARKET, Status.APPROVED, "", null, 2.0)
        );

        try (
                MockedStatic<InvoiceRepository> mockedInvoice = mockStatic(InvoiceRepository.class);
                MockedStatic<UserRepository> mockedUser = mockStatic(UserRepository.class)
        ) {
            mockedInvoice.when(InvoiceRepository::getAllInvoicesAdmin).thenReturn(invoices);
            mockedUser.when(() -> UserRepository.getByEmail("user1@example.com"))
                    .thenReturn(new User("Max Mustermann", "user1@example.com", "", false));

            Map<String, Map<String, Map<String, Object>>> result = statistics.getUserReimbursementDetailsPerMonth();

            assertTrue(result.containsKey("March"));
            Map<String, Object> userData = result.get("March").get("user1@example.com");
            assertEquals("Max Mustermann", userData.get("name"));
            assertEquals(5.0, userData.get("total_reimbursement"));
        }
    }
}
