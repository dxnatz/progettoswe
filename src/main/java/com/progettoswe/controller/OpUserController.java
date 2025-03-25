package com.progettoswe.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import com.progettoswe.App;
import com.progettoswe.business_logic.BookService;
import com.progettoswe.business_logic.LoanService;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Prestito;
import com.progettoswe.model.Session;
import com.progettoswe.utilities.AlertUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class OpUserController {
    @FXML private ListView<String> listaCatalogo;
    @FXML private TextField ricerca;
    @FXML private TextField ricercaPrestito;
    @FXML private ListView<String> listaPrestiti;
    Catalogo catalogo;
    ArrayList<Prestito> prestiti;
    private String lastSelectedISBN;
    private int lastSelectedLoan;
    private String lastSelectedLoanedBook;

    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button returnBookButton;

    public void initialize() {
        
        prestiti = new ArrayList<Prestito>();
        // Inizializza interfaccia utente
        stampaCatalogo();
        stampaPrestiti();
        listenerCatalogo();
        listenerPrestiti();
    }

    //chiama prestitoSelezionato passando la stringa della riga selezionata dai prestiti
    private void listenerPrestiti(){
        listaPrestiti.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                prestitoSelezionato(newValue);
            });
    }

    //chiama libroSelezionato passando la stringa della riga selezionata dal catalogo
    private void listenerCatalogo(){
        listaCatalogo.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                libroSelezionato(newValue);
            });
    }

    private void prestitoSelezionato(String s){
        if (s != null){
            returnBookButton.setDisable(false);
            lastSelectedLoan = Integer.parseInt(s.split(" - ")[0]);
            lastSelectedLoanedBook = s.split(" - ")[1];
        }
    }

    private void libroSelezionato(String s){
        if (s != null){
            updateButton.setDisable(false);
            deleteButton.setDisable(false);
            lastSelectedISBN = s.split(" ")[0]; 
        }
    }

    private void stampaCatalogo() {
        BookService.stampaCatalogoISBN(catalogo, listaCatalogo);
    }
    
    private void stampaPrestiti() {
        LoanService.stampaTuttiPrestiti(prestiti, listaPrestiti);
    }

    @FXML
    private void searchBooks() {
        BookService.searchBooksISBN(catalogo, listaCatalogo, ricerca);
    }

    @FXML
    private void searchLoans() {
        LoanService.searchLoans(prestiti, listaPrestiti, ricercaPrestito.getText());
    }

    @FXML
    private void openAddBookWindow() throws IOException{
        App.setRoot("add_book");
    }

    @FXML
    private void openUpdateBookWindow() throws IOException{
        UpdateBookController.isbnLibro = lastSelectedISBN;
        App.setRoot("update_book");
    }

    //TODO: Verifica se i button funzionano ancora
    @FXML
    private void openDeleteAlert(){

        String annulla = "Annulla";
        String cancella = "Cancella";
        String result = AlertUtil.showCustomButtonAlert("Conferma cancellazione", 
                                                        "Sei sicuro di voler cancellare \"" + lastSelectedISBN + "\"?", 
                                                        "Questa azione non può essere annullata.", 
                                                        annulla, cancella);

            if (result != null && result.equals(cancella)){
                String s = lastSelectedISBN;
                if (deleteBook()){
                    stampaCatalogo();
                    stampaPrestiti();
                }else{
                    AlertUtil.showErrorAlert("Errore", 
                    "Errore di connessione col database", 
                    "Libro " + s + " non è stato cancellato");
                }
            }
    }

    private boolean deleteBook(){
        boolean r = BookService.deleteBook(lastSelectedISBN, prestiti);
        if(r){
            updateButton.setDisable(true);
            deleteButton.setDisable(true);
        }

        return r;
    }

    private void returnBook(){
        if(LoanService.returnBook(lastSelectedLoan)){
            stampaPrestiti();
            returnBookButton.setDisable(true);
        }else{
            AlertUtil.showErrorAlert("Errore",
                                    "Errore di connessione col database",
                                    "Libro " + lastSelectedLoanedBook + " non è stato restituito");
        }
    }

    //TODO: Verifica se i button funzionano ancora
    @FXML
    private void openReturnBookAlert(){
        String annulla = "Annulla";
        String restituisci = "Restituisci";
        String result = AlertUtil.showCustomButtonAlert("Conferma restituzione", 
                                                        "Sei sicuro di voler restituire \"" + lastSelectedLoanedBook + "\"?", 
                                                        "Questa azione non può essere annullata.", 
                                                        annulla, restituisci);

            if (result != null && result.equals(restituisci)){
                returnBook();
            }
    }

    @FXML
    private void logout() {
        try {
            Session.setUserEmail(null);
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
