package com.progettoswe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.progettoswe.App;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Prestito;
import com.progettoswe.model.Session;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import com.progettoswe.ORM.UserDAO;

public class HomePageController {
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
        catalogo = UserDAO.caricaCatalogo();
        aggiornaCatalogo(catalogo);
    }
    
    private void stampaPrestiti() {
        prestiti = UserDAO.caricaPrestiti();
        for (int i = 0; i < prestiti.size(); i++) {
            String titolo = prestiti.get(i).getLibro().getTitolo();
            LocalDate dataFine = prestiti.get(i).getDataInizioPrenotazione().plusDays(30);
            listaPrestiti.getItems().add(titolo + " | Da restituire entro il: " + dataFine);
        }
    }
    
    @FXML
    private void searchBooks() {
        String searchText = ricerca.getText();
        listaCatalogo.getItems().clear();
        catalogo = UserDAO.ricercaLibro(searchText);
        aggiornaCatalogo(catalogo);
    }
    
    @FXML
    private void extendLoan() {
        // TODO: Prolungare il prestito nel database
    }
    
    @FXML
    private void cancelLoan() {
        // TODO: Annullare il prestito nel database
    }

    @FXML
    private void prenotaLibro(){

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

    private void aggiornaCatalogo(Catalogo catalogo){
        listaCatalogo.getItems().clear();
        for (int i = 0; i < catalogo.getLibri().size(); i++) {
            String titolo = catalogo.getLibri().get(i).getTitolo();
            String autore = catalogo.getLibri().get(i).getAutore();
            String disponibile;
            if(catalogo.getLibri().get(i).getCopie() == 0){
                disponibile = "Non disponibile";
            }else{
                disponibile = "Disponibile";
            }
            listaCatalogo.getItems().add(titolo + " - " + autore + " - " + disponibile);
        }
    }
}
