package com.progettoswe.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;

import com.progettoswe.App;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Prestito;
import com.progettoswe.business_logic.BookService;
import com.progettoswe.business_logic.LoanService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import com.progettoswe.utilities.AlertUtil;

public class HomePageController {
    @FXML private ListView<String> listaCatalogo;
    @FXML private TextField ricerca;
    @FXML private ListView<String> listaPrestiti;
    @FXML private Button btnPrenota;
    @FXML private Button btnCancellaPrestito;
    @FXML private Button btnProlungaPrestito; // Aggiungi il pulsante Prolunga Prestito
    Catalogo catalogo = new Catalogo();
    ArrayList<Prestito> prestiti = new ArrayList<Prestito>();
    
    public void initialize() {
        stampaCatalogo();
        stampaPrestiti();

        // Listener alla ListView per controllare la disponibilità del libro selezionato
        listaCatalogo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String[] bookDetails = newValue.split(" - ");
                String disponibilita = bookDetails[2];
                btnPrenota.setDisable(disponibilita.equals("Non disponibile"));
            }
        });

        // Listener alla ListView per controllare se è possibile annullare il prestito selezionato
        listaPrestiti.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String[] loanDetails = newValue.split(" - ");
                String dataFine = loanDetails[2].split("Da restituire entro il: ")[1];
                btnCancellaPrestito.setDisable(LoanService.prenotazioneScaduta(dataFine));
                btnProlungaPrestito.setDisable(LocalDate.parse(dataFine).minusDays(2).isBefore(LocalDate.now()));
            }
        });

        // Timeline per aggiornare la pagina ogni 20 secondi
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            listaCatalogo.getItems().clear();
            listaPrestiti.getItems().clear();
            stampaCatalogo();
            stampaPrestiti();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Imposta la cell factory per la listaCatalogo per personalizzare le celle
        listaCatalogo.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                return new BookListCell();
            }
        });
    }

    private void stampaCatalogo() {
        BookService.stampaCatalogo(catalogo, listaCatalogo);
    }
    
    private void stampaPrestiti() {
        LoanService.stampaPrestiti(prestiti, listaPrestiti);
    }
    
    @FXML
    private void searchBooks() {
        BookService.searchBooks(catalogo, listaCatalogo, ricerca);
    }
    
    //TODO: Verifica se i button funzionano ancora
    @FXML
    private void extendLoan() {
        // logica per prolungare il prestito nel database se il prestito non scade entro 2 giorni dalla data attuale, se c'è un'altra copia disponibile e se non è già stato prolungato una volta
        
        String selectedLoan = listaPrestiti.getSelectionModel().getSelectedItem();
        if (selectedLoan != null) {

            String si = "Sì";
            String no = "No";
            String result = AlertUtil.showCustomButtonAlert("Conferma Prolungamento Prestito", 
                                            "Conferma Prolungamento Prestito", 
                                            "Sei sicuro di voler prolungare il prestito del libro: " + selectedLoan + "?", 
                                            no, si);

                if (result != null && result.equals(si)){
                    if (LoanService.prolungaPrestito(selectedLoan)) {
                        listaCatalogo.getItems().clear();
                        listaPrestiti.getItems().clear();
                        BookService.stampaCatalogo(catalogo, listaCatalogo);
                        stampaPrestiti();
                        AlertUtil.showInfoAlert("Successo", 
                                                "Prestito prolungato con successo.", 
                                                "Il libro è da restituire entro 15 giorni dalla data attuale.");
                    } else {
                        AlertUtil.showErrorAlert("Errore", 
                                                "Il prestito non è stato selezionato correttamente.\nNon è possibile prolungare il prestito perchè sono passati più di 2 giorni dalla data attuale.\nNon è possibile prolungare il prestito perchè non c'è un'altra copia disponibile.\n\nNon è possibile prolungare il prestito perchè è già stato prolungato una volta.", 
                                                "Errore durante il prolungamento del prestito.");
                    }
                }

        } else {
            AlertUtil.showErrorAlert("Errore", 
                                    "Seleziona un prestito da prolungare.", 
                                    "Errore durante il prolungamento del prestito.");
        }
    }
    
    //TODO: Verifica se i button funzionano ancora
    @FXML
    private void cancellaPrestito() {
        // logica per annullare il prestito nel database se non sono passati più di 3 giorni dalla prenotazione, altrimenti restituire un errore
        String selectedLoan = listaPrestiti.getSelectionModel().getSelectedItem();
        if (selectedLoan != null) {

            String si = "Sì";
            String no = "No";
            String result = AlertUtil.showCustomButtonAlert("Conferma Annullamento Prestito", 
                                            "Conferma Annullamento Prestito", 
                                            "Sei sicuro di voler annullare il prestito del libro: " + selectedLoan + "?", 
                                            no, si);

            if (result != null && result.equals(si)){
                if (LoanService.annullaPrestito(selectedLoan)) {
                    listaCatalogo.getItems().clear();
                    listaPrestiti.getItems().clear();
                    BookService.stampaCatalogo(catalogo, listaCatalogo);
                    stampaPrestiti();
                    AlertUtil.showInfoAlert("Successo", 
                                            "Prestito annullato con successo.", 
                                            "Il libro è stato restituito con successo.");
                } else {
                    AlertUtil.showErrorAlert("Errore", 
                                            "Il prestito non è stato selezionato correttamente.\nNon è possibile annullare il prestito perchè sono passati più di 3 giorni dalla data attuale.", 
                                            "Errore durante l'annullamento del prestito.");
                }
            }

        } else {
            AlertUtil.showErrorAlert("Errore", 
                                    "Seleziona un prestito da annullare.", 
                                    "Errore durante l'annullamento del prestito.");
        }
    }

    //TODO: Verifica se i button funzionano ancora
    @FXML
    private void prenotaLibro(){
        String selectedBook = listaCatalogo.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {

            String si = "Sì";
            String no = "No";
            String result = AlertUtil.showCustomButtonAlert("Conferma Prenotazione", 
                                            "Conferma Prenotazione", 
                                            "Sei sicuro di voler prenotare il libro: " + selectedBook + "?", 
                                            no, si);

            if (result != null && result.equals(si)){
                if (LoanService.prenotaLibro(selectedBook)) {
                    listaCatalogo.getItems().clear();
                    listaPrestiti.getItems().clear();
                    BookService.stampaCatalogo(catalogo, listaCatalogo);
                    stampaPrestiti();
                    AlertUtil.showInfoAlert("Successo", 
                                            "Libro prenotato con successo.", 
                                            "Il libro è stato prenotato con successo.");
                } else {
                    AlertUtil.showErrorAlert("Errore", 
                                            "Il libro non è stato selezionato correttamente.\nNon è possibile prenotare il libro perchè non c'è una copia disponibile.", 
                                            "Errore durante la prenotazione del libro.");
                }
            }
        } else {
            AlertUtil.showErrorAlert("Errore", 
                                    "Seleziona un libro da prenotare.", 
                                    "Errore durante la prenotazione del libro.");
        }
    }

    @FXML
    private void visualizzaProfilo() {
        try {
            App.setRoot("profile");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Classe interna per personalizzare le celle della listaCatalogo
    private static class BookListCell extends ListCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
    
            if (item != null && !empty) {
                String[] bookDetails = item.split(" - ");
                String disponibilita = bookDetails[2];
    
                if (disponibilita.equals("Non disponibile")) {
                    Label disponibilitaLabel = new Label(disponibilita);
                    disponibilitaLabel.setTextFill(Color.RED);
                    HBox hbox = new HBox(new Label(bookDetails[0] + " - "), new Label(bookDetails[1] + " - "), disponibilitaLabel);
                    setGraphic(hbox);
                    setText(null); // Evita conflitti tra testo e grafica
                } else {
                    setText(bookDetails[0] + " - " + bookDetails[1] + " - " + disponibilita);
                    setGraphic(null);
                }
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }
}
