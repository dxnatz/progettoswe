package com.progettoswe.ORM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.progettoswe.model.Utente;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.Date;

public class UserDAO {

    // Metodo per inserire un nuovo utente nel database
    public static boolean inserimentoUtente(Utente utente) {
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

    // Metodo per controllare se un utente è già registrato
    public static boolean emailEsistente(String email) {
        String query = "SELECT count (*) FROM utente WHERE email = ?"; //query per contare il numero di email uguali a quella inserita

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email); // imposto il parametro della query, sostituisco il ? con l'email

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; //email trovata
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; //email non trovata
    }

    // Metodo per controllare se un codice fiscale è già registrato
    public static boolean cfEsistente(String cf) {
        String query = "SELECT count (*) FROM utente WHERE cf = ?"; //query per contare il numero di codici fiscali uguali a quello inserito

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, cf); // imposto il parametro della query, sostituisco il ? con il codice fiscale

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; //codice fiscale trovato
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; //codice fiscale non trovato
    }

    // Metodo per controllare se un numero di cellulare è già registrato
    public static boolean cellulareEsistente(String cellulare) {
        String query = "SELECT count (*) FROM utente WHERE cellulare = ?"; //query per contare il numero di numeri di cellulare uguali a quello inserito

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, cellulare); // imposto il parametro della query, sostituisco il ? con il numero di cellulare

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; //numero di cellulare trovato
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; //numero di cellulare non trovato
    }
}