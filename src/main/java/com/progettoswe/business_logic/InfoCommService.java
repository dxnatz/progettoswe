package com.progettoswe.business_logic;

import com.progettoswe.ORM.InfoCommDAO;

import java.util.ArrayList;

public class InfoCommService {

    public static ArrayList<String> getCommentiOpera() {
        return InfoCommDAO.getCommentiOpera();
    }

    public static boolean aggiungiCommento(String commento, int idEdizione) {
        return InfoCommDAO.aggiungiCommento(commento, idEdizione);
    }

    public static boolean utenteHaCommentato(int idEdizione) {
        return InfoCommDAO.utenteHaCommentato(idEdizione);
    }

    public static boolean modificaCommento(String commento, int idEdizione) {
        return InfoCommDAO.modificaCommento(commento, idEdizione);
    }

    public static String getCommentoUtente(int idEdizione) {
        return InfoCommDAO.getCommentoUtente(idEdizione);
    }

    public static void eliminaCommento(int idEdizione) {
        InfoCommDAO.eliminaCommento(idEdizione);
    }
}
