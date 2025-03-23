package com.progettoswe.controller;

import java.io.IOException;

import com.progettoswe.App;
import com.progettoswe.business_logic.BookService;
import com.progettoswe.model.Libro;
import com.progettoswe.utilities.AlertUtil;

import javafx.fxml.FXML;
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
        Libro book = BookService.getBook(isbnLibro);
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
                AlertUtil.showErrorAlert("Errore", 
                                        "Campi vuoti", 
                                        "Compilare tutti i campi.");

            }else if(BookService.isNumeric(annoField.getText()) == false || BookService.isNumeric(copieField.getText()) == false){
                AlertUtil.showErrorAlert("Errore", 
                                        "Anno e copie devono essere numeri", 
                                        "Inserire un numero valido.");

            }else{
                Libro book = getBookFromTextFields();

                if (BookService.isAnyPropertyTooLong(book)) {
                    AlertUtil.showErrorAlert("Errore", 
                                        "Proprietà troppo lunghe", 
                                        "Una o più proprietà sono troppo lunghe.");
                
                }else if(!BookService.isCopieValid(book)){
                    AlertUtil.showErrorAlert("Errore", 
                                        "Numero di copie non valido", 
                                        "Inserire un numero di copie valido.");
    
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
            AlertUtil.showInfoAlert("Modifica effettuata", 
                                    "Modifica effettuata",
                                    "Il libro è stato modificato con successo.");
        }else{
            AlertUtil.showErrorAlert("Errore", 
                                    "Errore durante la modifica", 
                                    "Si è verificato un errore durante la modifica del libro.");
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
