package com.progettoswe.business_logic;

import com.progettoswe.ORM.BookDAO;
import com.progettoswe.ORM.LoanDAO;
import com.progettoswe.model.Prestito;
import com.progettoswe.model.Session;
import com.progettoswe.model.Utente;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.time.LocalDate;
import java.util.ArrayList;

public class LoanService {

    public static void searchLoansBibliotecario(ArrayList<Prestito> prestiti, ListView<String> listaPrestiti, String searchText) {
        listaPrestiti.getItems().clear();

        if (searchText == null || searchText.trim().isEmpty()) {
            stampaTuttiPrestiti(prestiti, listaPrestiti);
            return;
        }

        String searchTerm = searchText.toLowerCase().trim();

        for (Prestito prestito : prestiti) {
            String loanString = formatLoanString(prestito);

            if (loanString.toLowerCase().contains(searchTerm)) {
                listaPrestiti.getItems().add(loanString);
            }
        }

        if (listaPrestiti.getItems().isEmpty()) {
            listaPrestiti.getItems().add("Nessun prestito trovato per: " + searchText);
        }
    }

    private static String formatLoanString(Prestito prestito) {
        return String.format(
                "ID Prestito: %d | ID Volume: %d | %s (Ed.%d) - %s | Utente ID: %d | Stato: %s | Scadenza: %s",
                prestito.getId_prestito(),
                prestito.getVolume().getId_volume(),
                prestito.getVolume().getEdizione().getOpera().getTitolo(),
                prestito.getVolume().getEdizione().getNumero(),
                prestito.getVolume().getEdizione().getEditore(),
                prestito.getUtente().getId_utente(),
                prestito.getRestituito() ? "restituito" : "da restituire",
                calculateDueDate(prestito)
        );
    }

    private static LocalDate calculateDueDate(Prestito prestito) {
        switch (prestito.getNum_rinnovi()) {
            case 2: return prestito.getDataInizio().plusDays(60);
            case 1: return prestito.getDataInizio().plusDays(45);
            default: return prestito.getDataInizio().plusDays(30);
        }
    }

    //aggiornato
    public static void stampaPrestiti(ArrayList<Prestito> prestiti, ListView<String> listaPrestiti) {
        prestiti = LoanDAO.caricaPrestiti();
        listaPrestiti.getItems().clear();
        for (int i = 0; i < prestiti.size(); i++) {
            String titolo = prestiti.get(i).getVolume().getEdizione().getOpera().getTitolo();
            String autore = prestiti.get(i).getVolume().getEdizione().getOpera().getAutore();
            String editore = prestiti.get(i).getVolume().getEdizione().getEditore();
            int numero_edizione = prestiti.get(i).getVolume().getEdizione().getNumero();
            LocalDate dataFine;
            String isbn = prestiti.get(i).getVolume().getEdizione().getIsbn();
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
        ArrayList<Prestito> loadedPrestiti = LoanDAO.caricaTuttiPrestiti();
        prestiti.clear();
        prestiti.addAll(loadedPrestiti);
        Utente utente = Session.getUtente();

        // Configura la cell factory per colorare i prestiti da restituire in rosso
        listaPrestiti.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("da restituire")) {
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });

        listaPrestiti.getItems().clear();
        for (Prestito prestito : prestiti) {
            String titolo = prestito.getVolume().getEdizione().getOpera().getTitolo();
            String autore = prestito.getVolume().getEdizione().getOpera().getAutore();
            String editore = prestito.getVolume().getEdizione().getEditore();
            int id_prestito = prestito.getId_prestito();
            int numero_edizione = prestito.getVolume().getEdizione().getNumero();
            LocalDate dataFine;
            String isbn = prestito.getVolume().getEdizione().getIsbn();
            boolean restituito = prestito.getRestituito();
            String stato = restituito ? "restituito" : "da restituire";

            // Calcola la data di scadenza in base ai rinnovi
            if (LoanDAO.rinnovi(isbn) == 2) {
                dataFine = prestito.getDataInizio().plusDays(60);
            } else if (LoanDAO.rinnovi(isbn) == 1) {
                dataFine = prestito.getDataInizio().plusDays(45);
            } else {
                dataFine = prestito.getDataInizio().plusDays(30);
            }

            // Formatta la stringa del prestito
            String prestitoString = String.format(
                    "%d - %s - %d edizione - %s - %s - Data fine prestito: %s - Stato: %s",
                    id_prestito, titolo, numero_edizione, editore, autore, dataFine, stato
            );

            listaPrestiti.getItems().add(prestitoString);
        }
        Session.setUtente(utente);
    }

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

    public static boolean concludiPrestito(int id){
        return LoanDAO.concludiPrestito(id);
    }

}
