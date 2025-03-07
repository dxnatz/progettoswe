package com.progettoswe.dao;

import com.progettoswe.model.Utente;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import com.progettoswe.dao.DatabaseConnection;

import java.sql.*;

public class UserDAO {

    // Metodo per inserire un nuovo utente nel database
    public boolean saveUser(Utente utente) {
        String query = "INSERT INTO utente (nome, cognome, cf, email, pw, cellulare, data_nascita, indirizzo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            //imposto i parametri della query
            Date sqlDate = Date.valueOf(utente.getDataNascita());
            pstmt.setString(1, utente.getNome());
            pstmt.setString(2, utente.getCognome());
            pstmt.setString(3, utente.getCodiceFiscale());
            pstmt.setString(4, utente.getEmail());
            pstmt.setString(5, utente.getPassword());
            pstmt.setString(6, utente.getCellulare());
            pstmt.setDate(7, sqlDate);
            pstmt.setString(8, utente.getIndirizzo());

            int rowsInserted = pstmt.executeUpdate(); //eseguo la query
            Alert a = new Alert(AlertType.INFORMATION, "Ti sei registrato con successo, verrai reindirizzato alla pagina di login");
            a.setHeaderText("Registrazione avvenuta con successo");
            a.setTitle("Registrazione avvenuta");
            a.showAndWait();
            return rowsInserted > 0; //restituisce true se l'utente è stato registrato con successo

        } catch (SQLException e) {
            e.printStackTrace();
        }
        Alert a = new Alert(AlertType.ERROR, "Errore non previsto nella registrazione");
        a.setHeaderText("Registrazione fallita");
        a.setTitle("Errore nella registrazione");
        a.showAndWait();
        return false; //se non è stato possibile registrare l'utente a causa di un errore
    }

    // Metodo per verificare se l'email è già presente nel database
    public boolean emailExists(String email) {
        String query = "SELECT count(*) FROM utente WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Altri metodi simili per codice fiscale e cellulare possono essere creati qui
}
