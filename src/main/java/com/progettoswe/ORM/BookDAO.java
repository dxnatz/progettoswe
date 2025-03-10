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

}
