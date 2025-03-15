package com.progettoswe.business_logic;

import com.progettoswe.ORM.UserDAO;
import com.progettoswe.model.Utente;

public class UserService {

    public static boolean login(String email, String password) {

        if(UserDAO.login(email, password)) {
            return true;
        }
        return false;
            
    }

    public static  boolean checkPassword(String email, String password) {
        if(!UserDAO.emailCorrettaPasswordErrata(email, password)) {
            return true;
        }
        return false;
    }

    public static boolean emailEsistente(String email) {
        if(UserDAO.emailEsistente(email)) {
            return true;
        }
        return false;
    }

    public static boolean cfEsistente(String cf) {
        if(UserDAO.cfEsistente(cf)) {
            return true;
        }
        return false;
    }

    public static boolean cellulareEsistente(String cellulare) {
        if(UserDAO.cellulareEsistente(cellulare)) {
            return true;
        }
        return false;
    }

    public static boolean inserimentoUtente(Utente utente) {
        if(UserDAO.inserimentoUtente(utente)) {
            return true;
        }
        return false;
    }

    public static boolean updateUtente(Utente utente) {
        if(UserDAO.updateUtente(utente)) {
            return true;
        }
        return false;
    }

    public static boolean deleteUtente(Utente utente) {
        if(UserDAO.deleteUtente(utente)) {
            return true;
        }
        return false;
    }
}