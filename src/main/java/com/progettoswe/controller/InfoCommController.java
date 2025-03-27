package com.progettoswe.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.util.Arrays;
import java.util.List;

public class InfoCommController {

    @FXML
    private VBox commentiContainer; // Contenitore dei commenti

    @FXML
    private ScrollPane scrollPane; // ScrollPane per evitare compressione

    public void initialize() {
        // Rimuove i limiti di altezza per permettere al VBox di crescere liberamente

        commentiContainer.setSpacing(10); // Aggiunge spazio tra i commenti

        // Recupera i commenti (simulati)
        List<String> commenti = getCommenti();

        if (commenti.isEmpty()) {
            Label noCommentLabel = new Label("Non ci sono commenti.");
            noCommentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
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

    // Metodo per calcolare l'altezza necessaria per ogni commento
    private double calcolaAltezzaCommento(String commento, double maxWidth) {
        Text text = new Text(commento);
        text.setWrappingWidth(maxWidth);
        text.setFont(javafx.scene.text.Font.font(14));
        text.setBoundsType(TextBoundsType.LOGICAL);

        double righe = Math.ceil(text.getLayoutBounds().getHeight() / 16); // Stima delle righe
        return Math.max(20, righe * 25); // Minimo 28px, altrimenti altezza dinamica
    }

    // Metodo simulato per ottenere i commenti
    private List<String> getCommenti() {
        return Arrays.asList(
                "Commento 1:\nQuesto è un commento breve.",
                "Commento 2:\nQuesto è un commento un po' più lungo per testare la visualizzazione.",
                "Commento 2:\nQuesto è un commento molto lungo che dovrebbe occupare più righe e adattarsi automaticamente senza essere ristretto. L'idea è che possiamo vedere l'intero testo senza dover cliccare nulla.",
                "Commento 2:\nAltro commento medio per verificare che tutto funzioni bene.",
                "Commento 2:\nAncora un commento lungo per testare l'adattamento. Dovrebbe andare a capo in modo corretto senza essere tagliato. In questo modo, possiamo leggere il commento completo direttamente nel container.",
                "Commento 2:\nQuesto è un altro commento per testare lo scrolling. Se ci sono troppi commenti, possiamo scorrere liberamente senza problemi.",
                "Commento 2:\nUn commento ancora più lungo per testare cosa succede quando il testo è veramente lungo. Dovrebbe occuparsi dello spazio necessario senza comprimere gli altri commenti e senza farli restringere in una sola riga.",
                "Commento 2:\nBreve commento.",
                "Commento 2:\nUn altro commento breve."
        );
    }
}