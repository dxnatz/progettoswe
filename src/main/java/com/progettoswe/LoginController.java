package com.progettoswe;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;


public class LoginController {

    @FXML
    private TextField mailTextField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private void handleLogin() {
        String mail = mailTextField.getText();
        String password = passwordField.getText();
            

        
        // Implement authentication logic here
        if (authenticate(mail, password)) {
            // Switch to the main view or perform the next action
        } else {
            // Show error message
        }
    }

    @FXML
    private void switchToRegistrate() throws IOException {
        App.setRoot("registrate");
    }

    private boolean authenticate(String username, String password) {
        // Placeholder for authentication logic
        return "admin".equals(username) && "password".equals(password);
    }
}