package com.progettoswe.utilities;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Labeled;

public class AlertUtil {

    /**
     * Mostra un alert con pulsanti personalizzati. Ritorna la stringa del pulsante premuto.
     *
     * @param title   Il titolo dell'alert.
     * @param content Il contenuto dell'alert.
     */
    public static String showCustomButtonAlert(String title, String header, String content, String... buttonTexts) {
        Alert alert = new Alert(AlertType.NONE);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Aggiungi i pulsanti personalizzati
        for (String buttonText : buttonTexts) {
            ButtonType buttonType = new ButtonType(buttonText);
            alert.getButtonTypes().add(buttonType);
        }

        // Mostra l'alert e aspetta la risposta dell'utente
        Optional<ButtonType> result = alert.showAndWait();

        // Restituisci il testo del pulsante premuto, se presente
        return result.map(ButtonType::getText).orElse(null);
    }

    /**
     * Mostra un alert di tipo INFORMATION con un contenuto personalizzato.
     *
     * @param title   Il titolo dell'alert.
     * @param content Il contenuto dell'alert.
     */
    public static void showInfoAlert(String title, String header, String content) {
        showAlert(AlertType.INFORMATION, title, header, content);
    }

    /**
     * Mostra un alert di tipo WARNING con un contenuto personalizzato.
     *
     * @param title   Il titolo dell'alert.
     * @param content Il contenuto dell'alert.
     */
    public static void showWarningAlert(String title, String header, String content) {
        showAlert(AlertType.WARNING, title, header, content);
    }

    /**
     * Mostra un alert di tipo ERROR con un contenuto personalizzato.
     *
     * @param title   Il titolo dell'alert.
     * @param content Il contenuto dell'alert.
     */
    public static void showErrorAlert(String title, String header, String content) {
        showAlert(AlertType.ERROR, title, header, content);
    }

    /**
     * Mostra un alert di tipo CONFIRMATION con un contenuto personalizzato.
     * Restituisce true se l'utente ha confermato, false altrimenti.
     *
     * @param title   Il titolo dell'alert.
     * @param content Il contenuto dell'alert.
     * @return true se l'utente ha premuto OK/Conferma, false altrimenti.
     */
    public static boolean showConfirmationAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Metodo privato generico per mostrare un alert.
     *
     * @param type    Il tipo di alert (INFORMATION, WARNING, ERROR, ecc.).
     * @param title   Il titolo dell'alert.
     * @param header  L'header dell'alert (può essere null).
     * @param content Il contenuto dell'alert.
     */
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
