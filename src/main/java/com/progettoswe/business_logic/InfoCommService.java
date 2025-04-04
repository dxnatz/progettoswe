package com.progettoswe.business_logic;

import com.progettoswe.ORM.InfoCommDAO;
import com.progettoswe.model.Commento;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public static boolean aggiungiCommentoVolume(int idPrestito, String testo) {
        return InfoCommDAO.aggiungiCommentoVolume(idPrestito, testo);
    }

    public static List<Commento> getCommentiOperaCompleti(int idOpera) throws SQLException {
        return InfoCommDAO.getCommentiCompletiPerOpera(idOpera);
    }

    public static String getTitoloOpera(int idOpera) throws SQLException {
        return InfoCommDAO.getTitoloOpera(idOpera);
    }

    public static List<Commento> getCommentiVolumeCompleti(int idVolume) throws SQLException {
        return InfoCommDAO.getCommentiCompletiPerVolume(idVolume);
    }
}