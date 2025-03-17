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
        // Inizializza interfaccia utente
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
    
    @FXML
    private void extendLoan() {
        // logica per prolungare il prestito nel database se il prestito non scade entro 2 giorni dalla data attuale, se c'è un'altra copia disponibile e se non è già stato prolungato una volta
        
        String selectedLoan = listaPrestiti.getSelectionModel().getSelectedItem();
        if (selectedLoan != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Conferma Prolungamento Prestito");
            confirmAlert.setHeaderText("Conferma Prolungamento Prestito");
            confirmAlert.setContentText("Sei sicuro di voler prolungare il prestito del libro: " + selectedLoan + "?\n\n" + "Il prestito verrà prolungato di 15 giorni.");

            ButtonType buttonTypeYes = new ButtonType("Sì");
            ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            confirmAlert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            confirmAlert.showAndWait().ifPresent(type -> {
                if (type == buttonTypeYes) {
                    if (LoanService.prolungaPrestito(selectedLoan)) {
                        listaCatalogo.getItems().clear();
                        listaPrestiti.getItems().clear();
                        BookService.stampaCatalogo(catalogo, listaCatalogo);
                        stampaPrestiti();
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setContentText("Prestito prolungato con successo.\n\nIl libro è da restituire entro 15 giorni dalla data attuale.");
                        successAlert.setHeaderText("Successo");
                        successAlert.setTitle("Prolungamento prestito avvenuto con successo");
                        successAlert.showAndWait();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setContentText("Il prestito non è stato selezionato correttamente.\n\nNon è possibile prolungare il prestito perchè è già stato prolungato due volte.\n\nNon c'è un'altra copia disponibile.\n\n Il prestito è scaduto.");
                        errorAlert.setHeaderText("Errore");
                        errorAlert.setTitle("Errore durante il prolungamento del prestito");
                        errorAlert.showAndWait();
                    }
                }
            });
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText("Seleziona un prestito da prolungare.");
            errorAlert.setHeaderText("Errore");
            errorAlert.setTitle("Errore durante il prolungamento del prestito");
            errorAlert.showAndWait();
        }
    }
    
    @FXML
    private void cancellaPrestito() {
        // logica per annullare il prestito nel database se non sono passati più di 3 giorni dalla prenotazione, altrimenti restituire un errore
        String selectedLoan = listaPrestiti.getSelectionModel().getSelectedItem();
        if (selectedLoan != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Conferma Annullamento Prestito");
            confirmAlert.setHeaderText("Conferma Annullamento Prestito");
            confirmAlert.setContentText("Sei sicuro di voler annullare il prestito del libro: " + selectedLoan + "?");

            ButtonType buttonTypeYes = new ButtonType("Sì");
            ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            confirmAlert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            confirmAlert.showAndWait().ifPresent(type -> {
                if (type == buttonTypeYes) {
                    if (LoanService.annullaPrestito(selectedLoan)) {
                        listaCatalogo.getItems().clear();
                        listaPrestiti.getItems().clear();
                        BookService.stampaCatalogo(catalogo, listaCatalogo);
                        stampaPrestiti();
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setContentText("Prestito annullato con successo.");
                        successAlert.setHeaderText("Successo");
                        successAlert.setTitle("Annullamento prestito avvenuto con successo");
                        successAlert.showAndWait();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setContentText("Il prestito non è stato selezionato correttamente.\n\nNon è possibile annullare il prestito perchè sono passati più di 3 giorni dalla prenotazione.");
                        errorAlert.setHeaderText("Errore");
                        errorAlert.setTitle("Errore durante l'annullamento del prestito");
                        errorAlert.showAndWait();
                    }
                }
            });
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText("Seleziona un prestito da annullare.");
            errorAlert.setHeaderText("Errore");
            errorAlert.setTitle("Errore durante l'annullamento del prestito");
            errorAlert.showAndWait();
        }
    }

    @FXML
    private void prenotaLibro(){
        String selectedBook = listaCatalogo.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Conferma Prenotazione");
            confirmAlert.setHeaderText("Conferma Prenotazione");
            confirmAlert.setContentText("Sei sicuro di voler prenotare il libro: " + selectedBook + "?");

            ButtonType buttonTypeYes = new ButtonType("Sì");
            ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            confirmAlert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            confirmAlert.showAndWait().ifPresent(type -> {
                if (type == buttonTypeYes) {
                    if (LoanService.prenotaLibro(selectedBook)) {
                        listaCatalogo.getItems().clear();
                        listaPrestiti.getItems().clear();
                        BookService.stampaCatalogo(catalogo, listaCatalogo);
                        stampaPrestiti();
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setContentText("Libro prenotato con successo.");
                        successAlert.setHeaderText("Successo");
                        successAlert.setTitle("Prenotazione avvenuta con successo");
                        successAlert.showAndWait();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setContentText("Il libro non è stato selezionato correttamente.\n\nIl libro non è disponibile.\n\nHai già un prestito attivo per questo libro.\n\n Il libro è appena stato prenotato da un altro utente.\n\nHai raggiunto il numero massimo di prestiti.");
                        errorAlert.setHeaderText("Errore");
                        errorAlert.setTitle("Errore durante la prenotazione del libro");
                        errorAlert.showAndWait();
                    }
                }
            });
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText("Seleziona un libro da prenotare.");
            errorAlert.setHeaderText("Errore");
            errorAlert.setTitle("Errore durante la prenotazione del libro");
            errorAlert.showAndWait();
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
