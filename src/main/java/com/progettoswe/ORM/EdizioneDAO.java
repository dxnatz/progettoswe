package com.progettoswe.ORM;

import com.progettoswe.model.Edizione;
import com.progettoswe.model.Opera;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EdizioneDAO {

    public static Edizione getEdizione(String titolo, int edizione) {
        String query = "SELECT * FROM opera JOIN edizione ON opera.id_opera = edizione.id_opera WHERE opera.titolo = ? AND edizione.numero_edizione = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, titolo);
            statement.setInt(2, edizione);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Recupera i dati dell'opera
                int idOpera = resultSet.getInt("id_opera");
                String titoloOpera = resultSet.getString("titolo");
                String autore = resultSet.getString("autore");
                String genere = resultSet.getString("genere");
                int annoPubblicazioneOriginale = resultSet.getInt("anno_pubblicazione_originale");
                String descrizione = resultSet.getString("descrizione");
                Opera opera = new Opera(idOpera, titoloOpera, autore, genere, annoPubblicazioneOriginale, descrizione);
                // Recupera i dati dell'edizione
                String isbn = resultSet.getString("isbn");
                String editore = resultSet.getString("editore");
                int annoPubblicazione = resultSet.getInt("anno_pubblicazione");
                int numeroEdizione = resultSet.getInt("numero_edizione");
                int idEdizione = resultSet.getInt("id_edizione");
                // Crea un oggetto Edizione
                Edizione edizioneObj = new Edizione(isbn, annoPubblicazione, editore, numeroEdizione, opera, idEdizione);
                return edizioneObj;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
