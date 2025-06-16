package jku.se;

/**
 * The {@code UserSession} class manages the current user session in the application.
 * It provides static methods to set and retrieve the user who is currently logged in.
 */
public class UserSession {
    /** The currently logged-in user. */
    private static User currentUser;

    /**
     * Sets the current logged-in user.
     *
     * @param user the user to set as currently logged in
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Gets the current logged-in user.
     *
     * @return the current user, or {@code null} if no user is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }
}

