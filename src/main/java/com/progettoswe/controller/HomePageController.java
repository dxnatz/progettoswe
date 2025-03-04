package com.progettoswe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class HomePageController {
    @FXML private ListView<String> favoriteBooksList;
    @FXML private ListView<String> catalogList;
    @FXML private TextField searchField;
    @FXML private VBox loansSection;
    
    public void initialize() {
        // Inizializza interfaccia utente
        loadFavoriteBooks();
        loadCatalog();
        loadLoans();
    }
    
    private void loadFavoriteBooks() {
        // TODO: Caricare i libri preferiti dell'utente dal database
    }
    
    private void loadCatalog() {
        // TODO: Caricare l'intero catalogo di libri dal database con filtri
    }
    
    private void loadLoans() {
        // TODO: Caricare i prestiti attivi dell'utente dal database
    }
    
    @FXML
    private void addToFavorites() {
        // TODO: Aggiungere il libro selezionato ai preferiti nel database
    }
    
    @FXML
    private void removeFromFavorites() {
        // TODO: Rimuovere il libro selezionato dai preferiti nel database
    }
    
    @FXML
    private void searchBooks() {
        // TODO: Implementare ricerca nel database per titolo/autore
    }
    
    @FXML
    private void extendLoan() {
        // TODO: Prolungare il prestito nel database
    }
    
    @FXML
    private void cancelLoan() {
        // TODO: Annullare il prestito nel database
    }

    @FXML
    private void logout() {
        // TODO: Implementare il logout e reindirizzare alla schermata di login
    }
}
