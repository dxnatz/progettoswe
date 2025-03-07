package com.progettoswe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import com.progettoswe.App;
import com.progettoswe.dao.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomePageController {
    @FXML private ListView<String> catalogList;
    @FXML private TextField searchField;
    @FXML private ListView<String> loansSection;
    
    public void initialize() {
        // Inizializza interfaccia utente
        loadCatalog();
        loadLoans();
    }

    
    private void loadCatalog() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT titolo, autore, copie FROM libro";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                String titolo = resultSet.getString("titolo");
                String autore = resultSet.getString("autore");
                String disponibile;
                if(resultSet.getInt("copie") == 0){
                    disponibile = "Non disponibile";
                }else{
                    disponibile = "Disponibile";
                }
                catalogList.getItems().add(titolo + " - " + autore + " - " + disponibile);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Mostra un messaggio di errore all'utente
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di caricamento");
            alert.setHeaderText("Impossibile caricare il catalogo");
            alert.setContentText("Si è verificato un errore durante il caricamento del catalogo dei libri.");
            alert.showAndWait();
        }
    }
    
    private void loadLoans() {
        // TODO: Caricare i prestiti attivi dell'utente dal database
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
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
