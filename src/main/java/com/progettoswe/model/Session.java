package com.progettoswe.model;
import com.progettoswe.ORM.UserDAO;

public class Session {
    private static String userEmail;
    private static Utente utente;

    public static void setUserEmail(String email) {
        userEmail = email;
        utente = UserDAO.utente(email); // Aggiorna l'oggetto Utente quando viene impostata l'email
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static Utente getUtente() {
        return utente;
    }

    public static void setUtente(Utente user) {
        utente = user;
    }
}
