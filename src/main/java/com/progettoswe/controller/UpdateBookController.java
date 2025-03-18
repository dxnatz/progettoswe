package com.progettoswe.controller;

import java.io.IOException;

import com.progettoswe.App;
import com.progettoswe.ORM.BookDAO;
import com.progettoswe.model.Libro;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class UpdateBookController {
    
    protected static String isbnLibro;
    private Libro book;

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
    private Button updateButton;

    public void initialize() {
        book = BookDAO.getLibro(isbnLibro);
        isbnField.setText(book.getIsbn());
        titoloField.setText(book.getTitolo());
        autoreField.setText(book.getAutore());
        editoreField.setText(book.getAutore());
        annoField.setText(String.valueOf(book.getAnnoPubblicazione()));
        genereField.setText(book.getGenere());
        copieField.setText(String.valueOf(book.getCopie()));

        isbnField.setDisable(true);
        updateButtonListener();
    }

    private void updateButtonListener(){
        updateButton.setOnAction(event -> {
            if (checkEmptyFields()) {

                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Campi vuoti");
                alert.setContentText("Tutti i campi devono essere compilati.");
                alert.showAndWait();
            } else {
                updateBook();
            }
        });
    }

    private boolean checkEmptyFields(){
        return (isbnField.getText().isEmpty() || 
        titoloField.getText().isEmpty() || 
        autoreField.getText().isEmpty() || 
        editoreField.getText().isEmpty() || 
        annoField.getText().isEmpty() || 
        genereField.getText().isEmpty() || 
        copieField.getText().isEmpty());
    }

    //TODO
    private void updateBook(){
        String isbn = isbnField.getText();
        String titolo = titoloField.getText();
        String autore = autoreField.getText();
        String editore = editoreField.getText();
        int anno = Integer.parseInt(annoField.getText());
        String genere = genereField.getText();
        int copie = Integer.parseInt(copieField.getText());

        if(BookDAO.aggiornaLibro(isbn, titolo, autore, editore, anno, genere, copie)){
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Aggiornamento");
            alert.setHeaderText(titolo + " è stato aggiornato");
            alert.setContentText("Modifiche salvate con successo.");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Errore di connessione col database");
            alert.setContentText("Non sono state apportate modifiche.");
            alert.showAndWait();
        }
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

    @FXML
    private void backToOpUser() throws IOException{
        App.setRoot("op_user");
    }
}
