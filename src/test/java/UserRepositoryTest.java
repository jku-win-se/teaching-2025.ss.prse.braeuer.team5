

import jku.se.User;
import jku.se.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest {
    private UserRepository userRepository;
    public static String USER = "postgres.dljjtuynbgxgmhkcdypu";
    public static String PWD = "LunchifyTeam5!";
    private static final String URL = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
    }

    @AfterEach
    void tearDown() {
        // Clean up any user that might have been added during tests.
        try (Connection connection = DriverManager.getConnection(URL, USER, PWD)) {
            // Delete the test user if it exists in the database.
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM userlunchify WHERE email = ?")) {
                statement.setString(1, "karl.test@lunchify.com");
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM userlunchify WHERE email = ?")) {
                statement.setString(1, "karl.löschen@lunchify.com");
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //test Admin addUser
    @Test
    void testAddUser() {

        User testUser = new User("Karl Test", "karl.test@lunchify.com", "test123", false);

        userRepository.addUser(testUser);  // add testuser to db

        User userFromDb = userRepository.findByEmailAndPassword("karl.test@lunchify.com", "test123");
        assertNotNull(userFromDb);  // check if User exists
        assertEquals(testUser.getEmail(), userFromDb.getEmail());  //check if email of testuser and userfromdb is equal

    }

    //test Admin deleteUser
   /* @Test
    void testDeleteUser() {

        User deleteUser = new User("Karl Löschen", "karl.löschen@lunchify.com", "test123", false);

        userRepository.addUser(deleteUser);  // add testuser to db

        User userFromDb = userRepository.findByEmailAndPassword("karl.löschen@lunchify.com", "test123");
        assertNotNull(userFromDb, "User should exist before deletion");

        // Try to delete the user and assert success.
        boolean deleteSuccess = userRepository.deleteUser(deleteUser.getEmail());  // Use only email
        assertTrue(deleteSuccess, "User should be deleted successfully");

        // Ensure the user is deleted by trying to retrieve the user again.
        userFromDb = userRepository.findByEmailAndPassword("karl.löschen@lunchify.com", "test123");
        assertNull(userFromDb, "User should not exist after deletion");
    }

    */
}

