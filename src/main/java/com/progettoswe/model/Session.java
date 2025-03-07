package com.progettoswe.model;

public class Session {
    private static String userEmail;

    public static void setUserEmail(String email) {
        userEmail = email;
    }

    public static String getUserEmail() {
        return userEmail;
    }
}
