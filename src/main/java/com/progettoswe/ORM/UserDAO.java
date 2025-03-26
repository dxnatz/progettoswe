package com.progettoswe.ORM;

import com.progettoswe.model.Utente;

import java.sql.*;
import java.time.LocalDate;

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
            return rowsInserted > 0; //restituisce true se l'utente è stato registrato con successo

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public static boolean login(String email, String password) {
        String query = "SELECT count(*) FROM utente WHERE email = ? AND pw = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                // Verifica se la query ha restituito risultati
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                } else {
                    return false;  // Restituisce false se non ci sono risultati
                }
            } catch (SQLException e) {
                // Gestisci eventuali eccezioni nella query
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            // Gestisci eccezioni di connessione
            e.printStackTrace();
            return false;
        }
    }

    // Metodo per controllare se l'email è corretta e la password è errata
    public static boolean emailCorrettaPasswordErrata(String email, String password) {

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

    // Metodo per restituire un utente dal database
    public static Utente utente(String email) {
        String query = "SELECT * FROM utente WHERE email = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id_utente = resultSet.getInt("id_utente");
                String nome = resultSet.getString("nome");
                String cognome = resultSet.getString("cognome");
                String codiceFiscale = resultSet.getString("cf");
                String cellulare = resultSet.getString("cellulare");
                LocalDate dataNascita = resultSet.getDate("data_nascita").toLocalDate();
                String indirizzo = resultSet.getString("indirizzo");
                LocalDate dataRegistrazione = resultSet.getDate("data_registrazione").toLocalDate();

                return new Utente(id_utente, nome, cognome, codiceFiscale, email, cellulare, "", dataNascita, indirizzo, dataRegistrazione);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateUtente(Utente utente) {
        String query;
        if (utente.getPassword() == null || utente.getPassword().isEmpty()) {
            query = "UPDATE utente SET nome = ?, cognome = ?, cf = ?, email = ? WHERE id_utente = ?";
        } else {
            query = "UPDATE utente SET nome = ?, cognome = ?, cf = ?, email = ?, pw = ? WHERE id_utente = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
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

    public static boolean deleteUtente(Utente utente) {
        String checkQuery = "SELECT COUNT(*) FROM prestito WHERE id_utente = ?";
        String deleteQuery = "DELETE FROM utente WHERE id_utente = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            //Controllo se ha prestiti attivi
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, utente.getId_utente());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return false;
            }

            //Se non ha prestiti attivi, procedo con l'eliminazione
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, utente.getId_utente());
            int rowsDeleted = deleteStmt.executeUpdate();

            return rowsDeleted > 0; //Ritorna true se l'utente è stato eliminato

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; //Se c'è un errore, ritorna false
    }

}