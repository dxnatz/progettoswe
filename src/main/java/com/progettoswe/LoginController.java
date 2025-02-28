package com.progettoswe;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        // Implement authentication logic here
        if (authenticate(username, password)) {
            // Switch to the main view or perform the next action
        } else {
            // Show error message
        }
    }

    private boolean authenticate(String username, String password) {
        // Placeholder for authentication logic
        return "admin".equals(username) && "password".equals(password);
    }
}