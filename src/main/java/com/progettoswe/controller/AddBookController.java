package com.progettoswe.controller;

import java.io.IOException;

import com.progettoswe.App;
import com.progettoswe.business_logic.BookService;
import com.progettoswe.model.Libro;
import com.progettoswe.utilities.AlertUtil;

import javafx.fxml.FXML;
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

            AlertUtil.showConfirmationAlert("Libro Aggiunto", 
                                            "Libro " + l.getTitolo() + " aggiunto con successo", 
                                            "Il libro è stato aggiunto con successo.");

        }else{
            AlertUtil.showErrorAlert("Errore", 
                                    "Il libro non è stato aggiunto...", 
                                    "Controlla che l'ISBN non sia già presente nel catalogo.");
        }
    }

    @FXML
    private void backToOpUser() throws IOException {
        App.setRoot("op_user");
    }

    @FXML
    private void addBookHandler() throws IOException{
        if(isAnyTextFieldEmpty()){
            AlertUtil.showErrorAlert("Errore", 
                                    "Campi vuoti", 
                                    "Tutti i campi devono essere compilati.");

        }else if(BookService.isNumeric(annoField.getText()) == false || BookService.isNumeric(copieField.getText()) == false){
            AlertUtil.showErrorAlert("Errore", 
                                    "Anno e copie devono essere numeri", 
                                    "Anno e copie devono essere numeri interi.");

        }else if(isbnField.getText().length() != 13){
            AlertUtil.showErrorAlert("Errore", 
                                    "ISBN non valido", 
                                    "L'ISBN deve essere composto da 13 cifre.");

        }else{
            Libro book = getBookFromTextFields();

                if (BookService.isAnyPropertyTooLong(book)) {
                    AlertUtil.showErrorAlert("Errore", 
                                            "Proprietà troppo lunghe", 
                                            "Controlla che le proprietà non siano troppo lunghe.");
                
                }else {
                    aggiungiLibro();
                }
        }
    }

    //TODO: sposta in InputValidator
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