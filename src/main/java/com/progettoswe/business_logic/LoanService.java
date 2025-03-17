package com.progettoswe.business_logic;

import java.time.LocalDate;
import java.util.ArrayList;

import com.progettoswe.ORM.BookDAO;
import com.progettoswe.ORM.LoanDAO;
import com.progettoswe.model.Prestito;

import javafx.scene.control.ListView;

public class LoanService {
    
    //TODO
    public static void stampaTuttiPrestiti(ArrayList<Prestito> prestiti, ListView<String> listaPrestiti) {

    }

    public static void stampaPrestiti(ArrayList<Prestito> prestiti, ListView<String> listaPrestiti) {
        prestiti = LoanDAO.caricaPrestiti();
        for (int i = 0; i < prestiti.size(); i++) {
            String titolo = prestiti.get(i).getLibro().getTitolo();
            String autore = prestiti.get(i).getLibro().getAutore();
            LocalDate dataFine;
            String isbn = prestiti.get(i).getLibro().getIsbn();
            if(LoanDAO.rinnoviEsauriti(isbn) == 2){
                dataFine = prestiti.get(i).getDataInizioPrenotazione().plusDays(60);
            }else if(LoanDAO.rinnoviEsauriti(isbn) == 1){
                dataFine = prestiti.get(i).getDataInizioPrenotazione().plusDays(45);
            }else{
                dataFine = prestiti.get(i).getDataInizioPrenotazione().plusDays(30);
            }
            listaPrestiti.getItems().add(titolo + " - " + autore + " - Da restituire entro il: " + dataFine);
        }
    }

    public static boolean prenotaLibro(String selectedBook) {
        if(selectedBook != null){
            String [] bookDetails = selectedBook.split(" - ");
            String titolo = bookDetails[0];
            String autore = bookDetails[1];
            String isbn = getIsbnFromSelection(titolo, autore);
            if(isbn != null && libroDisponibile(isbn) && !libroGiaPrenotato(isbn) && !prestitiMassimiRaggiunti()){
                decrementaCopieLibro(isbn);
                return LoanDAO.prenotaLibro(isbn);
            }
        }
        return false;
    }

    private static boolean prestitiMassimiRaggiunti(){
        return LoanDAO.prestitiMassimiRaggiunti();
    }

    private static String getIsbnFromSelection(String titolo, String autore) {
        String isbn = BookDAO.ottieniIsbn(titolo, autore);
        return isbn;
    }

    private static boolean libroDisponibile(String isbn) {
        return BookDAO.libroDisponibile(isbn);
    }

    private static boolean libroGiaPrenotato(String isbn) {
        return LoanDAO.libroGiaPrenotato(isbn);
    }

    private static void decrementaCopieLibro(String isbn) {
        BookDAO.decrementaCopieLibro(isbn);
    }

    private static void aumentaCopieLibro(String isbn) {
        BookDAO.aumentaCopieLibro(isbn);
    }

    public static boolean annullaPrestito(String selectedLoan) {
        if(selectedLoan != null){
            String [] loanDetails = selectedLoan.split(" - ");
            String titolo = loanDetails[0];
            String autore = loanDetails[1];
            String isbn = getIsbnFromSelection(titolo, autore);
            if(isbn != null && LoanDAO.prestitoDaMenoDiTreGiorni(isbn)){
                aumentaCopieLibro(isbn);
                return LoanDAO.annullaPrestito(isbn);
            }
        }
        return false;
    }

    public static boolean prenotazioneScaduta(String dataFine) {
        if(dataFine != null){
            LocalDate dataOdiena = LocalDate.now();
            LocalDate dataControllo = LocalDate.parse(dataFine);
            dataControllo = dataControllo.minusDays(27);
            if(dataOdiena.isAfter(dataControllo)){
                return true;
            }
        }
        return false;
    }

    public static boolean prolungaPrestito(String selectedLoan) {
        if(selectedLoan != null){
            String [] loanDetails = selectedLoan.split(" - ");
            String titolo = loanDetails[0];
            String autore = loanDetails[1];
            String dataFine = loanDetails[2].split("Da restituire entro il: ")[1];
            String isbn = getIsbnFromSelection(titolo, autore);
            if(isbn != null && !scadenzaImminente(dataFine) && copiaDisponibile(isbn) && rinnoviEsauriti(isbn) != 2){
                LoanDAO.prolungaPrestito(isbn);
                return true;
            }
        }
        return false;
    }

    private static boolean scadenzaImminente(String dataFine) {
        if(dataFine != null){
            LocalDate dataOdiena = LocalDate.now();
            LocalDate dataControllo = LocalDate.parse(dataFine);
            dataControllo = dataControllo.minusDays(2);
            if(dataOdiena.isAfter(dataControllo)){
                return true;
            }
        }
        return false;
    }

    private static boolean copiaDisponibile(String isbn) {
        return BookDAO.copiaDisponibile(isbn);
    }

    private static int rinnoviEsauriti(String isbn) {
        return LoanDAO.rinnoviEsauriti(isbn);
    }
    
}
