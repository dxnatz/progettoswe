package com.progettoswe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Inizializza la scena con la schermata di login
        Parent root = loadFXML("login");
        scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Applicazione Biblioteca");
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        if (scene != null) {
            scene.setRoot(loadFXML(fxml));
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}