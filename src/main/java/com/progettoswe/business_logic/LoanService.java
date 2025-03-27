package com.progettoswe.business_logic;

import com.progettoswe.ORM.BookDAO;
import com.progettoswe.ORM.LoanDAO;
import com.progettoswe.model.Prestito;
import com.progettoswe.model.Session;
import com.progettoswe.model.Utente;
import javafx.scene.control.ListView;

import java.time.LocalDate;
import java.util.ArrayList;

public class LoanService {

    public static void searchLoansBibliotecario(ArrayList<Prestito> prestiti, ListView<String> listaPrestiti, String searchText) {
        // Pulisci la ListView prima di mostrare i nuovi risultati
        listaPrestiti.getItems().clear();

        // Se il testo di ricerca è vuoto, mostra tutti i prestiti
        if (searchText == null || searchText.trim().isEmpty()) {
            stampaTuttiPrestiti(prestiti, listaPrestiti);
            return;
        }

        // Normalizza il testo di ricerca (minuscolo e senza spazi extra)
        String searchTerm = searchText.toLowerCase().trim();

        // Filtra i prestiti in base al testo di ricerca
        for (Prestito prestito : prestiti) {
            // Crea la stringa di rappresentazione del prestito (come in stampaTuttiPrestiti)
            String titolo = prestito.getVolume().edizione().getOpera().getTitolo();
            String autore = prestito.getVolume().edizione().getOpera().getAutore();
            String editore = prestito.getVolume().edizione().getEditore();
            int id_prestito = prestito.getId_prestito();
            int numero_edizione = prestito.getVolume().edizione().getNumero();
            LocalDate dataFine;
            String isbn = prestito.getVolume().edizione().getIsbn();

            // Calcola la data di restituzione in base ai rinnovi
            if(prestito.getNum_rinnovi() == 2) {
                dataFine = prestito.getDataInizio().plusDays(60);
            } else if(prestito.getNum_rinnovi() == 1) {
                dataFine = prestito.getDataInizio().plusDays(45);
            } else {
                dataFine = prestito.getDataInizio().plusDays(30);
            }

            // Crea la stringa completa per la ricerca
            String loanString = String.format("%d - %s - %d edizione - %s - %s - Da restituire entro il: %s",
                    id_prestito, titolo, numero_edizione, editore, autore, dataFine.toString());

            // Cerca il termine in tutte le parti della stringa
            if (loanString.toLowerCase().contains(searchTerm)) {
                listaPrestiti.getItems().add(loanString);
            }
        }

        // Se non ci sono risultati, mostra un messaggio
        if (listaPrestiti.getItems().isEmpty()) {
            listaPrestiti.getItems().add("Nessun prestito trovato per: " + searchText);
        }
    }

    //aggiornato
    public static void stampaPrestiti(ArrayList<Prestito> prestiti, ListView<String> listaPrestiti) {
        prestiti = LoanDAO.caricaPrestiti();
        listaPrestiti.getItems().clear();
        for (int i = 0; i < prestiti.size(); i++) {
            String titolo = prestiti.get(i).getVolume().edizione().getOpera().getTitolo();
            String autore = prestiti.get(i).getVolume().edizione().getOpera().getAutore();
            String editore = prestiti.get(i).getVolume().edizione().getEditore();
            int numero_edizione = prestiti.get(i).getVolume().edizione().getNumero();
            LocalDate dataFine;
            String isbn = prestiti.get(i).getVolume().edizione().getIsbn();
            if(LoanDAO.rinnovi(isbn) == 2){
                dataFine = prestiti.get(i).getDataInizio().plusDays(60);
            }else if(LoanDAO.rinnovi(isbn) == 1){
                dataFine = prestiti.get(i).getDataInizio().plusDays(45);
            }else{
                dataFine = prestiti.get(i).getDataInizio().plusDays(30);
            }
            listaPrestiti.getItems().add(titolo + " - " + numero_edizione + " edizione - " + editore + " - " + autore + " - Da restituire entro il: " + dataFine);
        }
    }

    public static void stampaTuttiPrestiti(ArrayList<Prestito> prestiti, ListView<String> listaPrestiti) {
        prestiti = LoanDAO.caricaTuttiPrestiti();
        Utente utente = Session.getUtente();
        listaPrestiti.getItems().clear();
        for (int i = 0; i < prestiti.size(); i++) {
            String titolo = prestiti.get(i).getVolume().edizione().getOpera().getTitolo();
            String autore = prestiti.get(i).getVolume().edizione().getOpera().getAutore();
            String editore = prestiti.get(i).getVolume().edizione().getEditore();
            int id_prestito = prestiti.get(i).getId_prestito();
            int numero_edizione = prestiti.get(i).getVolume().edizione().getNumero();
            LocalDate dataFine;
            String isbn = prestiti.get(i).getVolume().edizione().getIsbn();

            Session.setUtente(prestiti.get(i).getUtente());
            if(LoanDAO.rinnovi(isbn) == 2){
                dataFine = prestiti.get(i).getDataInizio().plusDays(60);
            }else if(LoanDAO.rinnovi(isbn) == 1){
                dataFine = prestiti.get(i).getDataInizio().plusDays(45);
            }else{
                dataFine = prestiti.get(i).getDataInizio().plusDays(30);
            }
            listaPrestiti.getItems().add(id_prestito + " - " + titolo + " - " + numero_edizione + " edizione - " + editore + " - " + autore + " - Da restituire entro il: " + dataFine);
        }
        Session.setUtente(utente);
    }

    //aggiornato
    public static boolean prenotaLibro(String selectedBook) {
        if(selectedBook != null){
            String [] bookDetails = selectedBook.split(" - ");
            String titolo = bookDetails[0];
            String autore = bookDetails[3];
            int num_edizione = Integer.parseInt(bookDetails[1].split(" edizione")[0]);
            String isbn = getIsbnFromSelection(titolo, autore, num_edizione);
            if(isbn != null && libroDisponibile(isbn) && !libroGiaPrenotato(isbn) && !prestitiMassimiRaggiunti()){
                if(LoanDAO.prenotaLibro(isbn)){
                    //aggiornaStatoVolumePrestito(isbn);
                    return true;
                }
            }
        }
        return false;
    }

    //corretto
    private static boolean prestitiMassimiRaggiunti(){
        return LoanDAO.prestitiMassimiRaggiunti();
    }

    //corretto
    private static String getIsbnFromSelection(String titolo, String autore, int num_edizione) {
        String isbn = BookDAO.ottieniIsbn(titolo, autore, num_edizione);
        return isbn;
    }

    //corretto
    private static boolean libroDisponibile(String isbn) {
        return BookDAO.libroDisponibile(isbn);
    }

    //aggiornato
    private static boolean libroGiaPrenotato(String isbn) {
        return LoanDAO.libroGiaPrenotato(isbn);
    }

    //corretto
    public static boolean annullaPrestito(String selectedLoan) {
        if(selectedLoan != null){
            String [] loanDetails = selectedLoan.split(" - ");
            String titolo = loanDetails[0];
            String autore = loanDetails[3];
            int num_edizione = Integer.parseInt(loanDetails[1].split(" edizione")[0]);
            String isbn = getIsbnFromSelection(titolo, autore, num_edizione);
            if(isbn != null && LoanDAO.prestitoDaMenoDiTreGiorni(isbn, num_edizione)){
                //aggiornaStatoVolumeRientro(isbn);
                return LoanDAO.annullaPrestito(isbn);
            }
        }
        return false;
    }

    //corretto
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

    //corretto
    public static boolean prolungaPrestito(String selectedLoan) {
        if(selectedLoan != null){
            String [] loanDetails = selectedLoan.split(" - ");
            String titolo = loanDetails[0];
            String autore = loanDetails[3];
            String dataFine = loanDetails[4].split("Da restituire entro il: ")[1];
            int num_edizione = Integer.parseInt(loanDetails[1].split(" edizione")[0]);
            String isbn = getIsbnFromSelection(titolo, autore, num_edizione);
            if(isbn != null && !scadenzaImminente(dataFine) && copiaDisponibile(isbn) && rinnoviEsauriti(isbn) != 2){
                LoanDAO.prolungaPrestito(isbn);
                return true;
            }
        }
        return false;
    }

    //corretto
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

    //aggiornato
    private static boolean copiaDisponibile(String isbn) {
        return BookDAO.copiaDisponibile(isbn);
    }

    //corretto
    private static int rinnoviEsauriti(String isbn) {
        return LoanDAO.rinnovi(isbn);
    }


}
