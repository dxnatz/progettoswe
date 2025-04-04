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

public class ViewCommentsController {
    private static int operaId;
    private static String operaTitle;

    @FXML private Label titleLabel;
    @FXML private VBox commentsContainer;

    public static void setOperaId(int id) {
        operaId = id;
    }

    public static void setOperaTitle(String title) {
        operaTitle = title;
    }

    @FXML
    public void initialize() {
        titleLabel.setText("Recensioni: " + operaTitle);
        loadComments();
    }

    private void loadComments() {
        try {
            for (Commento commento : InfoCommService.getCommentiOperaCompleti(operaId)) {
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

        // Intestazione commento (utente + edizione)
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label userLabel = new Label(commento.getUtente().getNome() + " " + commento.getUtente().getCognome());
        userLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        userLabel.setTextFill(Color.web("#495057"));

        Label editionLabel = new Label("Ed. " + commento.getEdizione().getNumero());
        editionLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        editionLabel.setTextFill(Color.web("#6c757d"));

        Label readLabel = new Label(commento.getPrestito() != null ? "✓ Letto" : "✗ Letto da altre fonti");
        readLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        readLabel.setTextFill(commento.getPrestito() != null ? Color.web("#2ecc71") : Color.web("#e74c3c"));

        header.getChildren().addAll(userLabel, editionLabel, readLabel);

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