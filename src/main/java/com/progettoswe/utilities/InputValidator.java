package com.progettoswe.utilities;

import javafx.scene.control.TextField;

public class InputValidator {

    /**
     * Valida se un TextField è vuoto.
     *
     * @param textField Il TextField da validare.
     * @param fieldName Il nome del campo (per il messaggio di errore).
     * @return true se il campo è valido, false altrimenti.
     */
    public static boolean validateNotEmpty(TextField textField, String fieldName) {
        if (textField.getText() == null || textField.getText().trim().isEmpty()) {
            AlertUtil.showErrorAlert("Errore di validazione", "Il campo " + fieldName + " non può essere vuoto.", "");
            return false;
        }
        return true;
    }

    /**
     * Valida se un TextField contiene un numero intero.
     *
     * @param textField Il TextField da validare.
     * @param fieldName Il nome del campo (per il messaggio di errore).
     * @return true se il campo è valido, false altrimenti.
     */
    public static boolean validateInteger(TextField textField, String fieldName) {
        try {
            Integer.parseInt(textField.getText());
            return true;
        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Errore di validazione", "Il campo " + fieldName + " deve contenere un numero intero.", "");
            return false;
        }
    }

    /**
     * Valida se un TextField contiene un numero decimale.
     *
     * @param textField Il TextField da validare.
     * @param fieldName Il nome del campo (per il messaggio di errore).
     * @return true se il campo è valido, false altrimenti.
     */
    public static boolean validateDecimal(TextField textField, String fieldName) {
        try {
            Double.parseDouble(textField.getText());
            return true;
        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Errore di validazione", "Il campo " + fieldName + " deve contenere un numero decimale.", "");
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