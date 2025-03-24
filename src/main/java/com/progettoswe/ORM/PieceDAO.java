package com.progettoswe.ORM;

import com.progettoswe.model.Opera;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PieceDAO {

    public static ArrayList<Opera> getOpere() throws SQLException {
        String query = "SELECT * FROM opera";
        ArrayList<Opera> opere = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            opere = new ArrayList<>();

            while (resultSet.next()) {
                Opera opera = new Opera(resultSet.getInt("id_opera"), resultSet.getString("titolo"), resultSet.getString("autore"), resultSet.getString("genere"), resultSet.getInt("annoPubblicazioneOrinale"), resultSet.getString("descrizione"));
                opere.add(opera);
            }
            return opere;
        }
    }

    public static Opera getOpera(int id_opera) throws SQLException {
        String query = "SELECT * FROM opera WHERE id_opera = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id_opera);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Opera(resultSet.getInt("id_opera"), resultSet.getString("titolo"), resultSet.getString("autore"), resultSet.getString("genere"), resultSet.getInt("annoPubblicazioneOrinale"), resultSet.getString("descrizione"));
            }
            return null;
        }
    }
}
