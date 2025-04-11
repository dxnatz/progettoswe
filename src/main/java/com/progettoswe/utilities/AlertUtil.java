package com.progettoswe.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;

public class AlertUtil {

        private static final String ALERT_STYLE =
            "/* Stile base del dialog pane - Moderno e minimalista */\n" +
            ".dialog-pane {\n" +
            "    -fx-background-color: #ffffff;\n" +
            "    -fx-background-radius: 12;\n" +
            "    -fx-border-radius: 12;\n" +
            "    -fx-border-color: #e0e0e0;\n" +
            "    -fx-border-width: 1;\n" +
            "    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 10);\n" +
            "    -fx-padding: 20;\n" +
            "}\n" +

            "/* Header più semplice */\n" +
            ".dialog-pane .header-panel {\n" +
            "    -fx-background-color: transparent;\n" +
            "    -fx-padding: 0 0 10 0;\n" +
            "}\n" +

            "/* Etichetta dell'header più elegante */\n" +
            ".dialog-pane .header-panel .label {\n" +
            "    -fx-text-fill: #333333;\n" +
            "    -fx-font-size: 20px;\n" +
            "    -fx-font-weight: 600;\n" +
            "    -fx-font-family: 'Poppins', sans-serif;\n" +
            "}\n" +

            "/* Contenuto con miglior leggibilità */\n" +
            ".dialog-pane .content.label {\n" +
            "    -fx-text-fill: #555555;\n" +
            "    -fx-font-size: 16px;\n" +
            "    -fx-line-spacing: 6px;\n" +
            "    -fx-wrap-text: true;\n" +
            "    -fx-padding: 12 0 0 0;\n" +
            "}\n" +

            "/* Barra dei pulsanti più spaziata */\n" +
            ".dialog-pane .button-bar {\n" +
            "    -fx-padding: 15 0 10 0;\n" +
            "    -fx-spacing: 10px;\n" +
            "}\n" +

            "/* Pulsante con angoli più morbidi e senza bordi */\n" +
            ".dialog-pane .button {\n" +
            "    -fx-background-radius: 8px;\n" +
            "    -fx-border-radius: 8px;\n" +
            "    -fx-pref-height: 42px;\n" +
            "    -fx-min-width: 150px; /* Aumenta la larghezza minima */\n" +
            "    -fx-font-size: 14px;\n" +
            "    -fx-font-weight: 500;\n" +
            "    -fx-cursor: hand;\n" +
            "    -fx-padding: 0 24px;\n" +
            "    -fx-font-family: 'Segoe UI', 'Roboto', sans-serif;\n" +
            "    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 0, 0, 0, 1);\n" +
            "    -fx-transition: all 0.2s;\n" +
            "    -fx-wrap-text: true; /* Testo avvolto */\n" +
            "}\n" +

            "/* Pulsante primario con sfumature più moderne */\n" +
            ".dialog-pane .button:default {\n" +
            "    -fx-background-color: #4361ee;\n" +
            "    -fx-text-fill: white;\n" +
            "}\n" +

            ".dialog-pane .button:default:hover {\n" +
            "    -fx-background-color: #3a56d4;\n" +
            "    -fx-effect: dropshadow(gaussian, rgba(67,97,238,0.3), 0, 0, 0, 3);\n" +
            "}\n" +

            "/* Pulsante di annullamento migliorato */\n" +
            ".dialog-pane .button:cancel {\n" +
            "    -fx-background-color: transparent;\n" +
            "    -fx-text-fill: #6c757d;\n" +
            "    -fx-border-color: #e2e8f0;\n" +
            "    -fx-border-width: 1px;\n" +
            "}\n" +

            ".dialog-pane .button:cancel:hover {\n" +
            "    -fx-background-color: #f8fafc;\n" +
            "    -fx-text-fill: #333333;\n" +
            "}\n" +

            "/* Icone degli alert più moderne */\n" +
            ".alert.information .dialog-pane .graphic-container {\n" +
            "    -fx-background-color: #4361ee;\n" +
            "    -fx-background-radius: 50%;\n" +
            "    -fx-padding: 8px;\n" +
            "}\n" +

            ".alert.warning .dialog-pane .graphic-container {\n" +
            "    -fx-background-color: #f6ad55;\n" +
            "    -fx-background-radius: 50%;\n" +
            "    -fx-padding: 8px;\n" +
            "}\n" +

            ".alert.error .dialog-pane .graphic-container {\n" +
            "    -fx-background-color: #f56565;\n" +
            "    -fx-background-radius: 50%;\n" +
            "    -fx-padding: 8px;\n" +
            "}\n" +

            ".alert.confirmation .dialog-pane .graphic-container {\n" +
            "    -fx-background-color: #48bb78;\n" +
            "    -fx-background-radius: 50%;\n" +
            "    -fx-padding: 8px;\n" +
            "}";

    private static void styleAlert(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(ALERT_STYLE);
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);

        // Aggiunge un po' di spazio intorno al contenuto
        dialogPane.setPrefWidth(450);
    }

    //TODO: agguista grafica che con un solo pulsante custom è brutto
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
        //styleAlert(alert);

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
        styleAlert(alert);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        removeContentIfEmpty(alert);
        //styleAlert(alert);
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