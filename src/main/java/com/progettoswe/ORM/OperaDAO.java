package com.progettoswe.ORM;

import com.progettoswe.model.Opera;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OperaDAO {
    private static final String INSERT_OPERA =
            "INSERT INTO Opera(titolo, autore, genere, anno_pubblicazione_originale, descrizione) " +
                    "VALUES(?, ?, ?, ?, ?) RETURNING id_opera";

    private static final String GET_OPERA_BY_ID =
            "SELECT * FROM Opera WHERE id_opera = ?";

    private static final String GET_ALL_OPERE =
            "SELECT * FROM Opera ORDER BY titolo, autore";

    private static final String UPDATE_OPERA =
            "UPDATE Opera SET titolo=?, autore=?, genere=?, anno_pubblicazione_originale=?, descrizione=? " +
                    "WHERE id_opera=?";

    private static final String DELETE_OPERA =
            "DELETE FROM Opera WHERE id_opera=?";

private static final String SEARCH_OPERE =
    "SELECT * FROM Opera WHERE LOWER(titolo) LIKE ? " +
    "OR LOWER(autore) LIKE ? " +
    "OR LOWER(genere) LIKE ? " +
    "OR CAST(id_opera AS TEXT) LIKE ?";

    // Inserisce una nuova opera e restituisce l'ID generato
    public static int insertOpera(Opera opera) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_OPERA)) {

            stmt.setString(1, opera.getTitolo());
            stmt.setString(2, opera.getAutore());
            stmt.setString(3, opera.getGenere());
            stmt.setInt(4, opera.getAnnoPubblicazioneOriginale());
            stmt.setString(5, opera.getDescrizione());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Inserimento opera fallito, nessun ID ottenuto");
        }
    }

    // Recupera un'opera per ID
    public static Opera getOperaById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_OPERA_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOpera(rs);
            }
            return null;
        }
    }

    // Recupera tutte le opere
    public static List<Opera> getAllOpere() throws SQLException {
        List<Opera> opere = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL_OPERE)) {

            while (rs.next()) {
                opere.add(mapResultSetToOpera(rs));
            }
        }
        return opere;
    }

    // Aggiorna un'opera esistente
    public static boolean updateOpera(Opera opera) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_OPERA)) {

            stmt.setString(1, opera.getTitolo());
            stmt.setString(2, opera.getAutore());
            stmt.setString(3, opera.getGenere());
            stmt.setInt(4, opera.getAnnoPubblicazioneOriginale());
            stmt.setString(5, opera.getDescrizione());
            stmt.setInt(6, opera.getId_opera());

            return stmt.executeUpdate() > 0;
        }
    }

    // Elimina un'opera
    public static boolean deleteOpera(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_OPERA)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Cerca opere per titolo, autore o genere
    public static List<Opera> searchOpere(String searchTerm) throws SQLException {
        List<Opera> opere = new ArrayList<>();
        String term = "%" + searchTerm.toLowerCase() + "%";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_OPERE)) {

            stmt.setString(1, term);
            stmt.setString(2, term);
            stmt.setString(3, term);
            stmt.setString(4, term);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                opere.add(mapResultSetToOpera(rs));
            }
        }
        return opere;
    }

    // Metodo helper per mappare un ResultSet a un oggetto Opera
    private static Opera mapResultSetToOpera(ResultSet rs) throws SQLException {
        return new Opera(
                rs.getInt("id_opera"),
                rs.getString("titolo"),
                rs.getString("autore"),
                rs.getString("genere"),
                rs.getInt("anno_pubblicazione_originale"),
                rs.getString("descrizione")
        );
    }
}