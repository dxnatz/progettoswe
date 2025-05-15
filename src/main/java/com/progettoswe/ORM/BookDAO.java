package com.progettoswe.ORM;

import com.progettoswe.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookDAO {

    public static Catalogo caricaCatalogo() {
        Catalogo catalogo = new Catalogo();
        String query = "SELECT DISTINCT ON (edizione.id_edizione) " +
                "opera.id_opera, " +
                "opera.titolo, " +
                "opera.autore, " +
                "opera.genere, " +
                "edizione.id_edizione, " +
                "edizione.isbn, " +
                "edizione.editore, " +
                "edizione.numero_edizione, " +
                "volume.id_volume, " +
                "volume.stato, " +
                "volume.posizione, " +
                "opera.anno_pubblicazione_originale, " +
                "edizione.anno_pubblicazione, " +
                "opera.descrizione " +
                "FROM opera " +
                "JOIN edizione ON opera.id_opera = edizione.id_opera " +
                "JOIN volume ON edizione.id_edizione = volume.id_edizione;";

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id_opera = resultSet.getInt("id_opera");
                String titolo = resultSet.getString("titolo");
                String autore = resultSet.getString("autore");
                String genere = resultSet.getString("genere");
                int id_edizione = resultSet.getInt("id_edizione");
                String isbn = resultSet.getString("isbn");
                String editore = resultSet.getString("editore");
                int numero_edizione = resultSet.getInt("numero_edizione");
                int id_volume = resultSet.getInt("id_volume"); // Aggiunto per ottenere l'id_volume
                String stato = resultSet.getString("stato");
                String posizione = resultSet.getString("posizione");
                int anno_pubblicazione_originale = resultSet.getInt("anno_pubblicazione_originale");
                int anno_pubblicazione = resultSet.getInt("anno_pubblicazione");
                String descrizione = resultSet.getString("descrizione");

                // Crea oggetti Opera, Edizione, Volume
                Opera opera = new Opera(id_opera, titolo, autore, genere, anno_pubblicazione_originale, descrizione);
                Edizione edizione = new Edizione(isbn, anno_pubblicazione, editore, numero_edizione, opera, id_edizione);
                Volume volume = new Volume(id_volume, edizione, stato, posizione);

                // Aggiungi il volume al catalogo
                catalogo.aggiungiVolume(volume);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return catalogo;
    }

    public static Catalogo ricercaLibro(String ricerca) {
        Catalogo catalogo = new Catalogo();
        String query = "SELECT DISTINCT ON (edizione.id_edizione) " +
                "opera.id_opera, " +
                "opera.titolo, " +
                "opera.autore, " +
                "opera.genere, " +
                "edizione.id_edizione, " +
                "edizione.isbn, " +
                "edizione.editore, " +
                "edizione.numero_edizione, " +
                "volume.id_volume, " +
                "volume.stato, " +
                "volume.posizione, " +
                "opera.anno_pubblicazione_originale, " +
                "edizione.anno_pubblicazione, " +
                "opera.descrizione " +
                "FROM opera " +
                "JOIN edizione ON opera.id_opera = edizione.id_opera " +
                "JOIN volume ON edizione.id_edizione = volume.id_edizione " +
                "WHERE LOWER(opera.titolo) LIKE ? " +
                "OR LOWER(opera.autore) LIKE ? " +
                "OR LOWER(opera.genere) LIKE ? " +
                "OR LOWER(edizione.editore) LIKE ? " +
                "OR LOWER(edizione.isbn) LIKE ?;";

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            String searchPattern = "%" + ricerca.toLowerCase() + "%"; // Converte la ricerca in minuscolo per rendere la ricerca case-insensitive
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);
            statement.setString(5, searchPattern);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id_opera = resultSet.getInt("id_opera");
                    int id_edizione = resultSet.getInt("id_edizione");
                    int id_volume = resultSet.getInt("id_volume");
                    int numero_edizione = resultSet.getInt("numero_edizione");
                    String descrizione = resultSet.getString("descrizione");
                    String isbn = resultSet.getString("isbn");
                    String titolo = resultSet.getString("titolo");
                    String autore = resultSet.getString("autore");
                    String editore = resultSet.getString("editore");
                    int anno_pubblicazione_originale = resultSet.getInt("anno_pubblicazione_originale");
                    int anno_pubblicazione = resultSet.getInt("anno_pubblicazione");
                    String genere = resultSet.getString("genere");
                    String stato = resultSet.getString("stato");
                    String posizione = resultSet.getString("posizione");

                    Opera opera = new Opera(id_opera, titolo, autore, genere, anno_pubblicazione_originale, descrizione);
                    Edizione edizione = new Edizione(isbn, anno_pubblicazione, editore, numero_edizione, opera, id_edizione);
                    Volume volume = new Volume(id_volume, edizione, stato, posizione);

                    catalogo.aggiungiVolume(volume);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return catalogo;
    }

    public static String ottieniIsbn (String nome, String autore, int num_edizione) {
        String query = "SELECT isbn FROM opera JOIN edizione ON opera.id_opera = edizione.id_opera WHERE opera.titolo = ? AND opera.autore = ? AND numero_edizione = ?;";
        try(Connection connection = DatabaseConnection.getInstance().getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nome);
            statement.setString(2, autore);
            statement.setInt(3, num_edizione);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return resultSet.getString("isbn");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean libroDisponibile(String isbn) {
        String query = "SELECT stato FROM volume WHERE id_edizione = (SELECT id_edizione FROM edizione WHERE isbn = ?);";
        try(Connection connection = DatabaseConnection.getInstance().getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String stato = resultSet.getString("stato");
                return stato.equals("disponibile");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean operaDisponibile(String isbn) {
        String query = "SELECT COUNT(*) FROM volume WHERE id_edizione = (SELECT id_edizione " +
                       "FROM edizione WHERE isbn = ?) AND stato = 'disponibile';";
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int copieDisponibili = resultSet.getInt(1);
                return copieDisponibili > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //aggiornato
    public static boolean copiaDisponibile(String isbn){
        String query = "SELECT COUNT(id_volume) AS copie FROM edizione JOIN volume ON edizione.id_edizione = volume.id_edizione WHERE isbn = ? AND stato = 'disponibile';";
        try(Connection connection = DatabaseConnection.getInstance().getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                int copie = resultSet.getInt("copie");
                return copie > 0;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}
