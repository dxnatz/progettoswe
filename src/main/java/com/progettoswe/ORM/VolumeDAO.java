package com.progettoswe.ORM;

import com.progettoswe.model.Opera;
import com.progettoswe.model.Volume;
import com.progettoswe.model.Edizione;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VolumeDAO {

    private static final String GET_ALL_VOLUMI =
            "SELECT v.*, e.*, o.* FROM volume v " +
                    "JOIN edizione e ON v.id_edizione = e.id_edizione " +
                    "JOIN opera o ON e.id_opera = o.id_opera " +
                    "ORDER BY o.titolo, e.numero_edizione, v.id_volume";

    private static final String SEARCH_VOLUMI =
            "SELECT v.*, e.*, o.* FROM volume v " +
                    "JOIN edizione e ON v.id_edizione = e.id_edizione " +
                    "JOIN opera o ON e.id_opera = o.id_opera " +
                    "WHERE LOWER(o.titolo) LIKE ? OR LOWER(o.autore) LIKE ? OR " +
                    "LOWER(e.isbn) LIKE ? OR LOWER(v.posizione) LIKE ? " +
                    "ORDER BY o.titolo, e.numero_edizione, v.id_volume";

    public static Volume getVolumeById(int idVolume) {
        String query = "SELECT * FROM volume WHERE id_volume = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idVolume);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id_edizione = resultSet.getInt("id_edizione");
                String stato = resultSet.getString("stato");
                String posizione = resultSet.getString("posizione");

                Edizione edizione = EdizioneDAO.getEdizioneById(id_edizione);
                return new Volume(idVolume, edizione, stato, posizione);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean insertVolume(Volume volume) {
        String query = "INSERT INTO volume (id_edizione, stato, posizione) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, volume.getEdizione().getId_edizione());
            statement.setString(2, volume.getStato());
            statement.setString(3, volume.getPosizione());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateVolume(Volume volume) {
        String query = "UPDATE volume SET id_edizione = ?, stato = ?, posizione = ? WHERE id_volume = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, volume.getEdizione().getId_edizione());
            statement.setString(2, volume.getStato());
            statement.setString(3, volume.getPosizione());
            statement.setInt(4, volume.getId_volume());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteVolume(int idVolume) {
        String query = "DELETE FROM volume WHERE id_volume = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idVolume);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateStatoVolume(int idVolume, String nuovoStato) {
        String query = "UPDATE volume SET stato = ? WHERE id_volume = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nuovoStato);
            statement.setInt(2, idVolume);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isVolumeInPrestito(int idVolume) {
        String query = "SELECT 1 FROM prestito WHERE id_volume = ? AND restituito = false";
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idVolume);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Volume> getAllVolumi() throws SQLException {
        List<Volume> volumi = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_VOLUMI)) {

            while (rs.next()) {
                volumi.add(mapResultSetToVolume(rs));
            }
        }
        return volumi;
    }

    public static List<Volume> searchVolumi(String searchTerm) throws SQLException {
        List<Volume> volumi = new ArrayList<>();
        String term = "%" + searchTerm.toLowerCase() + "%";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_VOLUMI)) {

            stmt.setString(1, term);
            stmt.setString(2, term);
            stmt.setString(3, term);
            stmt.setString(4, term);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                volumi.add(mapResultSetToVolume(rs));
            }
        }
        return volumi;
    }

    private static Volume mapResultSetToVolume(ResultSet rs) throws SQLException {
        // Recupera dati opera
        Opera opera = new Opera(
                rs.getInt("id_opera"),
                rs.getString("titolo"),
                rs.getString("autore"),
                rs.getString("genere"),
                rs.getInt("anno_pubblicazione_originale"),
                rs.getString("descrizione")
        );

        // Recupera dati edizione
        Edizione edizione = new Edizione(
                rs.getString("isbn"),
                rs.getInt("anno_pubblicazione"),
                rs.getString("editore"),
                rs.getInt("numero_edizione"),
                opera,
                rs.getInt("id_edizione")
        );

        // Crea volume
        return new Volume(
                rs.getInt("id_volume"),
                edizione,
                rs.getString("stato"),
                rs.getString("posizione")
        );
    }

}