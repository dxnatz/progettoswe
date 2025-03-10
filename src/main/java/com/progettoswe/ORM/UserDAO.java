package com.progettoswe.ORM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.progettoswe.model.Utente;
import java.sql.Date;

public class UserDAO {

    // Metodo per inserire un nuovo utente nel database
    public static boolean inserimentoUtente(Utente utente) {
        String query = "INSERT INTO utente (nome, cognome, cf, email, pw, cellulare, data_nascita, indirizzo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            //imposto i parametri della query
            Date sqlDate = Date.valueOf(utente.getDataNascita());
            pstmt.setString(1, utente.getNome());
            pstmt.setString(2, utente.getCognome());
            pstmt.setString(3, utente.getCodiceFiscale());
            pstmt.setString(4, utente.getEmail());
            pstmt.setString(5, utente.getPassword());
            pstmt.setString(6, utente.getCellulare());
            pstmt.setDate(7, sqlDate);
            pstmt.setString(8, utente.getIndirizzo());

            int rowsInserted = pstmt.executeUpdate(); //eseguo la query
            return rowsInserted > 0; //restituisce true se l'utente è stato registrato con successo

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; //se non è stato possibile registrare l'utente a causa di un errore
    }

    // Metodo per controllare se un utente è già registrato
    public static boolean emailEsistente(String email) {
        String query = "SELECT count (*) FROM utente WHERE email = ?"; //query per contare il numero di email uguali a quella inserita

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email); // imposto il parametro della query, sostituisco il ? con l'email

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; //email trovata
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; //email non trovata
    }

    // Metodo per controllare se un codice fiscale è già registrato
    public static boolean cfEsistente(String cf) {
        String query = "SELECT count (*) FROM utente WHERE cf = ?"; //query per contare il numero di codici fiscali uguali a quello inserito

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, cf); // imposto il parametro della query, sostituisco il ? con il codice fiscale

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; //codice fiscale trovato
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; //codice fiscale non trovato
    }

    // Metodo per controllare se un numero di cellulare è già registrato
    public static boolean cellulareEsistente(String cellulare) {
        String query = "SELECT count (*) FROM utente WHERE cellulare = ?"; //query per contare il numero di numeri di cellulare uguali a quello inserito

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, cellulare); // imposto il parametro della query, sostituisco il ? con il numero di cellulare

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; //numero di cellulare trovato
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; //numero di cellulare non trovato
    }

    public static boolean login(String email, String password) {
        String query = "SELECT count(*) FROM utente WHERE email = ? AND pw = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try(ResultSet rs = preparedStatement.executeQuery()) {

                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
                throw new SQLException();
            } 
            catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Metodo per controllare se l'email è corretta e la password è errata
    public static boolean emailCorrettaPasswordErrata(String email, String password) {

        String query = "SELECT count(*) FROM utente WHERE email = ? AND pw != ?";

        try (Connection connection = DatabaseConnection.getConnection()){
            
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try(ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false; 
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /*public static Catalogo caricaCatalogo() {
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
    }*/

    // Metodo per restituire un utente dal database
    public static Utente utente(String email) {
        String query = "SELECT * FROM utente WHERE email = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int codice = resultSet.getInt("codice");
                String nome = resultSet.getString("nome");
                String cognome = resultSet.getString("cognome");
                String codiceFiscale = resultSet.getString("cf");
                String cellulare = resultSet.getString("cellulare");
                Date dataNascita = resultSet.getDate("data_nascita");
                String indirizzo = resultSet.getString("indirizzo");

                return new Utente(codice, nome, cognome, codiceFiscale, email, "", cellulare, dataNascita.toLocalDate(), indirizzo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Metodo per rstituire i prestiti di un utente dal database in base all'email dentro la sessione
    /*public static ArrayList<Prestito> caricaPrestiti(){
        ArrayList<Prestito> prestiti = new ArrayList<>();
        String query = "SELECT * FROM prestito JOIN libro ON prestito.isbn_libro = libro.isbn JOIN utente ON utente.codice = prestito.codice_utente WHERE utente.email = ?";

        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, Session.getUserEmail()); //imposto il parametro della query, sostituisco il ? con l'email dell'utente dentro la sessione
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                int codice = resultSet.getInt("codice_utente");
                String isbn = resultSet.getString("isbn");
                String titolo = resultSet.getString("titolo");
                String autore = resultSet.getString("autore");
                String editore = resultSet.getString("editore");
                int anno_pubblicazione = resultSet.getInt("anno_pubblicazione");
                String genere = resultSet.getString("genere");
                int copie = resultSet.getInt("copie");
                Libro libro = new Libro(isbn, titolo, autore, editore, anno_pubblicazione, genere, copie);
                LocalDate dataPrenotazione = resultSet.getDate("data_inizio").toLocalDate();
                Prestito prestito = new Prestito(codice, Session.getUtente(), libro, dataPrenotazione);
                prestiti.add(prestito);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return prestiti;
    }*/

    /*public static Catalogo ricercaLibro(String ricerca){
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
    }*/

    /*public static boolean prenotaLibro(Libro libro){
        String query = "INSERT INTO prestito (codice_utente, isbn_libro) VALUES (?, ?, ?)";

        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Session.getUtente().getCodice());
            statement.setString(2, libro.getIsbn());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }*/
}