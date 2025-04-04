package com.progettoswe.controller;

import com.progettoswe.App;
import com.progettoswe.business_logic.InfoCommService;
import com.progettoswe.model.Commento;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.sql.SQLException;

public class ViewVolumeCommentsController {
    private static int volumeId;
    private static String volumeTitle;

    @FXML private Label titleLabel;
    @FXML private VBox commentsContainer;

    public static void setVolumeId(int id) {
        volumeId = id;
    }

    public static void setVolumeTitle(String title) {
        volumeTitle = title;
    }

    @FXML
    public void initialize() {
        titleLabel.setText("Recensioni per volume: " + volumeTitle);
        loadComments();
    }

    private void loadComments() {
        try {
            for (Commento commento : InfoCommService.getCommentiVolumeCompleti(volumeId)) {
                commentsContainer.getChildren().add(createCommentCard(commento));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createCommentCard(Commento commento) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-padding: 15; -fx-border-radius: 8; -fx-border-color: #e9ecef; -fx-border-width: 1;");
        card.setEffect(new javafx.scene.effect.DropShadow(5, Color.gray(0.4, 0.3)));

        // Intestazione commento (utente + data prestito)
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label userLabel = new Label(commento.getUtente().getNome());
        userLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        userLabel.setTextFill(Color.web("#495057"));

        Label dateLabel = new Label("Letto il: " + commento.getPrestito().getDataInizio());
        dateLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        dateLabel.setTextFill(Color.web("#6c757d"));

        header.getChildren().addAll(userLabel, dateLabel);

        // Testo commento
        Label commentText = new Label(commento.getTesto());
        commentText.setWrapText(true);
        commentText.setFont(Font.font("System", 14));
        commentText.setTextFill(Color.web("#343a40"));
        commentText.setPadding(new Insets(5, 0, 0, 0));

        card.getChildren().addAll(header, commentText);
        return card;
    }

    @FXML
    private void handleBack() throws IOException {
        App.setRoot("view_catalogue");
    }
}