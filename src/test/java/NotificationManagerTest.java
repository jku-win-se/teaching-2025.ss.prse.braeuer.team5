import jku.se.Utilities.NotificationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertSame;

class NotificationManagerTest {
    @BeforeEach
    void resetSingleton(){
        NotificationManager.getInstance().getNotifications().clear();
    }

    @Test
    void testGetInstance_ShouldAlwaysReturnSameInstance(){
        NotificationManager instance1 = NotificationManager.getInstance();
        NotificationManager instance2 = NotificationManager.getInstance();

        assertSame(instance1, instance2, "Both instances should be the same");
    }

}

