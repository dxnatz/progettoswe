package com.progettoswe.controller;

import com.progettoswe.App;
import com.progettoswe.business_logic.BookService;
import com.progettoswe.business_logic.InfoCommService;
import com.progettoswe.business_logic.LoanService;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Prestito;
import com.progettoswe.model.Session;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class HomePageController {
    @FXML private ListView<String> listaCatalogo;
    @FXML private TextField ricerca;
    @FXML private ListView<String> listaPrestiti;
    @FXML private Button btnPrenota;
    @FXML private Button btnCancellaPrestito;
    @FXML private Button btnProlungaPrestito;
    @FXML private Button btnInfoComm;
    @FXML private ComboBox<String> filtroPrestiti;
    @FXML private Button btnCommentaVolume;
    Catalogo catalogo = new Catalogo();
    private String ricercaAttiva = "";
    //ArrayList<Prestito> prestiti = new ArrayList<>();

    public void initialize() {
        // Inizializza interfaccia utente
        stampaCatalogo();
        //stampaPrestiti();
        filtroPrestiti.getItems().addAll("Prestiti attivi", "Prestiti conclusi", "Tutti i prestiti");

        // Imposta il valore predefinito (Prestiti attivi)
        filtroPrestiti.setValue("Prestiti attivi");

        // Ascolta le modifiche nel ComboBox e aggiorna la lista dei prestiti
        filtroPrestiti.setOnAction(event -> filtraPrestiti());

        // All'inizio, visualizza i prestiti attivi
        filtraPrestiti();

        btnInfoComm.setDisable(true);


        // Listener alla ListView per controllare la disponibilità del libro selezionato
        listaCatalogo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnInfoComm.setDisable(false);
            if (newValue != null) {
                String[] bookDetails = newValue.split(" - ");
                String disponibilita = bookDetails[4];
                btnPrenota.setDisable(disponibilita.equals("Non disponibile"));
            }
        });

        // Listener alla ListView per controllare se è possibile annullare il prestito selezionato
        listaPrestiti.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String[] loanDetails = newValue.split(" - ");
                String dataFine = loanDetails[4].split("Da restituire entro il: ")[1];
                dataFine = dataFine.split(" | ")[0];
                btnCancellaPrestito.setDisable(LoanService.prenotazioneScaduta(dataFine));
                btnProlungaPrestito.setDisable(LocalDate.parse(dataFine).minusDays(2).isBefore(LocalDate.now()));
            }
        });

        // Timeline per aggiornare la pagina ogni 25 secondi
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(25), event -> {
            listaCatalogo.getItems().clear();
            listaPrestiti.getItems().clear();
            stampaCatalogo();
            filtraPrestiti();
            if (!ricercaAttiva.isEmpty()) {
                BookService.searchBooks(catalogo, listaCatalogo, ricerca);
            }
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

    @FXML
    private void searchBooks() {
        ricercaAttiva = ricerca.getText();
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
                        //stampaPrestiti();
                        // Aggiorna la lista dei prestiti
                        filtraPrestiti();
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
                        //stampaPrestiti();
                        filtraPrestiti();
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
                        //modificare lo stato del volume in prestito

                        BookService.stampaCatalogo(catalogo, listaCatalogo);
                        //stampaPrestiti();
                        filtraPrestiti();
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setContentText("Libro prenotato con successo.");
                        successAlert.setHeaderText("Successo");
                        successAlert.setTitle("Prenotazione avvenuta con successo");
                        successAlert.showAndWait();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setContentText("Il libro non è stato selezionato correttamente.\n\n" +
                                "Il libro non è disponibile.\n\n" +
                                "Hai già un prestito attivo per questo libro.\n\n" +
                                "Il libro è appena stato prenotato da un altro utente.\n\n" +
                                "Hai raggiunto il numero massimo di prestiti.");
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
    public void filtraPrestiti() {
        String filtroSelezionato = filtroPrestiti.getValue();
        ArrayList<Prestito> prestitiFiltrati = LoanService.filtraPrestiti(filtroSelezionato);

        if(filtroSelezionato.equals("Prestiti conclusi")){
            btnProlungaPrestito.setVisible(false);
            btnCancellaPrestito.setVisible(false);
        }else{
            btnProlungaPrestito.setVisible(true);
            btnCancellaPrestito.setVisible(true);
        }

        listaPrestiti.getItems().clear(); // Pulisce la lista

        ObservableList<String> prestitiObservableList = FXCollections.observableArrayList();

        if (prestitiFiltrati.isEmpty()) {
            prestitiObservableList.add("Nessun prestito disponibile visibile.");
            listaPrestiti.setDisable(true);
            btnProlungaPrestito.setDisable(true);
            btnCancellaPrestito.setDisable(true);
            btnCommentaVolume.setDisable(true);
        } else {
            for (Prestito prestito : prestitiFiltrati) {
                prestitiObservableList.add(prestito.toString());
                listaPrestiti.setDisable(false);
                btnProlungaPrestito.setDisable(false);
                btnCancellaPrestito.setDisable(false);
                btnCommentaVolume.setDisable(false);
            }
        }

        listaPrestiti.setItems(prestitiObservableList);
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
    private void visualizzaInfoComm() {

        String selectedBook = listaCatalogo.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            String [] bookDetails = selectedBook.split(" - ");
            Session.setNomeOpera(bookDetails[0]);
            Session.setEdizione(bookDetails[1].split(" edizione")[0]);
            try {
                App.setRoot("infocomm");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText("Seleziona un libro per visualizzare le informazioni.");
            errorAlert.setHeaderText("Errore");
            errorAlert.setTitle("Errore durante la visualizzazione delle informazioni");
            errorAlert.showAndWait();
        }

    }

    @FXML
    private void commentaVolume() {
        String selectedLoan = listaPrestiti.getSelectionModel().getSelectedItem();

        if (selectedLoan != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Inserisci Commento");
            dialog.setHeaderText("Aggiungi un commento per il prestito selezionato");
            dialog.setContentText("Scrivi il tuo commento:");

            dialog.showAndWait().ifPresent(commento -> {
                if (!commento.trim().isEmpty()) {
                    // Recupera i dati del prestito selezionato
                    String[] loanDetails = selectedLoan.split(" - ");
                    String prestitoId = loanDetails[5];
                    System.out.println("ID prestito: " + prestitoId);

                    // Chiama il servizio per inserire il commento
                    boolean successo = InfoCommService.aggiungiCommentoVolume(Integer.parseInt(prestitoId), commento);

                    Alert alert = new Alert(successo ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                    alert.setTitle(successo ? "Successo" : "Errore");
                    alert.setHeaderText(successo ? "Commento aggiunto con successo!" : "Errore durante l'inserimento del commento.");
                    alert.setContentText(successo ? "Il tuo commento è stato salvato nel database." : "Si è verificato un problema.");
                    alert.showAndWait();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Errore");
                    errorAlert.setHeaderText("Il commento non può essere vuoto!");
                    errorAlert.showAndWait();
                }
            });

        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Errore");
            errorAlert.setHeaderText("Seleziona un prestito prima di commentare.");
            errorAlert.showAndWait();
        }
    }

    // Classe interna per personalizzare le celle della listaCatalogo
    private static class BookListCell extends ListCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null && !empty) {
                String[] bookDetails = item.split(" - ");
                String disponibilita = bookDetails[4];

                if (disponibilita.equals("Non disponibile")) {
                    Label disponibilitaLabel = new Label(disponibilita);
                    disponibilitaLabel.setTextFill(Color.RED);
                    HBox hbox = new HBox(new Label(bookDetails[0] + " - "), new Label(bookDetails[1] + " - "), new Label(bookDetails[2] + " - "), new Label(bookDetails[3] + " - ") ,disponibilitaLabel);
                    setGraphic(hbox);
                    setText(null); // Evita conflitti tra testo e grafica
                } else {
                    setText(bookDetails[0] + " - " + bookDetails[1] + " - " + bookDetails[2] + " - " + bookDetails[3] + " - " + disponibilita);
                    setGraphic(null);
                }
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }
}