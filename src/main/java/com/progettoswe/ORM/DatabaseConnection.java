package com.progettoswe.ORM;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/ProvaSWE";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    // Costruttore privato per impedire l'istanziazione
    private DatabaseConnection() {
        // Inizializzazione lasciata vuota
    }

    // Metodo pubblico per ottenere l'istanza singleton
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            } catch (SQLException e) {
                throw new SQLException("Errore durante la connessione al database.", e);
            }
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}