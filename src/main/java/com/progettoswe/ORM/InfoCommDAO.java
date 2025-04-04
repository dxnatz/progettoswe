package com.progettoswe.ORM;

import com.progettoswe.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InfoCommDAO {

    //recupero dei commenti dal database

    public static ArrayList<String> getCommentiOpera(){
        String query = "SELECT commento.testo, edizione.numero_edizione, utente.nome " +
                "FROM commento JOIN edizione ON commento.id_edizione = edizione.id_edizione JOIN opera ON edizione.id_opera = opera.id_opera JOIN utente ON utente.id_utente = commento.id_utente " +
                "WHERE opera.titolo = ?";

        ArrayList<String> commentiOpera = new ArrayList<>();

        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, Session.getNomeOpera());
            var resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                String testo = resultSet.getString("testo");
                String numero_edizione = resultSet.getString("numero_edizione");
                String nome_utente = resultSet.getString("nome");
                String commento = nome_utente + ":\n" + testo + "\n\n" + numero_edizione + " edizione";
                commentiOpera.add(commento);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return commentiOpera;
    }


    //inserimento di un commento nel database
    public static boolean aggiungiCommento(String commento, int idEdizione) {
        String query = "INSERT INTO commento (testo, id_edizione, id_utente) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, commento);
            preparedStatement.setInt(2, idEdizione);
            preparedStatement.setInt(3, Session.getUtente().getId_utente());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean utenteHaCommentato(int idEdizione) {
        String query = "SELECT COUNT(*) FROM Commento WHERE id_utente = ? AND id_edizione = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, Session.getUtente().getId_utente());
            ps.setInt(2, idEdizione);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Se c'è almeno un commento, restituisce true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Se non ci sono commenti
    }

    public static boolean modificaCommento(String commento, int idEdizione) {
        String query = "UPDATE commento SET testo = ? WHERE id_edizione = ? AND id_utente = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, commento);
            preparedStatement.setInt(2, idEdizione);
            preparedStatement.setInt(3, Session.getUtente().getId_utente());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getCommentoUtente(int idEdizione) {
        String query = "SELECT testo FROM commento WHERE id_edizione = ? AND id_utente = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idEdizione);
            preparedStatement.setInt(2, Session.getUtente().getId_utente());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("testo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void eliminaCommento(int idEdizione) {
        String query = "DELETE FROM commento WHERE id_edizione = ? AND id_utente = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idEdizione);
            preparedStatement.setInt(2, Session.getUtente().getId_utente());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Commento> getCommentiCompletiPerOpera(int idOpera) throws SQLException {
        String query = "SELECT c.id_commento, c.testo, " +
                "u.id_utente, u.nome, u.cognome, u.cf, u.email, u.cellulare, " +
                "e.id_edizione, e.numero_edizione, e.editore, e.anno_pubblicazione, e.isbn, " +
                "p.id_prestito " +
                "FROM commento c " +
                "JOIN utente u ON c.id_utente = u.id_utente " +
                "JOIN edizione e ON c.id_edizione = e.id_edizione " +
                "LEFT JOIN prestito p ON p.id_utente = u.id_utente AND p.id_volume IN " +
                "(SELECT id_volume FROM volume WHERE id_edizione = e.id_edizione) " +
                "WHERE e.id_opera = ? " +
                "ORDER BY c.id_commento DESC";

        List<Commento> comments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idOpera);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Costruzione oggetto Utente
                Utente utente = new Utente(
                        rs.getInt("id_utente"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("cf"),
                        rs.getString("email"),
                        rs.getString("cellulare"),
                        null, // dataNascita (non necessaria)
                        null  // indirizzo (non necessario)
                );

                // Costruzione oggetto Edizione (opera sarà null perché non ci serve)
                Edizione edizione = new Edizione(
                        rs.getString("isbn"),
                        rs.getInt("anno_pubblicazione"),
                        rs.getString("editore"),
                        rs.getInt("numero_edizione"),
                        null, // opera (non necessaria)
                        rs.getInt("id_edizione")
                );

                // Costruzione Prestito (solo se esiste)
                Prestito prestito = rs.getObject("id_prestito") != null ?
                        new Prestito(rs.getInt("id_prestito"), null, null, null, false, 0) :
                        null;

                // Creazione Commento
                comments.add(new Commento(
                        rs.getInt("id_commento"),
                        utente,
                        edizione,
                        prestito,
                        rs.getString("testo")
                ));
            }
        }
        return comments;
    }

    public static String getTitoloOpera(int idOpera) throws SQLException {
        String query = "SELECT titolo FROM opera WHERE id_opera = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idOpera);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("titolo") : null;
        }
    }

    public static List<Commento> getCommentiCompletiPerVolume(int idVolume) throws SQLException {
        String query = "SELECT c.id_commento, c.testo, " +
                "u.id_utente, u.nome, u.cognome, u.cf, u.email, u.cellulare, " +
                "e.id_edizione, e.numero_edizione, e.editore, e.anno_pubblicazione, e.isbn, " +
                "p.id_prestito, p.data_inizio " +
                "FROM commento c " +
                "JOIN prestito p ON c.id_prestito = p.id_prestito " +
                "JOIN utente u ON c.id_utente = u.id_utente " +
                "JOIN edizione e ON c.id_edizione = e.id_edizione " +
                "WHERE p.id_volume = ? " +
                "ORDER BY c.id_commento DESC";

        List<Commento> comments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idVolume);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Utente utente = new Utente(
                        rs.getInt("id_utente"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("cf"),
                        rs.getString("email"),
                        rs.getString("cellulare"),
                        null, null
                );

                Edizione edizione = new Edizione(
                        rs.getString("isbn"),
                        rs.getInt("anno_pubblicazione"),
                        rs.getString("editore"),
                        rs.getInt("numero_edizione"),
                        null,
                        rs.getInt("id_edizione")
                );

                Prestito prestito = new Prestito(
                        rs.getInt("id_prestito"),
                        null,
                        utente,
                        rs.getDate("data_inizio").toLocalDate(),
                        false,
                        0
                );

                comments.add(new Commento(
                        rs.getInt("id_commento"),
                        utente,
                        edizione,
                        prestito,
                        rs.getString("testo")
                ));
            }
        }
        return comments;
    }

    public static List<Commento> getCommentiOpera(int idOpera) throws SQLException {
        String query = "SELECT c.*, e.numero_edizione, u.nome, u.cognome " +
                "FROM commento c " +
                "JOIN edizione e ON c.id_edizione = e.id_edizione " +
                "JOIN utente u ON c.id_utente = u.id_utente " +
                "WHERE e.id_opera = ? AND c.id_prestito IS NULL";

        List<Commento> commenti = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idOpera);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Mappatura dell'utente
                Utente utente = new Utente(
                        rs.getInt("id_utente"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        null, null, null, null, null
                );

                // Mappatura dell'edizione (solo dati essenziali)
                Edizione edizione = new Edizione(
                        null, 0, null,
                        rs.getInt("numero_edizione"),
                        null,
                        rs.getInt("id_edizione")
                );

                // Creazione del commento
                Commento commento = new Commento(
                        rs.getInt("id_commento"),
                        utente,
                        edizione,
                        null, // Prestito null per commenti opera
                        rs.getString("testo")
                );

                commenti.add(commento);
            }
        }
        return commenti;
    }

    public static List<Commento> getCommentiVolume(int idVolume) throws SQLException {
        String query = "SELECT c.*, e.numero_edizione, u.nome, u.cognome, p.data_inizio " +
                "FROM commento c " +
                "JOIN prestito p ON c.id_prestito = p.id_prestito " +
                "JOIN edizione e ON c.id_edizione = e.id_edizione " +
                "JOIN utente u ON c.id_utente = u.id_utente " +
                "WHERE p.id_volume = ?";

        List<Commento> commenti = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idVolume);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Mappatura dell'utente
                Utente utente = new Utente(
                        rs.getInt("id_utente"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        null, null, null, null, null
                );

                // Mappatura dell'edizione (solo dati essenziali)
                Edizione edizione = new Edizione(
                        null, 0, null,
                        rs.getInt("numero_edizione"),
                        null,
                        rs.getInt("id_edizione")
                );

                // Mappatura del prestito (solo dati essenziali)
                Prestito prestito = new Prestito(
                        rs.getInt("id_prestito"),
                        null, // Volume non necessario
                        null, // Utente già mappato
                        rs.getDate("data_inizio").toLocalDate(),
                        false, // restituito
                        0 // rinnovi
                );

                // Creazione del commento
                Commento commento = new Commento(
                        rs.getInt("id_commento"),
                        utente,
                        edizione,
                        prestito,
                        rs.getString("testo")
                );

                commenti.add(commento);
            }
        }
        return commenti;
    }
}