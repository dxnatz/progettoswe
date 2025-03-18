package com.progettoswe.ORM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import com.progettoswe.model.Libro;
import com.progettoswe.model.Prestito;
import com.progettoswe.model.Session;
import com.progettoswe.model.Utente;

public class LoanDAO {
    
    public static ArrayList<Prestito> caricaTuttiPrestiti(){
        ArrayList<Prestito> prestiti = new ArrayList<>();
        String query = "SELECT * FROM prestito JOIN libro ON prestito.isbn_libro = libro.isbn JOIN utente ON utente.codice = prestito.codice_utente WHERE restituito = false";

        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                //prestito
                int codice = resultSet.getInt("codice");
                int codiceUtente = resultSet.getInt("codice_utente");
                String isbnLibro = resultSet.getString("isbn_libro");
                LocalDate dataInizio = resultSet.getDate("data_inizio").toLocalDate();
                int numRinnovi = resultSet.getInt("num_rinnovi");
                boolean restituito = resultSet.getBoolean("restituito");

                //libro del prestito
                String titolo = (resultSet.getString("titolo"));
                String autore = (resultSet.getString("autore"));
                String editore = (resultSet.getString("editore"));
                int anno = (resultSet.getInt("anno_pubblicazione"));
                String genere = (resultSet.getString("genere"));
                int copie = (resultSet.getInt("copie"));

                //utente del prestito
                String nome = resultSet.getString("nome");
                String cognome = resultSet.getString("cognome");
                String cf = resultSet.getString("cf");
                String email = resultSet.getString("email");
                String pw = resultSet.getString("pw");
                String cellulare = resultSet.getString("cellulare");
                LocalDate dataNascita = resultSet.getDate("data_nascita").toLocalDate();
                String indirizzo = resultSet.getString("indirizzo");
                LocalDate dataRegistrazione = resultSet.getDate("data_registrazione").toLocalDate();

                Utente utente = new Utente(codiceUtente, nome, cognome, cf, email, pw, cellulare, dataNascita, indirizzo, dataRegistrazione);
                Libro libro = new Libro(isbnLibro, titolo, autore, editore, anno, genere, copie);

                Prestito prestito = new Prestito(codice, utente, libro, dataInizio, numRinnovi, restituito);
                prestiti.add(prestito);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prestiti;
    }

    public static ArrayList<Prestito> caricaPrestiti(){
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
    }

    public static boolean prenotaLibro(String isbn){
        String query = "INSERT INTO prestito (codice_utente, isbn_libro) VALUES (?, ?)";

        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Session.getUtente().getCodice());
            statement.setString(2, isbn);

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean libroGiaPrenotato(String isbn){
        String query = "SELECT * FROM prestito WHERE isbn_libro = ? AND codice_utente = ?";

        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            statement.setInt(2, Session.getUtente().getCodice());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean prestitoDaMenoDiTreGiorni(String isbn){
        String query = "SELECT * FROM prestito WHERE isbn_libro = ? AND codice_utente = ? AND data_inizio >= CURRENT_DATE - INTERVAL '3 days'";

        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            statement.setInt(2, Session.getUtente().getCodice());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean annullaPrestito(String isbn){
        String query = "DELETE FROM prestito WHERE isbn_libro = ? AND codice_utente = ?";

        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            statement.setInt(2, Session.getUtente().getCodice());

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static void prolungaPrestito(String isbn){
        String query = "UPDATE prestito SET num_rinnovi = num_rinnovi + 1 WHERE isbn_libro = ? AND codice_utente = ?";

        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            statement.setInt(2, Session.getUtente().getCodice());
            statement.executeUpdate();

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static int rinnoviEsauriti(String isbn){
        String query = "SELECT num_rinnovi FROM prestito WHERE isbn_libro = ? AND codice_utente = ?";
        int num_rinnovi = 0;

        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            statement.setInt(2, Session.getUtente().getCodice());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                num_rinnovi = resultSet.getInt("num_rinnovi");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return num_rinnovi;
    }

    public static boolean prestitiMassimiRaggiunti(){
        String query = "SELECT COUNT(*) FROM prestito WHERE codice_utente = ?";

        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Session.getUtente().getCodice());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                int num_prestiti = resultSet.getInt(1);
                return num_prestiti >= 3;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean returnBook(int codicePrestito){
        String sql = "UPDATE prestito SET restituito = true WHERE codice = ?";

        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {
            statement.setInt(1, codicePrestito); // Imposta il codice del prestito come parametro

            int rowsUpdated = statement.executeUpdate(); // Esegue l'operazione di UPDATE

            // Se almeno una riga è stata aggiornata, restituisce true
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // In caso di errore, restituisce false
        }
    }

    public static ArrayList<Prestito> ricercaPrestiti(String ricerca) {
        ArrayList<Prestito> prestitiTrovati = new ArrayList<>(); // Lista per i prestiti trovati
        String query = "SELECT * " +
                       "FROM prestito " +
                       "JOIN libro ON prestito.isbn_libro = libro.isbn " +
                       "JOIN utente ON utente.codice = prestito.codice_utente " +
                       "WHERE " +
                       "    LOWER(prestito.codice::text) LIKE LOWER(?) OR " +
                       "    LOWER(prestito.codice_utente::text) LIKE LOWER(?) OR " +
                       "    LOWER(prestito.isbn_libro) LIKE LOWER(?) OR " +
                       "    LOWER(libro.titolo) LIKE LOWER(?) OR " +
                       "    LOWER(libro.autore) LIKE LOWER(?)";
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            String searchPattern = "%" + ricerca + "%"; // Aggiunge i wildcard per la ricerca parziale
            statement.setString(1, searchPattern); // Ricerca su codice prestito
            statement.setString(2, searchPattern); // Ricerca su codice utente
            statement.setString(3, searchPattern); // Ricerca su ISBN libro
            statement.setString(4, searchPattern); // Ricerca su titolo libro
            statement.setString(5, searchPattern); // Ricerca su autore libro
    
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                // Prestito
                int codice = resultSet.getInt("codice");
                int codiceUtente = resultSet.getInt("codice_utente");
                String isbnLibro = resultSet.getString("isbn_libro");
                LocalDate dataInizio = resultSet.getDate("data_inizio").toLocalDate();
                int numRinnovi = resultSet.getInt("num_rinnovi");
                boolean restituito = resultSet.getBoolean("restituito");
    
                // Libro del prestito
                String titolo = resultSet.getString("titolo");
                String autore = resultSet.getString("autore");
                String editore = resultSet.getString("editore");
                int anno = resultSet.getInt("anno_pubblicazione");
                String genere = resultSet.getString("genere");
                int copie = resultSet.getInt("copie");
    
                // Utente del prestito
                String nome = resultSet.getString("nome");
                String cognome = resultSet.getString("cognome");
                String cf = resultSet.getString("cf");
                String email = resultSet.getString("email");
                String pw = resultSet.getString("pw");
                String cellulare = resultSet.getString("cellulare");
                LocalDate dataNascita = resultSet.getDate("data_nascita").toLocalDate();
                String indirizzo = resultSet.getString("indirizzo");
                LocalDate dataRegistrazione = resultSet.getDate("data_registrazione").toLocalDate();
    
                // Creazione degli oggetti
                Utente utente = new Utente(codiceUtente, nome, cognome, cf, email, pw, cellulare, dataNascita, indirizzo, dataRegistrazione);
                Libro libro = new Libro(isbnLibro, titolo, autore, editore, anno, genere, copie);
    
                // Creazione del prestito
                Prestito prestito = new Prestito(codice, utente, libro, dataInizio, numRinnovi, restituito);
                prestitiTrovati.add(prestito); // Aggiunge il prestito alla lista
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prestitiTrovati; // Restituisce la lista dei prestiti trovati
    }
}
