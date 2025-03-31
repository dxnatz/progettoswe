package com.progettoswe.controller;

import com.progettoswe.App;
import com.progettoswe.business_logic.EdizioneService;
import com.progettoswe.business_logic.InfoCommService;
import com.progettoswe.model.Edizione;
import com.progettoswe.model.Session;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class InfoCommController {

    @FXML
    private VBox commentiContainer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button eliminaCommentoButton;

    @FXML
    private TextArea informazioniOpera;

    @FXML
    private Button aggiungiCommentoButton; // Bottone per aggiungere o modificare il commento

    private Edizione edizione;

    public void initialize() {

        // Recupera l'edizione selezionata
        edizione = EdizioneService.getEdizione(Session.getNomeOpera(), Session.getEdizione());

        if (edizione != null) {
            informazioniOpera.setText("Titolo: " + edizione.getOpera().getTitolo() + "\n" +
                    "Autore: " + edizione.getOpera().getAutore() + "\n" +
                    "Genere: " + edizione.getOpera().getGenere() + "\n" +
                    "Anno di pubblicazione dell'opera: " + edizione.getOpera().getAnnoPubblicazioneOriginale() + "\n" +
                    "Editore: " + edizione.getEditore() + "\n" +
                    "Anno di pubblicazione dell'edizione: " + edizione.getAnnoPubblicazione() + "\n" +
                    "ISBN edizione: " + edizione.getIsbn() + "\n\n" +
                    "Descrizione: " + edizione.getOpera().getDescrizione());
        }

        // Controlla se l'utente ha già commentato l'opera
        loadPulsanti();

        // Carica i commenti
        loadCommenti();

        // ScrollPane si adatta e permette di scorrere
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(commentiContainer);
    }

    private void loadCommenti() {
        List<String> commenti = InfoCommService.getCommentiOpera();

        commentiContainer.getChildren().clear(); // Pulisce i commenti esistenti

        if (commenti.isEmpty()) {
            Label noCommentLabel = new Label("Non ci sono commenti.");
            noCommentLabel.setStyle("-fx-background-color: #e1f5fe; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-radius: 10; " +
                    "-fx-padding: 2 5 2 5; " +
                    "-fx-font-size: 14px;" +
                    "-fx-text-alignment: justify;");
            commentiContainer.getChildren().add(noCommentLabel);
        } else {
            for (String commento : commenti) {
                Label commentoLabel = new Label(commento);
                commentoLabel.setWrapText(true);
                commentoLabel.setMaxWidth(380);
                commentoLabel.setStyle("-fx-background-color: #e1f5fe; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-radius: 10; " +
                        "-fx-padding: 2 5 2 5; " +
                        "-fx-font-size: 14px;" +
                        "-fx-text-alignment: justify;");
                VBox.setMargin(commentoLabel, new Insets(5, 25, 5, 10));
                commentiContainer.getChildren().add(commentoLabel);
            }
        }
    }

    // Metodo per aggiungere o modificare il commento
    @FXML
    private void operazioneAggiungiModifica() {
        // Creazione di un TextInputDialog per inserire il commento
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Commento");
        dialog.setHeaderText(null);
        dialog.setContentText("Inserisci il tuo commento:");

        // Se l'utente ha già commentato, pre-carica il commento esistente
        if (InfoCommService.utenteHaCommentato(edizione.getId_edizione())) {
            String commentoEsistente = InfoCommService.getCommentoUtente(edizione.getId_edizione());
            dialog.setContentText("Modifica il tuo commento:");
            dialog.getEditor().setText(commentoEsistente);
        }

        dialog.getEditor().setPrefWidth(350);
        dialog.getEditor().setPrefHeight(30);

        // Mostra il dialogo e aspetta la risposta
        dialog.showAndWait().ifPresent(commentoText -> {
            // Se l'utente ha scritto qualcosa
            if (!commentoText.isEmpty()) {
                // Se l'utente ha già commentato, modifica il commento
                if (InfoCommService.utenteHaCommentato(edizione.getId_edizione())) {
                    InfoCommService.modificaCommento(commentoText, edizione.getId_edizione());
                } else {
                    // Altrimenti aggiungi il nuovo commento
                    InfoCommService.aggiungiCommento(commentoText, edizione.getId_edizione());
                }

                // Ricarica i commenti per visualizzare l'aggiornamento
                loadCommenti();
                loadPulsanti();
            }
        });
    }

    // Metodo per eliminare il commento
    @FXML
    private void eliminaCommento() {
        // Mostra un Alert per confermare l'eliminazione
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Sei sicuro di voler eliminare il tuo commento?");
        alert.setContentText("Questa azione non può essere annullata.");

        // Mostra l'alert e prendi la risposta
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Se l'utente conferma, elimina il commento dal database
                InfoCommService.eliminaCommento(edizione.getId_edizione());

                // Ricarica i commenti per visualizzare l'aggiornamento
                loadCommenti();
                loadPulsanti();
            }
        });
    }


    // Metodo per ricaricare i pulsanti
    private void loadPulsanti() {
        // Controlla se l'utente ha già commentato l'opera
        boolean haCommentato = InfoCommService.utenteHaCommentato(edizione.getId_edizione());

        // Se ha commentato, mostra il pulsante per modificare
        if (haCommentato) {
            aggiungiCommentoButton.setText("Modifica commento");
            eliminaCommentoButton.setVisible(true);
        } else {
            aggiungiCommentoButton.setText("Aggiungi commento");
            eliminaCommentoButton.setVisible(false);
        }
    }

    @FXML
    private void handleIndietroAction() {
        Session.setNomeOpera(null);
        Session.setEdizione("-1");
        try {
            App.setRoot("homepage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}