package com.progettoswe.utilities;

import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

public class InputValidator {

    /**
     * Applica un bordo rosso temporaneo al TextField
     * @param textField Il TextField a cui applicare l'effetto
     */
    private static void applyErrorEffect(TextField textField) {
        // Crea un bordo rosso
        BorderStroke borderStroke = new BorderStroke(
                Color.RED,
                BorderStrokeStyle.SOLID,
                new CornerRadii(3),
                new BorderWidths(1)
        );

        // Applica il bordo rosso
        textField.setBorder(new Border(borderStroke));

        // Crea una pausa di 3 secondi
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            // Ripristina il bordo originale
            textField.setBorder(Border.EMPTY);
        });
        pause.play();
    }

    /**
     * Valida se un TextField è vuoto.
     */
    public static boolean validateNotEmpty(TextField textField, String fieldName) {
        if (textField.getText() == null || textField.getText().trim().isEmpty()) {
            AlertUtil.showErrorAlert("Errore di validazione", "Il campo " + fieldName + " non può essere vuoto.", "");
            applyErrorEffect(textField);
            return false;
        }
        return true;
    }

    /**
     * Valida se un TextField contiene un numero intero.
     */
    public static boolean validateInteger(TextField textField, String fieldName) {
        try {
            Integer.parseInt(textField.getText());
            return true;
        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Errore di validazione", "Il campo " + fieldName + " deve contenere un numero intero.", "");
            applyErrorEffect(textField);
            return false;
        }
    }

    /**
     * Valida se un TextField contiene un numero decimale.
     */
    public static boolean validateDecimal(TextField textField, String fieldName) {
        try {
            Double.parseDouble(textField.getText());
            return true;
        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Errore di validazione", "Il campo " + fieldName + " deve contenere un numero decimale.", "");
            applyErrorEffect(textField);
            return false;
        }
    }

    public static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}