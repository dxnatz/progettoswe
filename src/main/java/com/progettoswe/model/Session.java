package com.progettoswe.model;
import com.progettoswe.ORM.UserDAO;

import java.awt.color.ICC_ColorSpace;

public class Session {
    public static final String ADMIN_EMAIL = "@biblioteca.it";
    private static String userEmail;
    private static Utente utente;
    private static String nomeOpera;
    private static int edizione;

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

    public static String getNomeOpera() {
        return nomeOpera;
    }

    public static void setNomeOpera(String nomeOpera) {
        Session.nomeOpera = nomeOpera;
    }

    public static int getEdizione() {
        return edizione;
    }

    public static void setEdizione(String edizione) {
        Session.edizione = Integer.parseInt(edizione);
    }
}
