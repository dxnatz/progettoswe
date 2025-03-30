package com.progettoswe.controller;

import com.progettoswe.App;
import com.progettoswe.business_logic.EdizioneService;
import com.progettoswe.business_logic.InfoCommService;
import com.progettoswe.model.Edizione;
import com.progettoswe.model.Session;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.io.IOException;
import java.util.List;

public class InfoCommController {

    @FXML
    private VBox commentiContainer; // Contenitore dei commenti

    @FXML
    private ScrollPane scrollPane; // ScrollPane per evitare compressione

    @FXML
    private TextArea informazioniOpera;

    @FXML
    private Button indietroButton;

    public void initialize() {

        Edizione edizione = EdizioneService.getEdizione(Session.getNomeOpera(), Session.getEdizione());

        if(edizione != null) {
            informazioniOpera.setText("Titolo: " + edizione.getOpera().getTitolo() + "\n" +
                    "Autore: " + edizione.getOpera().getAutore() + "\n" +
                    "Genere: " + edizione.getOpera().getGenere() + "\n" +
                    "Anno di pubblicazione dell'opera: " + edizione.getOpera().getAnnoPubblicazioneOriginale() + "\n" +
                    "Editore: " + edizione.getEditore() + "\n" +
                    "Anno di pubblicazione dell'edizione: " + edizione.getAnnoPubblicazione() + "\n" +
                    "ISBN edizione: " + edizione.getIsbn() + "\n" +
                    "Descrizione: " + edizione.getOpera().getDescrizione());
        }

        commentiContainer.setSpacing(10); // Aggiunge spazio tra i commenti

        // Recupera i commenti (simulati)
        List<String> commenti = InfoCommService.getCommentiOpera();

        if (commenti.isEmpty()) {
            Label noCommentLabel = new Label("Non ci sono commenti.");
            noCommentLabel.setStyle("-fx-font-size: 14px;" +
                    "-fx-text-alignment: center;" +
                    "-fx-padding: 10;" +
                    "-fx-background-color: #D3D3D3; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-radius: 10;");
            commentiContainer.getChildren().add(noCommentLabel);
        } else {
            for (String commento : commenti) {
                Label commentoLabel = new Label(commento);

                // Permette il wrap del testo
                commentoLabel.setWrapText(true);
                commentoLabel.setMaxWidth(380); // Evita di allargare troppo il testo

                // Calcola l'altezza corretta del commento
                double altezzaDinamica = calcolaAltezzaCommento(commento, 380);
                commentoLabel.setMinHeight(altezzaDinamica);
                commentoLabel.setPrefHeight(altezzaDinamica);

                // Stile della Label
                commentoLabel.setStyle("-fx-background-color: #D3D3D3; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-radius: 10; " +
                        "-fx-padding: 2 5 2 5; " +
                        "-fx-font-size: 14px;" +
                        "-fx-text-alignment: justify;");

                // Margini
                VBox.setMargin(commentoLabel, new Insets(5, 25, 5, 10));
                // Aggiunge il commento al container
                commentiContainer.getChildren().add(commentoLabel);
            }
        }

        // Lo ScrollPane si adatta e permette di scorrere
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(commentiContainer);
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

    // Metodo per calcolare l'altezza necessaria per ogni commento
    private double calcolaAltezzaCommento(String commento, double maxWidth) {
        Text text = new Text(commento);
        text.setWrappingWidth(maxWidth);
        text.setFont(javafx.scene.text.Font.font(14));
        text.setBoundsType(TextBoundsType.LOGICAL);

        double righe = Math.ceil(text.getLayoutBounds().getHeight() / 16); // Stima delle righe
        return Math.max(20, righe * 20);
    }
}