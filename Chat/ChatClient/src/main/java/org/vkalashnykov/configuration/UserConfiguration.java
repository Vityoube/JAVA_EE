package org.vkalashnykov.configuration;

/**
 * Created by vkalashnykov on 30.12.16.
 */
public class UserConfiguration {
    private static String currentUsername;
    private static String currentStatus;

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    public static String getCurrentStatus() {
        return currentStatus;
    }

    public static void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
}
