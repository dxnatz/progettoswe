package com.progettoswe.ORM;

import com.progettoswe.model.Edizione;
import com.progettoswe.model.Opera;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EdizioneDAO {

    private static final String GET_ALL_EDIZIONI =
            "SELECT e.*, o.* FROM edizione e JOIN opera o ON e.id_opera = o.id_opera ORDER BY o.titolo, e.numero_edizione";

    private static final String SEARCH_EDIZIONI =
            "SELECT e.*, o.* FROM edizione e JOIN opera o ON e.id_opera = o.id_opera " +
                    "WHERE LOWER(o.titolo) LIKE ? OR LOWER(o.autore) LIKE ? OR LOWER(e.editore) LIKE ? OR LOWER(e.isbn) LIKE ? " +
                    "ORDER BY o.titolo, e.numero_edizione";

    public static Edizione getEdizioneById(int idEdizione) {
        String query = "SELECT e.*, o.* FROM edizione e JOIN opera o ON e.id_opera = o.id_opera WHERE e.id_edizione = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idEdizione);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                int annoPubblicazione = resultSet.getInt("anno_pubblicazione");
                String editore = resultSet.getString("editore");
                int numero = resultSet.getInt("numero_edizione");

                // Dati opera
                int id_opera = resultSet.getInt("id_opera");
                String titolo = resultSet.getString("titolo");
                String autore = resultSet.getString("autore");
                String genere = resultSet.getString("genere");
                int annoOriginale = resultSet.getInt("anno_pubblicazione_originale");
                String descrizione = resultSet.getString("descrizione");

                Opera opera = new Opera(id_opera, titolo, autore, genere, annoOriginale, descrizione);
                return new Edizione(isbn, annoPubblicazione, editore, numero, opera, idEdizione);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Edizione getEdizioneByIsbn(String isbn) {
        String query = "SELECT e.*, o.* FROM edizione e JOIN opera o ON e.id_opera = o.id_opera WHERE e.isbn = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int idEdizione = resultSet.getInt("id_edizione");
                int annoPubblicazione = resultSet.getInt("anno_pubblicazione");
                String editore = resultSet.getString("editore");
                int numero = resultSet.getInt("numero_edizione");

                // Dati opera
                int id_opera = resultSet.getInt("id_opera");
                String titolo = resultSet.getString("titolo");
                String autore = resultSet.getString("autore");
                String genere = resultSet.getString("genere");
                int annoOriginale = resultSet.getInt("anno_pubblicazione_originale");
                String descrizione = resultSet.getString("descrizione");

                Opera opera = new Opera(id_opera, titolo, autore, genere, annoOriginale, descrizione);
                return new Edizione(isbn, annoPubblicazione, editore, numero, opera, idEdizione);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int insertEdizione(Edizione edizione) {
        String query = "INSERT INTO edizione (isbn, anno_pubblicazione, editore, numero_edizione, id_opera) VALUES (?, ?, ?, ?, ?) RETURNING id_edizione";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, edizione.getIsbn());
            statement.setInt(2, edizione.getAnnoPubblicazione());
            statement.setString(3, edizione.getEditore());
            statement.setInt(4, edizione.getNumero());
            statement.setInt(5, edizione.getOpera().getId_opera());

            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                return rs.getInt("id_edizione");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean updateEdizione(Edizione edizione) {
        String query = "UPDATE edizione SET isbn = ?, anno_pubblicazione = ?, editore = ?, numero_edizione = ?, id_opera = ? WHERE id_edizione = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, edizione.getIsbn());
            statement.setInt(2, edizione.getAnnoPubblicazione());
            statement.setString(3, edizione.getEditore());
            statement.setInt(4, edizione.getNumero());
            statement.setInt(5, edizione.getOpera().getId_opera());
            statement.setInt(6, edizione.getId_edizione());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteEdizione(int idEdizione) {
        String query = "DELETE FROM edizione WHERE id_edizione = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idEdizione);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean existsEdizioneByIsbn(String isbn) {
        String query = "SELECT 1 FROM edizione WHERE isbn = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Edizione> getAllEdizioni() throws SQLException {
        List<Edizione> edizioni = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_EDIZIONI)) {

            while (rs.next()) {
                edizioni.add(mapResultSetToEdizione(rs));
            }
        }
        return edizioni;
    }

    public static List<Edizione> searchEdizioni(String searchTerm) throws SQLException {
        List<Edizione> edizioni = new ArrayList<>();
        String term = "%" + searchTerm.toLowerCase() + "%";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_EDIZIONI)) {

            stmt.setString(1, term);
            stmt.setString(2, term);
            stmt.setString(3, term);
            stmt.setString(4, term);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                edizioni.add(mapResultSetToEdizione(rs));
            }
        }
        return edizioni;
    }

    private static Edizione mapResultSetToEdizione(ResultSet rs) throws SQLException {
        // Recupera dati opera
        Opera opera = new Opera(
                rs.getInt("id_opera"),
                rs.getString("titolo"),
                rs.getString("autore"),
                rs.getString("genere"),
                rs.getInt("anno_pubblicazione_originale"),
                rs.getString("descrizione")
        );

        // Crea edizione
        return new Edizione(
                rs.getString("isbn"),
                rs.getInt("anno_pubblicazione"),
                rs.getString("editore"),
                rs.getInt("numero_edizione"),
                opera,
                rs.getInt("id_edizione")
        );
    }
}