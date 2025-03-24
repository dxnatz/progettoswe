package com.progettoswe.ORM;

import com.progettoswe.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookDAO {

    //aggiornato
    public static Catalogo caricaCatalogo() {
        Catalogo catalogo = new Catalogo(); //creo un nuovo catalogo per inserire i libri
        String query = "SELECT * FROM opera JOIN edizione ON opera.id_opera = edizione.id_opera JOIN volume ON edizione.id_edizione = volume.id_edizione;";

        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            
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

                //da completare per bene

                Opera opera = new Opera(id_opera, titolo, autore, genere, anno_pubblicazione_originale, descrizione);
                Edizione edizione = new Edizione(isbn, anno_pubblicazione, editore, numero_edizione, opera, id_edizione);
                Volume volume = new Volume(id_volume, edizione, stato, posizione);

                catalogo.aggiungiVolume(volume);

                //Libro libro = new Libro(isbn, titolo, autore, editore, anno_pubblicazione, genere);
                //catalogo.aggiungiLibro(libro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return catalogo;
    }

    //aggiornato
    public static Catalogo ricercaLibro(String ricerca){
        Catalogo catalogo = new Catalogo(); //creo un nuovo catalogo per inserire i libri che corrispondono alla ricerca
        String query =  "SELECT *"+
                        "FROM opera JOIN edizione ON opera.id_opera = edizione.id_opera JOIN volume ON edizione.id_edizione = volume.id_edizione" +
                        "WHERE LOWER(opera.titolo) LIKE ? OR LOWER(opera.autore) LIKE ? OR LOWER(opera.genere) LIKE ? " +
                        "OR LOWER(edizione.editore) LIKE ? OR LOWER(edizione.isbn) LIKE ?;";

        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            String searchPattern = "%" + ricerca + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);
            statement.setString(5, searchPattern);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
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
        }catch(SQLException e){
            e.printStackTrace();
        }
        return catalogo;
    }

    public static String ottieniIsbn (String nome, String autore) {
        String query = "SELECT isbn FROM libro WHERE titolo = ? AND autore = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nome);
            statement.setString(2, autore);
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
        String query = "SELECT copie FROM libro WHERE isbn = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
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

    public static void decrementaCopieLibro(String isbn){
        String query = "UPDATE libro SET copie = copie - 1 WHERE isbn = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            statement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void aumentaCopieLibro(String isbn){
        String query = "UPDATE libro SET copie = copie + 1 WHERE isbn = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            statement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static boolean copiaDisponibile(String isbn){
        String query = "SELECT copie FROM libro WHERE isbn = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
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

    public static boolean aggiungiLibro(Libro l){
        String query = "INSERT INTO libro (isbn, titolo, autore, editore, anno_pubblicazione, genere, copie) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); // Ottieni una connessione al database
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Imposta i parametri della query con i valori del libro
            pstmt.setString(1, l.getIsbn());
            pstmt.setString(2, l.getTitolo());
            pstmt.setString(3, l.getAutore());
            pstmt.setString(4, l.getEditore());
            pstmt.setInt(5, l.getAnnoPubblicazione());
            pstmt.setString(6, l.getGenere());
            pstmt.setInt(7, l.getCopie());

            // Esegui la query
            int rowsAffected = pstmt.executeUpdate();

            // Restituisci true se l'inserimento è avvenuto con successo
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Gestisci eventuali errori
            e.printStackTrace();
            return false;
        }
    }

    public static Volume getVolume(String isbn) {
        String query = "SELECT * FROM volume JOIN edizione ON volume.id_edizione = edizione.id_edizione JOIN opera ON edizione.id_opera = opera.id_opera WHERE isbn = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, isbn);
            ResultSet resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                int id_opera = resultSet.getInt("id_opera");
                int id_edizione = resultSet.getInt("id_edizione");
                int id_volume = resultSet.getInt("id_volume");
                int numero_edizione = resultSet.getInt("numero_edizione");
                String descrizione = resultSet.getString("descrizione");
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

                return volume;
            } else {
                return null;
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Restituisci null in caso di errore
        }
    }

    public static boolean aggiornaLibro(Libro l) {
        String query = "UPDATE libro SET titolo = ?, autore = ?, editore = ?, anno_pubblicazione = ?, genere = ?, copie = ? WHERE isbn = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, l.getTitolo());
            statement.setString(2, l.getAutore());
            statement.setString(3, l.getEditore());
            statement.setInt(4, l.getAnnoPubblicazione());
            statement.setString(5, l.getGenere());
            statement.setInt(6, l.getCopie());
            statement.setString(7, l.getIsbn());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean cancellaVolume(String isbn){

        String query = "DELETE FROM volume WHERE id_edizione = (SELECT id_edizione FROM edizione WHERE isbn = ?)";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1, isbn);
            return statement.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

}
