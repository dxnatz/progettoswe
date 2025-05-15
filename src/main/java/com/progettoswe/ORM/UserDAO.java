package com.progettoswe.ORM;

import com.progettoswe.model.Utente;

import java.sql.*;
import java.time.LocalDate;

public class UserDAO {

    // Metodo per inserire un nuovo utente nel database
    public static boolean inserimentoUtente(Utente utente) {
        String query = "INSERT INTO utente (nome, cognome, cf, email, pw, cellulare, data_nascita, indirizzo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);

            // Imposto i parametri della query
            Date sqlDate = Date.valueOf(utente.getDataNascita());
            pstmt.setString(1, utente.getNome());
            pstmt.setString(2, utente.getCognome());
            pstmt.setString(3, utente.getCodiceFiscale());
            pstmt.setString(4, utente.getEmail());
            pstmt.setString(5, utente.getPassword());
            pstmt.setString(6, utente.getCellulare());
            pstmt.setDate(7, sqlDate);
            pstmt.setString(8, utente.getIndirizzo());

            int rowsInserted = pstmt.executeUpdate(); // Eseguo la query
            return rowsInserted > 0; // Restituisce true se l'utente è stato registrato con successo

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Se non è stato possibile registrare l'utente a causa di un errore
    }

    // Metodo per controllare se un utente è già registrato (email)
    public static boolean emailEsistente(String email) {
        String query = "SELECT count(*) FROM utente WHERE email = ?"; // Query per contare il numero di email uguali a quella inserita

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email); // Imposto il parametro della query, sostituisco il ? con l'email

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; // Email trovata
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Email non trovata
    }

    // Metodo per controllare se un codice fiscale è già registrato
    public static boolean cfEsistente(String cf) {
        String query = "SELECT count(*) FROM utente WHERE cf = ?"; // Query per contare il numero di codici fiscali uguali a quello inserito

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, cf); // Imposto il parametro della query, sostituisco il ? con il codice fiscale

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; // Codice fiscale trovato
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Codice fiscale non trovato
    }

    // Metodo per controllare se un numero di cellulare è già registrato
    public static boolean cellulareEsistente(String cellulare) {
        String query = "SELECT count(*) FROM utente WHERE cellulare = ?"; // Query per contare il numero di numeri di cellulare uguali a quello inserito

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, cellulare); // Imposto il parametro della query, sostituisco il ? con il numero di cellulare

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; // Numero di cellulare trovato
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Numero di cellulare non trovato
    }

    // Metodo per login
    public static boolean login(String email, String password) {
        String query = "SELECT count(*) FROM utente WHERE email = ? AND pw = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metodo per ottenere un utente dal database
    public static Utente utente(String email) {
        String query = "SELECT * FROM utente WHERE email = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int idUtente = rs.getInt("id_utente");
                    String nome = rs.getString("nome");
                    String cognome = rs.getString("cognome");
                    String codiceFiscale = rs.getString("cf");
                    String cellulare = rs.getString("cellulare");
                    LocalDate dataNascita = rs.getDate("data_nascita").toLocalDate();
                    String indirizzo = rs.getString("indirizzo");
                    LocalDate dataRegistrazione = rs.getDate("data_registrazione").toLocalDate();

                    return new Utente(idUtente, nome, cognome, codiceFiscale, email, cellulare, "", dataNascita, indirizzo, dataRegistrazione);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Utente non trovato
    }

    // Metodo per aggiornare un utente
    public static boolean updateUtente(Utente utente) {
        String query = (utente.getPassword() == null || utente.getPassword().isEmpty()) ?
                "UPDATE utente SET nome = ?, cognome = ?, cf = ?, email = ? WHERE id_utente = ?" :
                "UPDATE utente SET nome = ?, cognome = ?, cf = ?, email = ?, pw = ? WHERE id_utente = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, utente.getNome());
            pstmt.setString(2, utente.getCognome());
            pstmt.setString(3, utente.getCodiceFiscale());
            pstmt.setString(4, utente.getEmail());

            if (utente.getPassword() == null || utente.getPassword().isEmpty()) {
                pstmt.setInt(5, utente.getId_utente());
            } else {
                pstmt.setString(5, utente.getPassword());
                pstmt.setInt(6, utente.getId_utente());
            }

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Metodo per eliminare un utente
    public static boolean deleteUtente(Utente utente) {
        String checkQuery = "SELECT COUNT(*) FROM prestito WHERE id_utente = ?";
        String deleteQuery = "DELETE FROM utente WHERE id_utente = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, utente.getId_utente());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
                    deleteStmt.setInt(1, utente.getId_utente());
                    int rowsDeleted = deleteStmt.executeUpdate();
                    return rowsDeleted > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean emailCorrettaPasswordErrata(String email, String password) {
        String query = "SELECT count(*) FROM utente WHERE email = ? AND pw = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; // La combinazione è corretta
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true; // La combinazione è errata
    }
}
