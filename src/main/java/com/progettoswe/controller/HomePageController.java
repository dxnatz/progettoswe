package com.progettoswe.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import com.progettoswe.App;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Prestito;
import com.progettoswe.model.Session;
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
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(20), event -> {
            listaCatalogo.getItems().clear();
            listaPrestiti.getItems().clear();
            stampaCatalogo();
            stampaPrestiti();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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
                        successAlert.setContentText("Prestito prolungato con successo");
                        successAlert.setHeaderText("Successo");
                        successAlert.setTitle("Prolungamento prestito avvenuto con successo");
                        successAlert.showAndWait();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setContentText("Il prestito non è stato selezionato correttamente oppure non è possibile prolungare il prestito perchè è già stato prolungato due volte, non c'è un'altra copia disponibile oppure perchè è scaduto");
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
                        successAlert.setContentText("Prestito annullato con successo");
                        successAlert.setHeaderText("Successo");
                        successAlert.setTitle("Annullamento prestito avvenuto con successo");
                        successAlert.showAndWait();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setContentText("Il prestito non è stato selezionato correttamente oppure non è possibile annullare il prestito perchè sono passati più di 3 giorni dalla prenotazione");
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
                        successAlert.setContentText("Libro prenotato con successo");
                        successAlert.setHeaderText("Successo");
                        successAlert.setTitle("Prenotazione avvenuta con successo");
                        successAlert.showAndWait();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setContentText("Il libro non è stato selezionato correttamente, non è disponibile, hai già un prestito attivo per questo libro, perchè è appena stato prenotato da un altro utente, oppure hai raggiunto il numero massimo di prestiti");
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

    @FXML
    private void logout() {
        try {
            Session.setUserEmail(null);
            Session.setUtente(null);
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
