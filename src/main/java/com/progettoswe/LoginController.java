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
    private TextField emailTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button loginButton;

    @FXML
    private void switchToRegistrate() throws IOException {
        App.setRoot("registrate");
    }

    @FXML
    private void handleLogin() {
        String mail = emailTextField.getText();
        String password = passwordTextField.getText();
        
        authenticate(mail, password);
    }

    private boolean authenticate(String email, String password) {

        if (email.isEmpty() || password.isEmpty()) {
            Alert a = new Alert(AlertType.ERROR, "Inserisci email e password");
            a.setHeaderText("Errore di accesso");
            a.setTitle("Errore durante l'accesso");
            a.showAndWait();
            return false;
            
        }

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

                if(emailCorrettaPasswordErrata(email, password)){
                    Alert a = new Alert(AlertType.ERROR, "Password errata\n\nReinseriscila");
                    a.setHeaderText("Errore di accesso");
                    a.setTitle("Errore durante l'accesso");
                    a.showAndWait();
                    passwordTextField.clear();

                } else {
                    Alert a = new Alert(AlertType.INFORMATION, "Email non registrata nel nostro sistema\n\nRegistrati per accedere");
                    a.setHeaderText("Errore di accesso");
                    a.setTitle("Errore durante l'accesso");
                    a.showAndWait();
                    emailTextField.clear();
                    passwordTextField.clear();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean emailCorrettaPasswordErrata(String email, String password) {

        String query = "SELECT count(*) FROM utente WHERE email = ? AND pw != ?";

        try (Connection connection = DatabaseConnection.getConnection()){
            
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try(ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; 
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

}