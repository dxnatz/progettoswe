package com.progettoswe.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class AlertUtil {

    public static String showCustomButtonAlert(String title, String header, String content, String... buttonTexts) {
        Alert alert = new Alert(AlertType.NONE);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        List<ButtonType> buttons = new ArrayList<>();
        for (String buttonText : buttonTexts) {
            buttons.add(new ButtonType(buttonText));
        }
        buttons.add(new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE));

        alert.getButtonTypes().setAll(buttons);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && !result.get().getButtonData().isCancelButton()
                ? result.get().getText()
                : null;
    }

    public static void showInfoAlert(String title, String header, String content) {
        showAlert(AlertType.INFORMATION, title, header, content);
    }

    public static void showWarningAlert(String title, String header, String content) {
        showAlert(AlertType.WARNING, title, header, content);
    }

    public static void showErrorAlert(String title, String header, String content) {
        showAlert(AlertType.ERROR, title, header, content);
    }

    public static boolean showConfirmationAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        removeContentIfEmpty(alert);
        alert.showAndWait();
    }

    private static void removeContentIfEmpty(Alert alert) {
        if(alert.getContentText() == null || alert.getContentText().isEmpty()) {
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setContent(null);
            dialogPane.getChildren().removeIf(node -> node instanceof Labeled &&
                    ((Labeled)node).getText() != null &&
                    ((Labeled)node).getText().equals(dialogPane.getContentText()));
        }
    }
}