package com.progettoswe.model;
import com.progettoswe.ORM.UserDAO;

public class Session {
    private static String userEmail;

    public static void setUserEmail(String email) {
        userEmail = email;
    }

    public static String getUserEmail() {
        return userEmail;
    }
    public static Utente getUtente(){
        return UserDAO.utente(userEmail);
    }
}
