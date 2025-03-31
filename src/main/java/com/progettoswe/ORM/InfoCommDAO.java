package com.progettoswe.ORM;

import com.progettoswe.model.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

}
