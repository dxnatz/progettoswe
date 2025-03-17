package com.progettoswe.controller;

import java.io.IOException;
import java.util.ArrayList;

import com.progettoswe.App;
import com.progettoswe.business_logic.BookService;
import com.progettoswe.business_logic.LoanService;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Prestito;
import com.progettoswe.model.Session;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class OpUserController {
    @FXML private ListView<String> listaCatalogo;
    @FXML private TextField ricerca;
    @FXML private ListView<String> listaPrestiti;
    Catalogo catalogo = new Catalogo();
    ArrayList<Prestito> prestiti = new ArrayList<Prestito>();

    public void initialize() {
        // Inizializza interfaccia utente
        stampaCatalogo();
        stampaPrestiti();
    }

    private void stampaCatalogo() {
        BookService.stampaCatalogo(catalogo, listaCatalogo);
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
