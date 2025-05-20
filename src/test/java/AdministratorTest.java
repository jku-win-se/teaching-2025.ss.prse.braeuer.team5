
import jku.se.Administrator;
import jku.se.Category;
import jku.se.Invoice;
import jku.se.Status;
import jku.se.repository.InvoiceRepository;
import jku.se.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdministratorTest {

    private Administrator admin;

    @BeforeEach
    void setUp() {
        admin = new Administrator("Admin", "admin@example.com", "securepass");
    }

    //test admin add user
    @Test
    void testAddUser() {
        try (MockedStatic<UserRepository> mockedRepo = mockStatic(UserRepository.class)) {
            admin.addUser("Max", "max@example.com", "123", false);

            mockedRepo.verify(() ->
                    UserRepository.addUser(argThat(user ->
                            user.getEmail().equals("max@example.com") &&
                                    !user.isAdministrator()
                    )), times(1));
        }
    }

    //admin add an admin
    @Test
    void testAddAdministrator() {
        try (MockedStatic<UserRepository> mockedRepo = mockStatic(UserRepository.class)) {
            admin.addAdministrator("Lisa", "lisa@example.com", "123", true);

            mockedRepo.verify(() ->
                    UserRepository.addUser(argThat(user ->
                            user.getEmail().equals("lisa@example.com") &&
                                    user.isAdministrator()
                    )), times(1));
        }
    }

    //admin delete an user
    @Test
    void testDeleteUser() {
        try (MockedStatic<UserRepository> mockedRepo = mockStatic(UserRepository.class)) {
            mockedRepo.when(() -> UserRepository.deleteUser("someone@example.com"))
                    .thenReturn(true);

            boolean result = admin.deleteUser("someone@example.com");

            assertTrue(result);
            mockedRepo.verify(() -> UserRepository.deleteUser("someone@example.com"), times(1));
        }
    }

    //test admin approved an invoice
    @Test
    void testApproveInvoice() throws SQLException {
        Invoice invoice = mock(Invoice.class);

        try (MockedStatic<InvoiceRepository> mockedRepo = mockStatic(InvoiceRepository.class)) {
            admin.approveInvoice(invoice);

            verify(invoice).setStatus(Status.APPROVED);
            mockedRepo.verify(() -> InvoiceRepository.updateInvoiceStatus(invoice), times(1));
        }
    }

    //Test admin decline invoice
    @Test
    void testDeclineInvoice() throws SQLException {
        Invoice invoice = mock(Invoice.class);

        try (MockedStatic<InvoiceRepository> mockedRepo = mockStatic(InvoiceRepository.class)) {
            admin.declinedInvoice(invoice);

            verify(invoice).setStatus(Status.DECLINED);
            mockedRepo.verify(() -> InvoiceRepository.updateInvoiceStatus(invoice), times(1));
        }
    }

    //Test admin correct invoice
    @Test
    void testCorrectInvoice() {
        Invoice invoice = mock(Invoice.class);
        LocalDate newDate = LocalDate.now();
        Category newCategory = Category.SUPERMARKET;

        admin.correctInvoice(invoice, 99.9, newCategory, newDate);

        verify(invoice).correct(99.9, newCategory, newDate);
    }
}
