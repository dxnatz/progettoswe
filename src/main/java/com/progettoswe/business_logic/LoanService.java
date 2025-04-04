package com.progettoswe.business_logic;

import com.progettoswe.ORM.BookDAO;
import com.progettoswe.ORM.LoanDAO;
import com.progettoswe.model.Prestito;

import java.time.LocalDate;
import java.util.ArrayList;

public class LoanService {

    //aggiornato
    /*public static void stampaPrestiti(ArrayList<Prestito> prestiti, ListView<String> listaPrestiti) {
        //prestiti = LoanDAO.caricaPrestiti();
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
    }*/

    public static ArrayList<Prestito> filtraPrestiti(String filtro) {
        ArrayList<Prestito> prestitiFiltrati = new ArrayList<>();

        // Supponiamo che tu abbia una lista di tutti i prestiti
        ArrayList<Prestito> tuttiIPrestiti = LoanDAO.caricaPrestiti(); // Recupera tutti i prestiti

        // Filtro in base alla scelta
        for (Prestito prestito : tuttiIPrestiti) {
            switch (filtro) {
                case "Tutti i prestiti":
                    prestitiFiltrati.add(prestito);
                    break;
                case "Prestiti attivi":
                    if (!prestito.getRestituito()) { // Se restituito è false, significa che il prestito è ancora attivo
                        prestitiFiltrati.add(prestito);
                    }
                    break;
                case "Prestiti conclusi":
                    if (prestito.getRestituito()) { // Se restituito è true, significa che il prestito è concluso
                        prestitiFiltrati.add(prestito);
                    }
                    break;
                default:
                    // Aggiungi un comportamento di default se necessario
                    break;
            }
        }
        return prestitiFiltrati;
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
