package com.progettoswe.ORM;

import com.progettoswe.model.Edizione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EditionDAO {

    public static ArrayList<Edizione> getEdizioni() throws SQLException {
        String query = "SELECT * FROM edizione";
        ArrayList<Edizione> edizioni = new ArrayList<Edizione>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Edizione edizione = new Edizione(resultSet.getString("isbn"), resultSet.getInt("anno_pubblicazione"), resultSet.getString("editore"), resultSet.getInt("numero"), PieceDAO.getOpera(resultSet.getInt("id_opera")), resultSet.getInt("id_edizione"));
                edizioni.add(edizione);
            }
            return edizioni;
        }
    }

    public static ArrayList<Edizione> getCopiePerEdizione() throws SQLException {
        String query = "SELECT *, count(*) AS copie FROM edizione JOIN volume ON volume.id_edizione = edizione.id_edizione GROUP BY id_edizione";
        ArrayList<Edizione> edizioni = new ArrayList<Edizione>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Edizione edizione = new Edizione(resultSet.getString("isbn"), resultSet.getInt("anno_pubblicazione"), resultSet.getString("editore"), resultSet.getInt("numero"), PieceDAO.getOpera(resultSet.getInt("id_opera")), resultSet.getInt("id_edizione"));
                edizioni.add(edizione);
            }
            return edizioni;
        }
    }

    public static Edizione getEdizione(int id_edizione) throws SQLException {
        String query = "SELECT * FROM edizione WHERE id_edizione = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id_edizione);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Edizione(resultSet.getString("isbn"), resultSet.getInt("anno_pubblicazione"), resultSet.getString("editore"), resultSet.getInt("numero"), PieceDAO.getOpera(resultSet.getInt("id_opera")), resultSet.getInt("id_edizione"));
            }
            return null;
        }
    }
}
