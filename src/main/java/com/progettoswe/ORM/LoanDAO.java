package com.progettoswe.ORM;

import com.progettoswe.model.*;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class LoanDAO {

    public static ArrayList<Prestito> caricaPrestiti(){
        ArrayList<Prestito> prestiti = new ArrayList<>();
        String query = "SELECT opera.id_opera, titolo, autore, genere, anno_pubblicazione_originale, descrizione, edizione.id_edizione, numero_edizione, editore, anno_pubblicazione, isbn, volume.id_volume, stato, posizione, prestito.id_prestito, data_inizio, restituito, num_rinnovi FROM prestito JOIN Utente ON prestito.id_utente = utente.id_utente JOIN volume ON prestito.id_volume = volume.id_volume JOIN edizione ON edizione.id_edizione = volume.id_edizione JOIN opera ON opera.id_opera = edizione.id_opera WHERE email = ?";

        try(Connection connection = DatabaseConnection.getInstance().getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, Session.getUserEmail()); //imposto il parametro della query, sostituisco il ? con l'email dell'utente dentro la sessione
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                int id_opera = resultSet.getInt("id_opera");
                String titolo = resultSet.getString("titolo");
                String autore = resultSet.getString("autore");
                String genere = resultSet.getString("genere");
                int anno_pubblicazione_originale = resultSet.getInt("anno_pubblicazione_originale");
                String descrizione = resultSet.getString("descrizione");

                Opera opera = new Opera(id_opera, titolo, autore, genere, anno_pubblicazione_originale, descrizione);

                int id_edizione = resultSet.getInt("id_edizione");
                int numero_edizione = resultSet.getInt("numero_edizione");
                String editore = resultSet.getString("editore");
                int anno_pubblicazione = resultSet.getInt("anno_pubblicazione");
                String isbn = resultSet.getString("isbn");

                Edizione edizione = new Edizione(isbn, anno_pubblicazione, editore, numero_edizione, opera, id_edizione);

                int id_volume = resultSet.getInt("id_volume");
                String stato = resultSet.getString("stato");
                String posizione = resultSet.getString("posizione");

                Volume volume = new Volume(id_volume, edizione, stato, posizione);

                int id_prestito = resultSet.getInt("id_prestito");
                LocalDate data_inizio = resultSet.getDate("data_inizio").toLocalDate();
                boolean restituito = resultSet.getBoolean("restituito");
                int num_rinnovi = resultSet.getInt("num_rinnovi");

                Prestito prestito = new Prestito(id_prestito, volume, Session.getUtente(), data_inizio, restituito, num_rinnovi);
                prestiti.add(prestito);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return prestiti;
    }

    public static void stampaTuttiPrestiti(ArrayList<Prestito> prestiti, ListView<String> listaPrestiti) {
        ArrayList<Prestito> loadedPrestiti = LoanDAO.caricaTuttiPrestiti();
        prestiti.clear();
        prestiti.addAll(loadedPrestiti);
        Utente utente = Session.getUtente();
        listaPrestiti.getItems().clear();
        for (Prestito prestito : prestiti) {
            String titolo = prestito.getVolume().getEdizione().getOpera().getTitolo();
            String autore = prestito.getVolume().getEdizione().getOpera().getAutore();
            String editore = prestito.getVolume().getEdizione().getEditore();
            int id_prestito = prestito.getId_prestito();
            int numero_edizione = prestito.getVolume().getEdizione().getNumero();
            LocalDate dataFine;
            String isbn = prestito.getVolume().getEdizione().getIsbn();
            boolean restituito = prestito.getRestituito();
            String r = "da restituire";
            if (restituito) {
                r = "restituito";
            }

            Session.setUtente(prestito.getUtente());
            if (LoanDAO.rinnovi(isbn) == 2) {
                dataFine = prestito.getDataInizio().plusDays(60);
            } else if (LoanDAO.rinnovi(isbn) == 1) {
                dataFine = prestito.getDataInizio().plusDays(45);
            } else {
                dataFine = prestito.getDataInizio().plusDays(30);
            }
            listaPrestiti.getItems().add(id_prestito + " - " + titolo + " - " + numero_edizione + " edizione - " + editore + " - " + autore + " - Data fine prestito: " + dataFine + " - Stato: " + r);
        }
        Session.setUtente(utente);
        System.out.println(prestiti.size()); // Debug statement
    }

    public static ArrayList<Prestito> caricaTuttiPrestiti() {
        ArrayList<Prestito> prestiti = new ArrayList<>();
        String query = "SELECT opera.id_opera, titolo, autore, genere, anno_pubblicazione_originale, descrizione, "
                + "edizione.id_edizione, numero_edizione, editore, anno_pubblicazione, isbn, "
                + "volume.id_volume, stato, posizione, "
                + "prestito.id_prestito, data_inizio, restituito, num_rinnovi, "
                + "utente.id_utente, nome, cognome, cf, email, pw, cellulare, data_nascita, indirizzo "
                + "FROM prestito "
                + "JOIN utente ON prestito.id_utente = utente.id_utente "
                + "JOIN volume ON prestito.id_volume = volume.id_volume "
                + "JOIN edizione ON edizione.id_edizione = volume.id_edizione "
                + "JOIN opera ON opera.id_opera = edizione.id_opera "
                + "WHERE prestito.restituito = false";

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // Creazione oggetto Opera
                Opera opera = new Opera(
                        resultSet.getInt("id_opera"),
                        resultSet.getString("titolo"),
                        resultSet.getString("autore"),
                        resultSet.getString("genere"),
                        resultSet.getInt("anno_pubblicazione_originale"),
                        resultSet.getString("descrizione")
                );

                // Creazione oggetto Edizione
                Edizione edizione = new Edizione(
                        resultSet.getString("isbn"),
                        resultSet.getInt("anno_pubblicazione"),
                        resultSet.getString("editore"),
                        resultSet.getInt("numero_edizione"),
                        opera,
                        resultSet.getInt("id_edizione")
                );

                // Creazione oggetto Volume
                Volume volume = new Volume(
                        resultSet.getInt("id_volume"),
                        edizione,
                        resultSet.getString("stato"),
                        resultSet.getString("posizione")
                );

                // Creazione oggetto Utente
                Utente utente = new Utente(
                        resultSet.getInt("id_utente"),
                        resultSet.getString("nome"),
                        resultSet.getString("cognome"),
                        resultSet.getString("cf"),
                        resultSet.getString("email"),
                        resultSet.getString("cellulare"),
                        resultSet.getDate("data_nascita").toLocalDate(),
                        resultSet.getString("indirizzo")
                );

                // Creazione oggetto Prestito
                Prestito prestito = new Prestito(
                        resultSet.getInt("id_prestito"),
                        volume,
                        utente,
                        resultSet.getDate("data_inizio").toLocalDate(),
                        resultSet.getBoolean("restituito"),
                        resultSet.getInt("num_rinnovi")
                );

                prestiti.add(prestito);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prestiti;
    }

    public static boolean libroGiaPrenotato(String isbn){
        String query = "SELECT * FROM prestito p " +
                "JOIN volume v ON p.id_volume = v.id_volume " +
                "JOIN edizione e ON v.id_edizione = e.id_edizione " +
                "WHERE e.isbn = ? " +
                "AND p.id_utente = ? " +
                "AND p.restituito = false";

        try(Connection connection = DatabaseConnection.getInstance().getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            statement.setInt(2, Session.getUtente().getId_utente());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean prestitoDaMenoDiTreGiorni(String isbn, int num_edizione){
        String query = "SELECT * FROM prestito JOIN volume ON prestito.id_volume = volume.id_volume JOIN edizione ON volume.id_edizione = edizione.id_edizione WHERE isbn = ? AND id_utente = ? AND numero_edizione = ? AND data_inizio >= CURRENT_DATE - INTERVAL '3 days'";

        try(Connection connection = DatabaseConnection.getInstance().getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            statement.setInt(2, Session.getUtente().getId_utente());
            statement.setInt(3, num_edizione);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean annullaPrestito(String isbn){
        String deleteQuery = "DELETE FROM prestito " +
                "USING volume, edizione " +
                "WHERE prestito.id_volume = volume.id_volume " +
                "AND volume.id_edizione = edizione.id_edizione " +
                "AND edizione.isbn = ? " +
                "AND prestito.id_utente = ? " +
                "RETURNING prestito.id_volume";

        String updateQuery = "UPDATE volume " +
                "SET stato = 'disponibile' " +
                "WHERE id_volume = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            // Prima eliminiamo il prestito e recuperiamo l'id del volume
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, isbn);
            deleteStatement.setInt(2, Session.getUtente().getId_utente());

            ResultSet rs = deleteStatement.executeQuery();

            if (rs.next()) {
                int idVolume = rs.getInt("id_volume");

                // Aggiorniamo lo stato del volume
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setInt(1, idVolume);
                int rowsUpdated = updateStatement.executeUpdate();

                return rowsUpdated > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void prolungaPrestito(String isbn){
        String query = "UPDATE prestito " +
                "SET num_rinnovi = num_rinnovi + 1 " +
                "FROM volume, edizione " +
                "WHERE prestito.id_volume = volume.id_volume " +
                "AND volume.id_edizione = edizione.id_edizione " +
                "AND edizione.isbn = ? " +
                "AND prestito.id_utente = ?";

        try(Connection connection = DatabaseConnection.getInstance().getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            statement.setInt(2, Session.getUtente().getId_utente());
            statement.executeUpdate();

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static boolean prenotaLibro(String isbn) {
        //otteniamo l'id_volume disponibile
        String getIdVolumeQuery =   "SELECT id_volume " +
                "FROM volume " +
                "WHERE id_edizione = (SELECT id_edizione FROM edizione WHERE isbn = ?) " +
                "AND stato = 'disponibile' " +
                "ORDER BY id_volume ASC LIMIT 1";  // Ottieni il primo volume disponibile

        String updateQuery = "UPDATE volume " +
                "SET stato = 'in prestito' " +
                "WHERE id_volume = ?";  // Aggiorna lo stato del volume

        String insertPrestitoQuery = "INSERT INTO prestito (id_volume, id_utente) " +
                "VALUES (?, ?)";  // Inserisce il prestito

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            // 1. Ottenere l'id_volume disponibile
            PreparedStatement getIdVolumeStatement = connection.prepareStatement(getIdVolumeQuery);
                    getIdVolumeStatement.setString(1, isbn);  // Imposta l'ISBN

            ResultSet getIdVolumeResultSet = getIdVolumeStatement.executeQuery();
            if (getIdVolumeResultSet.next()) {
                int idVolume = getIdVolumeResultSet.getInt("id_volume");  // Ottieni l'ID del volume disponibili

                // 2. Aggiorna lo stato del volume selezionato come "in prestito"
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setInt(1, idVolume);  // Imposta l'ID del volume

                int rowsUpdated = updateStatement.executeUpdate();  // Esegui l'UPDATE
                if (rowsUpdated > 0) {
                    // Aggiornamento avvenuto correttamente

                    // 3. Inserisci il prestito nel database
                    PreparedStatement insertStatement = connection.prepareStatement(insertPrestitoQuery);
                    insertStatement.setInt(1, idVolume);  // Imposta l'ID del volume selezionato
                    insertStatement.setInt(2, Session.getUtente().getId_utente());  // Imposta l'ID dell'utente

                    int rowsInserted = insertStatement.executeUpdate();
                    if (rowsInserted > 0) {
                        // Se l'inserimento del prestito è andato a buon fine, ritorna true
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
                e.printStackTrace();
                }
        return false;  // Ritorna false in caso di errore
    }

    //Corretto
    public static int rinnovi(String isbn){
        String query = "SELECT num_rinnovi FROM prestito JOIN volume ON prestito.id_volume = volume.id_volume JOIN edizione ON volume.id_edizione = edizione.id_edizione WHERE isbn = ? AND id_utente = ?";
        int num_rinnovi = 0;

        try(Connection connection = DatabaseConnection.getInstance().getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, isbn);
            statement.setInt(2, Session.getUtente().getId_utente());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                num_rinnovi = resultSet.getInt("num_rinnovi");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return num_rinnovi;
    }

    //corretto
    public static boolean prestitiMassimiRaggiunti(){
        String query = "SELECT COUNT(*) FROM prestito WHERE id_utente = ? AND restituito = false;";

        try(Connection connection = DatabaseConnection.getInstance().getConnection()){
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Session.getUtente().getId_utente());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                int num_prestiti = resultSet.getInt(1);
                return num_prestiti >= 3; // Se il numero di prestiti è maggiore o uguale a 3, ritorna true
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean concludiPrestito(int id) {
        String queryUpdatePrestito = "UPDATE prestito SET restituito = true WHERE id_prestito = ?";

        String queryUpdateVolume = " UPDATE volume SET stato = 'disponibile' FROM prestito WHERE prestito.id_prestito = ? AND prestito.id_volume = volume.id_volume";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmtPrestito = conn.prepareStatement(queryUpdatePrestito);
             PreparedStatement stmtVolume = conn.prepareStatement(queryUpdateVolume)) {

            stmtPrestito.setInt(1, id);
            stmtVolume.setInt(1, id);

            int updatedPrestito = stmtPrestito.executeUpdate();
            int updatedVolume = stmtVolume.executeUpdate();

            return updatedPrestito > 0 && updatedVolume > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
