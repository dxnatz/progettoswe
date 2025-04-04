package com.progettoswe.business_logic;

import com.progettoswe.ORM.EdizioneDAO;
import com.progettoswe.model.Edizione;
import com.progettoswe.ORM.EdizioneDAO;

import java.sql.SQLException;
import java.util.List;

public class EdizioneService {

    public static Edizione getEdizione(int idEdizione) {
        return EdizioneDAO.getEdizioneById(idEdizione);
    }

    public static Edizione getEdizioneByIsbn(String isbn) {
        return EdizioneDAO.getEdizioneByIsbn(isbn);
    }

    public static int aggiungiEdizione(Edizione edizione) {
        if (EdizioneDAO.existsEdizioneByIsbn(edizione.getIsbn())) {
            return -1; // ISBN già esistente
        }
        return EdizioneDAO.insertEdizione(edizione);
    }

    public static boolean modificaEdizione(Edizione edizione) {
        return EdizioneDAO.updateEdizione(edizione);
    }

    public static boolean rimuoviEdizione(int idEdizione) {
        return EdizioneDAO.deleteEdizione(idEdizione);
    }

    public static Edizione getEdizione(String titolo, int edizione) {
        return EdizioneDAO.getEdizione(titolo, edizione);
    }

    public static boolean verificaEsistenzaIsbn(String isbn) {
        return EdizioneDAO.existsEdizioneByIsbn(isbn);
    }

    public static List<Edizione> getAllEdizioni() throws SQLException {
        return EdizioneDAO.getAllEdizioni();
    }

    public static List<Edizione> searchEdizioni(String searchTerm) throws SQLException {
        return EdizioneDAO.searchEdizioni(searchTerm);
    }
}