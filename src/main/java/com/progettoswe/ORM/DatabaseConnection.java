package com.progettoswe.ORM;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static Connection connection;

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/ProvaSWE";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    // Metodo per ottenere la connessione (Singleton)
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            } catch (SQLException e) {
                throw new SQLException("Errore durante la connessione al database.", e);
            }
        }
        return connection;
    }

    // Metodo per chiudere la connessione (se necessario)
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
