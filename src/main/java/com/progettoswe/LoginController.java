package com.progettoswe;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.IOException;


public class LoginController {

    @FXML
    private TextField mailTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button loginButton;

    @FXML
    private void handleLogin() {
        String mail = mailTextField.getText();
        String password = passwordTextField.getText();
            

        
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

    private boolean authenticate(String email, String password) {
        // Implement authentication logic here
        String query = "SELECT count(*) FROM utente WHERE email = ? AND pw = ?";

        try (Connection connection = DatabaseConnection.getConnection()){
             PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try(ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Accesso effettuato");
                    return true; 
                }
                throw new SQLException();
            } 
            catch (SQLException e) {
            e.printStackTrace();
            Alert a = new Alert(AlertType.ERROR, "Errore durante l'accesso, campi inseriri non corretti, riprova");
            a.setHeaderText("Errore");
            a.setTitle("Errore durante l'accesso");
            a.showAndWait();
            mailTextField.clear();
            passwordTextField.clear();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}