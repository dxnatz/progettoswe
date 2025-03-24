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

public class UpdateBookController {

    protected static String isbnLibro;

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
        Libro book = BookService.getVolume(isbnLibro);
        setTextFields(book);
        isbnField.setDisable(true);
        updateButtonListener();
    }

    private void setTextFields(Libro book){
        isbnField.setText(book.getIsbn());
        titoloField.setText(book.getTitolo());
        autoreField.setText(book.getAutore());
        editoreField.setText(book.getEditore());
        annoField.setText(String.valueOf(book.getAnnoPubblicazione()));
        genereField.setText(book.getGenere());
        copieField.setText(String.valueOf(book.getCopie()));
    }

    private void updateButtonListener(){
        updateButton.setOnAction(event -> {

            //controlli
            if (isAnyTextFieldEmpty()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Campi vuoti");
                alert.setContentText("Tutti i campi devono essere compilati.");
                alert.showAndWait();

            }else if(BookService.isNumeric(annoField.getText()) == false || BookService.isNumeric(copieField.getText()) == false){
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Anno e copie non validi");
                alert.setContentText("Anno e copie devono essere numeri interi.");
                alert.showAndWait();

            }else{
                Libro book = getBookFromTextFields();

                if (BookService.isAnyPropertyTooLong(book)) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Errore");
                    alert.setHeaderText("Almeno un campo ha troppi caratteri");
                    alert.showAndWait();
                
                }else if(!BookService.isCopieValid(book)){
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Errore");
                    alert.setHeaderText("Numero copie non valido");
                    alert.showAndWait();
    
                } else {
                    updateBook();
                }
            }
        });
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

    private void updateBook(){
        Libro book = getBookFromTextFields();

        if(BookService.updateBook(book)){
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Aggiornamento");
            alert.setHeaderText(book.getTitolo() + " è stato aggiornato");
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
