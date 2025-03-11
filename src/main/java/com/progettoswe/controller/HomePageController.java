package com.progettoswe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.progettoswe.App;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Prestito;
import com.progettoswe.model.Session;
import com.progettoswe.business_logic.BookService;
import com.progettoswe.business_logic.LoanService;

import java.io.IOException;
import java.util.ArrayList;

public class HomePageController {
    @FXML private ListView<String> listaCatalogo;
    @FXML private TextField ricerca;
    @FXML private ListView<String> listaPrestiti;
    Catalogo catalogo = new Catalogo();
    ArrayList<Prestito> prestiti = new ArrayList<Prestito>();
    
    public void initialize() {
        // Inizializza interfaccia utente
        stampaCatalogo();
        stampaPrestiti();
    }

    
    private void stampaCatalogo() {
        BookService.stampaCatalogo(catalogo, listaCatalogo);
    }
    
    private void stampaPrestiti() {
        LoanService.stampaPrestiti(prestiti, listaPrestiti);
    }
    
    @FXML
    private void searchBooks() {
        BookService.searchBooks(catalogo, listaCatalogo, ricerca);
    }
    
    @FXML
    private void extendLoan() {
        // logica per prolungare il prestito nel database
    }
    
    @FXML
    private void cancelLoan() {
        // logica per annullare il prestito nel database
    }

    @FXML
    private void prenotaLibro(){
        String selectedBook = listaCatalogo.getSelectionModel().getSelectedItem();
        if(LoanService.prenotaLibro(selectedBook)){
            listaCatalogo.getItems().clear();
            listaPrestiti.getItems().clear();
            BookService.stampaCatalogo(catalogo, listaCatalogo);
            stampaPrestiti();
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Libro prenotato con successo");
            a.setHeaderText("Successo");
            a.setTitle("Prenotazione avvenuta con successo");
            a.showAndWait();
        }else{
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Il libro non è stato selezionato correttamente, non è disponibile oppure hai già un prestito attivo per questo libro");
            a.setHeaderText("Errore");
            a.setTitle("Errore durante la prenotazione del libro");
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
