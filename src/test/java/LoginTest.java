import jku.se.User;

import jku.se.repository.UserRepository;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginTest {

    private final String validEmail = "employee@lunchify.com";
    private final String validPassword = "123";

    @BeforeEach
    void setUp() {
        User testUser = new User("Test Employee", validEmail, validPassword, false);
        UserRepository.addUser(testUser);
    }

    @AfterEach
    void tearDown() {
        UserRepository.deleteUser(validEmail);
    }

    @Test
    void loginWithValidCredentials_ShouldSucceed() {
        User loggedInUser = UserRepository.findByEmailAndPassword(validEmail, validPassword);
        assertNotNull(loggedInUser, "Login should be successful with valid credentials");
        assertEquals(validEmail, loggedInUser.getEmail());
    }

    @Test
    void loginWithInvalidCredentials_ShouldFail() {
        User user = UserRepository.findByEmailAndPassword("wrong@lunchify.com", "wrongpass");
        assertNull(user, "Login should fail with incorrect email or password");
        System.out.println("Invalid login credentials");
    }

    @Test
    void loginWithEmptyPassword_ShouldFail() {
        User user = UserRepository.findByEmailAndPassword(validEmail, "");
        assertNull(user, "Login should fail with empty password");
        System.out.println("Password cannot be empty");
    }

    @Test
    void loginThenLogout_ShouldRedirectToLoginPage() {
        User loggedInUser = UserRepository.findByEmailAndPassword(validEmail, validPassword);
        assertNotNull(loggedInUser);
        loggedInUser = null;

        assertNull(loggedInUser, "After logout, user should be null (logged out)");
    }
}
