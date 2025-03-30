package com.progettoswe.ORM;

import com.progettoswe.model.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class InfoCommDAO {

    //recupero dei commenti dal database
    /*public static ArrayList<String> getCommentiOpera() {
        String query = "SELECT opera.id_opera, opera.titolo, opera.autore, opera.genere, opera.anno_pubblicazione_originale, opera.descrizione," +
                        "edizione.id_edizione, edizione.numero_edizione, edizione.editore, edizione.anno_pubblicazione, edizione.isbn," +
                        "commento.id_commento, commento.testo" +
                        "FROM commento JOIN edizione ON commento.id_edizione = edizione.id_edizione JOIN opera ON opera.id_opera = edizione.id_opera JOIN utente ON utente.id_utente = commento.id_utente" +
                       "WHERE edizione.nome_opera = ? AND commento.id_utente = ? AND edizione.numero_edizione = ?";
        ArrayList<String> commenti = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, Session.getNomeOpera());
            preparedStatement.setInt(2, Session.getUtente().getId_utente());
            preparedStatement.setInt(3, Session.getEdizione());
            var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id_opera = resultSet.getInt("id_opera");
                String titolo_opera = resultSet.getString("titolo");
                String autore = resultSet.getString("autore");
                String genere = resultSet.getString("genere");
                int anno_pubblicazione = resultSet.getInt("anno_pubblicazione_originale");
                String descrizione = resultSet.getString("descrizione");

                Opera opera = new Opera(id_opera, titolo_opera, autore, genere, anno_pubblicazione, descrizione);

                int id_edizione = resultSet.getInt("id_edizione");
                int numero_edizione = resultSet.getInt("numero_edizione");
                String editore = resultSet.getString("editore");
                int anno_pubblicazione_edizione = resultSet.getInt("anno_pubblicazione");
                String isbn = resultSet.getString("isbn");

                Edizione edizione = new Edizione(isbn, anno_pubblicazione_edizione, editore, numero_edizione, opera, id_edizione);

                int id_commento = resultSet.getInt("id_commento");
                String testo_commento = resultSet.getString("testo");

                Commento commento = new Commento(id_commento, Session.getUtente(), opera, null, testo_commento);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commenti;
    }*/

    //recupero dei commenti dal database
    //todo da creare commento, edizione, utente e opera
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

}
