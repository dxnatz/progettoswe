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
    }

    private void updateButtonListener(){
        updateButton.setOnAction(event -> {
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
                updateBook();
            }
        });
    }

    //TODO
    private void updateBook(){
        
    }

    @FXML
    private void backToOpUser() throws IOException{
        App.setRoot("op_user");
    }
}
