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
    private void aggiungiLibro() throws IOException {
        Libro l = getBookFromTextFields();
        if(BookService.addBook(l)){
            svuotaCampi();

            Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Libro Aggiunto");
                alert.setHeaderText("Libro " + l.getTitolo() + " aggiunto con successo");
                alert.showAndWait();
                backToOpUser();
        }else{
            Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Il libro non è stato aggiunto...");
                alert.setContentText("Controlla che l'ISBN non sia già presente nel catalogo.");
                alert.showAndWait();
        }
    }

    @FXML
    private void backToOpUser() throws IOException {
        App.setRoot("op_user");
    }

    @FXML
    private void addBookHandler() throws IOException{
        if(isAnyTextFieldEmpty()){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Campi vuoti");
            alert.setContentText("Tutti i campi devono essere compilati.");
            alert.showAndWait();

        }else if(BookService.isNumeric(annoField.getText()) == false || BookService.isNumeric(copieField.getText()) == false){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Anno e copie non validi");
            alert.setContentText("Anno e copie devono essere numeri interi.\n\nNumero copie non valido");
            alert.showAndWait();

        }else if(isbnField.getText().length() != 13){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("ISBN non valido");
            alert.setContentText("L'ISBN deve essere composto da 13 cifre.");
            alert.showAndWait();

        }else{
            Libro book = getBookFromTextFields();

                if (BookService.isAnyPropertyTooLong(book)) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Errore");
                    alert.setHeaderText("Almeno un campo ha troppi caratteri");
                    alert.showAndWait();
                
                }else {
                    aggiungiLibro();
                }
        }
    }

    private boolean isAnyTextFieldEmpty(){
        return (isbnField.getText().isEmpty() || 
        titoloField.getText().isEmpty() || 
        autoreField.getText().isEmpty() || 
        editoreField.getText().isEmpty() || 
        annoField.getText().isEmpty() || 
        genereField.getText().isEmpty() || 
        copieField.getText().isEmpty());
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

    private Libro getBookFromTextFields(){
        String isbn = isbnField.getText();
        String titolo = titoloField.getText();
        String autore = autoreField.getText();
        String editore = editoreField.getText();
        String genere = genereField.getText();
        int anno = Integer.parseInt(annoField.getText());
        int copie = Integer.parseInt(copieField.getText());

        return new Libro(isbn, titolo, autore, editore, anno, genere, copie);
    }
}