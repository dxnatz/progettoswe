package com.progettoswe.ORM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Libro;

public class BookDAO {
    
    public static Catalogo caricaCatalogo() {
        Catalogo catalogo = new Catalogo(); //creo un nuovo catalogo per inserire i libri
        String query = "SELECT * FROM libro";

        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                String titolo = resultSet.getString("titolo");
                String autore = resultSet.getString("autore");
                String editore = resultSet.getString("editore");
                int anno_pubblicazione = resultSet.getInt("anno_pubblicazione");
                String genere = resultSet.getString("genere");
                int copie = resultSet.getInt("copie");

                Libro libro = new Libro(isbn, titolo, autore, editore, anno_pubblicazione, genere, copie);
                catalogo.aggiungiLibro(libro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return catalogo;
    }

    public static Catalogo ricercaLibro(String ricerca){
        Catalogo catalogo = new Catalogo(); //creo un nuovo catalogo per inserire i libri che corrispondono alla ricerca
        String query = "SELECT * FROM libro WHERE LOWER(titolo) LIKE LOWER(?) OR LOWER(autore) LIKE LOWER(?) OR LOWER(editore) LIKE LOWER(?) OR LOWER(genere) LIKE LOWER(?)";

        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            String searchPattern = "%" + ricerca + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                String isbn = resultSet.getString("isbn");
                String titolo = resultSet.getString("titolo");
                String autore = resultSet.getString("autore");
                String editore = resultSet.getString("editore");
                int anno_pubblicazione = resultSet.getInt("anno_pubblicazione");
                String genere = resultSet.getString("genere");
                int copie = resultSet.getInt("copie");

                Libro libro = new Libro(isbn, titolo, autore, editore, anno_pubblicazione, genere, copie);
                catalogo.aggiungiLibro(libro);
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

}
