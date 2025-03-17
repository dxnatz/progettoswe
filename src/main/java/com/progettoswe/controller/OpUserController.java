package com.progettoswe.controller;

import java.io.IOException;
import java.util.ArrayList;

import com.progettoswe.App;
import com.progettoswe.ORM.BookDAO;
import com.progettoswe.business_logic.BookService;
import com.progettoswe.business_logic.LoanService;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Libro;
import com.progettoswe.model.Prestito;
import com.progettoswe.model.Session;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OpUserController {
    @FXML private ListView<String> listaCatalogo;
    @FXML private TextField ricerca;
    @FXML private ListView<String> listaPrestiti;
    Catalogo catalogo = new Catalogo();
    ArrayList<Prestito> prestiti = new ArrayList<Prestito>();
    private String lastSelected;

    @FXML 
    private Button updateButton;

    public void initialize() {
        // Inizializza interfaccia utente
        stampaCatalogo();
        stampaPrestiti();
        listenerCatalogo();
    }

    private void listenerCatalogo(){
        listaCatalogo.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                // Chiama il metodo che vuoi eseguire quando un elemento viene selezionato
                libroSelezionato(newValue);
            });
    }

    private void libroSelezionato(String s){
        updateButton.setDisable(false);
        lastSelected = s.split(" ")[0]; 
    }

    private void stampaCatalogo() {
        BookService.stampaCatalogoISBN(catalogo, listaCatalogo);
    }
    
    private void stampaPrestiti() {
        LoanService.stampaPrestiti(prestiti, listaPrestiti);
    }

    @FXML
    private void searchBooks() {
        BookService.searchBooks(catalogo, listaCatalogo, ricerca);
    }

    @FXML
    private void openAddBookWindow() throws IOException{
        App.setRoot("add_book");
    }

    @FXML
    private void openUpdateBookWindow() throws IOException{
        UpdateBookController.isbnLibro = lastSelected;
        App.setRoot("update_book");

    }

    @FXML
    private void returnBook(){
        
    }

    @FXML
    private void logout() {
        try {
            Session.setUserEmail(null);
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
