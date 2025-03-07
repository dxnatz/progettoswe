package com.progettoswe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.progettoswe.App;
import com.progettoswe.ORM.DatabaseConnection;
import com.progettoswe.model.Session;

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
        String query = "SELECT titolo, autore, copie FROM libro";

        try (Connection connection = DatabaseConnection.getConnection()) {
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
        }
    }
    
    private void loadLoans() {
        String query = "SELECT libro.titolo, data_fine FROM prestito JOIN libro ON prestito.isbn_libro = libro.isbn JOIN utente ON utente.codice = prestito.codice_utente WHERE utente.email = ?";

        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, Session.getUserEmail());
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                String titolo = resultSet.getString("titolo");
                String dataFine = resultSet.getString("data_fine");
                loansSection.getItems().add(titolo + " - FINE PRESTITO: " + dataFine);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    @FXML
    private void searchBooks() {
        String searchText = searchField.getText();
        String query = "SELECT titolo, autore, copie FROM libro WHERE LOWER(titolo) LIKE LOWER(?) OR LOWER(autore) LIKE LOWER(?) OR LOWER(editore) LIKE LOWER(?) OR LOWER(genere) LIKE LOWER(?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            String searchPattern = "%" + searchText + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);
            ResultSet resultSet = statement.executeQuery();
            
            catalogList.getItems().clear(); // Pulisce la lista prima di aggiungere i nuovi risultati
            
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
            alert.setTitle("Errore di ricerca");
            alert.setHeaderText("Impossibile eseguire la ricerca");
            alert.setContentText("Si è verificato un errore durante la ricerca dei libri.");
            alert.showAndWait();
        }
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
            Session.setUserEmail(null);
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
