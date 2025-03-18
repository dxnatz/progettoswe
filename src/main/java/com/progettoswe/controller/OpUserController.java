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

    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button returnBookButton;

    public void initialize() {
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
                // Chiama il metodo che vuoi eseguire quando un elemento viene selezionato
                libroSelezionato(newValue);
            });
    }

    private void prestitoSelezionato(String s){
        returnBookButton.setDisable(false);
        lastSelectedLoan = Integer.parseInt(s.split(" ")[0]);
    }

    private void libroSelezionato(String s){
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
        lastSelectedISBN = s.split(" ")[0]; 
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

    @FXML
    private void openDeleteAlert(){
        Alert confermaAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confermaAlert.setTitle("Conferma cancellazione");
            confermaAlert.setHeaderText("Sei sicuro di voler cancellare questo libro?");
            confermaAlert.setContentText("Questa azione non può essere annullata.");

            Optional<ButtonType> risultato = confermaAlert.showAndWait();

            if (risultato.isPresent() && risultato.get() == ButtonType.OK){
                String s = lastSelectedISBN;
                if (deleteBook()){
                    stampaCatalogo();
                    System.out.println("---------------cancellato");
                }else{
                    Alert nonCancellato = new Alert(Alert.AlertType.ERROR);
                    nonCancellato.setTitle("Errore");
                    nonCancellato.setHeaderText("Errore, controlla se ci sono dei prestiti in atto");
                    nonCancellato.setContentText(s + " non è stato cancellato");
                    nonCancellato.showAndWait();
                }
            }
    }

    private boolean deleteBook(){
        return BookService.deleteBook(lastSelectedISBN, prestiti);
    }

    @FXML
    private void returnBook(){
        if(LoanService.returnBook(lastSelectedLoan)){
            stampaPrestiti();
        }else{
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Errore");
            a.setHeaderText("Errore di connessione col database");
            a.setContentText("Prestito " + lastSelectedLoan + " non è stato modificato");
            a.showAndWait();
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
