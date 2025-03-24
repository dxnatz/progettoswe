package com.progettoswe.ORM;

import com.progettoswe.model.Volume;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VolumeDAO {

    public static ArrayList<Volume> getVolumi() throws SQLException {
        String query = "SELECT * FROM volume";
        ArrayList<Volume> volumi = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            volumi = new ArrayList<>();

            while (resultSet.next()) {
                Volume volume = new Volume(resultSet.getInt("id_volume"), EditionDAO.getEdizione(resultSet.getInt("id_edizione")), resultSet.getString("stato"), resultSet.getString("posizione"));
                volumi.add(volume);
            }
            return volumi;
        }
    }

    public static Volume getVolume(int id_volume) throws SQLException {
        String query = "SELECT * FROM volume WHERE id_volume = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id_volume);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Volume(resultSet.getInt("id_volume"), EditionDAO.getEdizione(resultSet.getInt("id_edizione")), resultSet.getString("stato"), resultSet.getString("posizione"));
            }
            return null;
        }
    }

    public static boolean aggiungiVolume(Volume volume) throws SQLException {
        String query = "INSERT INTO volume (id_edizione, stato, posizione) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, volume.edizione().getId_edizione());
            statement.setString(2, volume.getStato());
            statement.setString(3, volume.getPosizione());
            return statement.executeUpdate() > 0;
        }
    }
}
