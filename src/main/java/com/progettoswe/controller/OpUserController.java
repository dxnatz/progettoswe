package com.progettoswe.controller;

import com.progettoswe.App;
import com.progettoswe.business_logic.BookService;
import com.progettoswe.business_logic.LoanService;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Prestito;
import com.progettoswe.utilities.InputValidator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;

public class OpUserController {

    // Elementi della top bar
    @FXML private TextField ricerca;
    @FXML private TextField ricercaPrestito;

    // Elementi del catalogo libri
    @FXML private ListView<String> listaCatalogo;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    Catalogo catalogo = new Catalogo();

    // Elementi dei prestiti attivi
    @FXML private ListView<String> listaPrestiti;
    @FXML private Button returnBookButton;
    ArrayList<Prestito> prestiti = new ArrayList<>();

    String lastSelectedBook = null;
    String lastSelectedLoan = null;

    // Metodi per le azioni dei bottoni

    @FXML
    private void searchBooks() {
        String searchText = ricerca.getText();
        if(searchText.isEmpty()){
            BookService.stampaCatalogoBibliotecario(catalogo, listaCatalogo);
        } else {
            BookService.searchBooksBibliotecario(catalogo, listaCatalogo, ricerca);
        }
    }

    @FXML
    private void searchLoans() {
        String searchText = ricercaPrestito.getText();
        if(searchText.isEmpty()) {
            LoanService.stampaTuttiPrestiti(prestiti, listaPrestiti);
        }else{
            LoanService.searchLoansBibliotecario(prestiti, listaPrestiti, searchText);
        }
    }

    @FXML
    private void logout() throws IOException {
        App.setRoot("login");
    }

    @FXML
    private void openAddBookWindow() {
        // Implementa l'apertura della finestra per aggiungere un libro
        // TODO: Creare e mostrare una nuova finestra/dialog
    }

    @FXML
    private void openUpdateBookWindow() {
        // Implementa l'apertura della finestra per modificare un libro
        // TODO: Verificare che un libro sia selezionato e aprire la finestra di modifica
    }

    @FXML
    private void openDeleteAlert() {
        // Implementa l'apertura dell'alert per confermare la cancellazione
        // TODO: Verificare che un libro sia selezionato e mostrare un alert di conferma
    }

    @FXML
    private void openReturnBookAlert() {
        // Implementa l'apertura dell'alert per confermare la restituzione
        // TODO: Verificare che un prestito sia selezionato e mostrare un alert di conferma
    }

    // Metodi di inizializzazione e utility
    private void stampaCatalogo(){
        BookService.stampaCatalogoBibliotecario(catalogo, listaCatalogo);
    }

    private void stampaPrestiti(){
        LoanService.stampaTuttiPrestiti(prestiti, listaPrestiti);
    }

    private void listenerCatalogo(){
        listaCatalogo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean itemSelected = newVal != null;
            updateButton.setDisable(!itemSelected);
            deleteButton.setDisable(!itemSelected);

            if(newVal != null){
                lastSelectedBook = newVal.split(" - ")[0]; // Assuming the first part is the ISBN
            }
        });
    }

    private void listenerPrestiti(){
        listaPrestiti.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean itemSelected = newVal != null && InputValidator.isNumber(newVal.split(" - ")[0]);
            returnBookButton.setDisable(!itemSelected);
        });
    }

    @FXML
    private void initialize() {
        // Abilita/disabilita i bottoni in base alla selezione
        listenerCatalogo();
        listenerPrestiti();
        stampaCatalogo();
        stampaPrestiti();
    }
}