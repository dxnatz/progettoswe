package com.progettoswe.controller;

import java.io.IOException;

import com.progettoswe.App;
import com.progettoswe.business_logic.BookService;
import com.progettoswe.model.Libro;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AddBookController {

    @FXML
    private TextField isbnField;

    @FXML
    private TextField titoloField;

    @FXML
    private TextField autoreField;

    @FXML
    private TextField editoreField;

    @FXML
    private TextField annoField;

    @FXML
    private TextField genereField;

    @FXML
    private TextField copieField;

    @FXML
    private Button aggiungiButton;
    
    @FXML
    private void aggiungiLibro() {
        Libro l = new Libro(isbnField.getText(),
                            titoloField.getText(),
                            autoreField.getText(),
                            editoreField.getText(),
                            Integer.parseInt(annoField.getText()),
                            genereField.getText(),
                            Integer.parseInt(copieField.getText()));
        if(BookService.addBook(l)){
            svuotaCampi();

            Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Libro Aggiunto");
                alert.setHeaderText("Libro " + l.getTitolo() + " aggiunto con successo");
                alert.showAndWait();
        }else{
            Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Il libro non è stato aggiunto...");
                alert.setContentText("Errore di connessione col database");
                alert.showAndWait();
        }
    }

    @FXML
    private void backToOpUser() throws IOException {
        App.setRoot("op_user");
    }

    public void initialize() {

        aggiungiButton.setOnAction(event -> {
            if (isbnField.getText().isEmpty() || 
                titoloField.getText().isEmpty() || 
                autoreField.getText().isEmpty() || 
                editoreField.getText().isEmpty() || 
                annoField.getText().isEmpty() || 
                genereField.getText().isEmpty() || 
                copieField.getText().isEmpty()) {

                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Campi vuoti");
                alert.setContentText("Tutti i campi devono essere compilati.");
                alert.showAndWait();
            } else {
                aggiungiLibro();
            }
        });
    }

    @FXML
    private void svuotaCampi() {
        isbnField.clear();
        titoloField.clear();
        autoreField.clear();
        editoreField.clear();
        annoField.clear();
        genereField.clear();
        copieField.clear();
    }
}