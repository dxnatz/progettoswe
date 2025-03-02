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
import java.time.LocalDate;
import java.sql.Date;


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

        registraUtente(mail, password);
            

        
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

    //metodo per verificare se l'email è già presente nel database
    private static boolean emailEsistente(String email) {
        String query = "SELECT count(*) FROM utente WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, query); // imposto il parametro della query, sostituisco il ? con l'email

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    Alert a = new Alert(AlertType.ERROR, "Email già registrata");
                    return true; //email trovata
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; //email non trovata
    }

    //metodo per registrare un nuovo utente nel database
    private static boolean registraUtente(String email, String password) {
        if(emailEsistente(email)) { //verifico se l'email è già presente nel database
            Alert a = new Alert(AlertType.ERROR, "Email già registrata");
            return false;
        }
        String query = "INSERT INTO utente (nome, cognome, cf, email, pw, cellulare, data_nascita, indirizzo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"; //query per inserire un nuovo utente

        try(Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            //imposto i parametri della query
            LocalDate data = LocalDate.of(1990, 1, 1);
            Date sqlDate = Date.valueOf(data);
            pstmt.setString(1, "Mattia");
            pstmt.setString(2, "Donadoni");
            pstmt.setString(3, "DNDMTT03L11D612B");
            pstmt.setString(4, email);
            pstmt.setString(5, password);
            pstmt.setString(6, "3333333333");
            pstmt.setDate(7, sqlDate);
            pstmt.setString(8, "Via Roma 1");

            int rowsInserted = pstmt.executeUpdate(); //eseguo la query
            return rowsInserted > 0; //restituisce true se l'utente è stato registrato con successo

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; //se non è stato possibile registrare l'utente a causa di un errore
    }

}