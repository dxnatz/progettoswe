package com.progettoswe;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;


public class RegistrationController {

    private String erroreMessage = "Errore";

    @FXML
    private TextField mailTextField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button registrateButton;

    @FXML
    private void handleRegistration() {
        String mail = mailTextField.getText();
        String password = passwordField.getText();
        
        // Implement registration logic here
        if (register(mail, password)) {
            Alert a = new Alert(AlertType.INFORMATION, "Utente registrato con successo");
            a.showAndWait();
            switchToLogin();
        
        } else {
            Alert a = new Alert(AlertType.ERROR, erroreMessage);
            a.setHeaderText("Registrazione fallita");
            a.setTitle("Errore nella registrazione");
            a.show();
        }

        mailTextField.clear();
        passwordField.clear();  
    }

    private boolean register(String mail, String password) {
        if (mail.isEmpty() || password.isEmpty()) {
            erroreMessage = "Ci sono dei campi vuoti";
            return false;
        }

        if (mail.length() < 5 || mail.length() > 50 || mail.contains(" ") || password.contains(" ") || password.length() < 5) {
            erroreMessage = "Email o password non validi\nEmail deve essere tra 5 e 50 caratteri\nLa password deve essere lunga almeno 5 caratteri\nNon devono contenere spazi";
            return false;
        }

        if (App.users.get(mail) != null) {
            erroreMessage = "Email già registrata";
            return false;
        }

        App.users.put(mail, password);
        return true;

    }

    @FXML
    private void switchToLogin() {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}