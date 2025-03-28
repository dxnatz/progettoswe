package com.progettoswe.controller;

import com.progettoswe.App;
import com.progettoswe.business_logic.BookService;
import com.progettoswe.business_logic.LoanService;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Prestito;
import com.progettoswe.utilities.AlertUtil;
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

    int lastSelectedBook = -1;
    int lastSelectedEdition = -1;
    int lastSelectedLoanId = -1;
    boolean itemSelected = false;

    // Metodi per le azioni dei bottoni

    @FXML
    private void searchBooks() {
        String searchText = ricerca.getText().trim();
        if(searchText.isEmpty()) {
            BookService.stampaCatalogoBibliotecario(catalogo, listaCatalogo);
        } else {
            BookService.searchBooksBibliotecario(catalogo, listaCatalogo, ricerca);
        }
    }

    //TODO: Fixare che qua non funziona niente
    @FXML
    private void searchLoans() {
        String searchText = ricercaPrestito.getText().trim();
        if(searchText.isEmpty()) {
            LoanService.stampaTuttiPrestiti(prestiti, listaPrestiti);
        } else {
            LoanService.searchLoansBibliotecario(prestiti, listaPrestiti, searchText);
        }
    }

    @FXML
    private void logout() throws IOException {
        App.setRoot("login");
    }

    @FXML
    private void openUpdateBookWindow() {
        // Implementa l'apertura della finestra per modificare un libro
        // TODO: Verificare che un libro sia selezionato e aprire la finestra di modifica
    }

    @FXML
    private void openAddBookAlert() throws IOException {
        String result = null;
        if(itemSelected){
            result = AlertUtil.showCustomButtonAlert("Aggiungi libro",
                    "Scegli se vuoi aggiungere una nuova opera, edizione o un nuovo volume",
                    "", "Nuova opera", "Nuova edizione", "Nuovo volume");
        }else{
            result = AlertUtil.showCustomButtonAlert("Aggiungi libro",
                    "Scegli se vuoi aggiungere una nuova opera o edizione",
                    "", "Nuova opera", "Nuova edizione");
        }
        if(result == null){
            return;
        }
        setAddBookWindowType(result);
        openAddBookWindow();
    }

    private void openAddBookWindow() throws IOException {
        App.setRoot("add_book");
    }

    private void setAddBookWindowType(String type) {
        if (type == null) {
            return;
        }
        if (type.equals("Nuova opera")) {
            AddBookController.typeToAdd = AddBookController.ADD_OPERA;
        } else if (type.equals("Nuova edizione")) {
            AddBookController.typeToAdd = AddBookController.ADD_EDIZIONE;
        } else if (type.equals("Nuovo volume")) {
            AddBookController.typeToAdd = AddBookController.ADD_VOLUME;
        }
        if (itemSelected) {
            AddBookController.codiceOpera = lastSelectedBook;
            AddBookController.idEdizione = lastSelectedEdition;
        }
    }

    @FXML
    private void openUpdateBookAlert() {
        // Implementa l'apertura dell'alert per confermare la cancellazione
        // TODO: Verificare che un libro sia selezionato e mostrare un alert di conferma
    }

    @FXML
    private void openDeleteAlert() {
        // Implementa l'apertura dell'alert per confermare la cancellazione
        // TODO: Verificare che un libro sia selezionato e mostrare un alert di conferma
    }

    @FXML
    private void openReturnBookAlert() {
        if (lastSelectedLoanId != -1) {
            boolean confirmed = AlertUtil.showConfirmationAlert(
                    "Concludi prestito",
                    "Sei sicuro di voler concludere questo prestito?",
                    "ID Prestito: " + lastSelectedLoanId
            );

            if (confirmed) {
                boolean success = LoanService.concludiPrestito(lastSelectedLoanId);
                if (success) {
                    AlertUtil.showInfoAlert("Successo", "Prestito concluso con successo", null);
                    refreshPrestiti();
                } else {
                    AlertUtil.showErrorAlert("Errore", "Impossibile concludere il prestito", null);
                }
            }
        }
    }

    @FXML
    private void openViewAlert() throws IOException {
        String result = AlertUtil.showCustomButtonAlert("Visualizza Catalogo",
                "Scegli un'opzione da visualizzare", null,
                "Opere", "Edizioni", "Volumi");
        if(result == null){
            return;
        }
        if(result.equals("Opere")){
            ViewCatalogueController.whatToView = ViewCatalogueController.SHOW_OPERE;
        } else if (result.equals("Edizioni")) {
            ViewCatalogueController.whatToView = ViewCatalogueController.SHOW_EDIZIONI;
        } else if (result.equals("Volumi")) {
            ViewCatalogueController.whatToView = ViewCatalogueController.SHOW_VOLUMI;
        }
        App.setRoot("view_catalogue");
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
            itemSelected = newVal != null;
            updateButton.setDisable(!itemSelected);
            deleteButton.setDisable(!itemSelected);

            if(newVal != null){
                var s = newVal.split(" - ");
                if(InputValidator.isNumber(s[0])){
                    lastSelectedBook = Integer.parseInt(s[0]); // Assuming the first part is the ISBN
                }
                if(InputValidator.isNumber(s[2])){
                    lastSelectedEdition = Integer.parseInt(s[2]); // Assuming the third part is the edition number
                }else{
                    System.err.println("Error: Edition number is not a number");
                }
            }
        });
    }

    private void listenerPrestiti(){
        listaPrestiti.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean itemSelected = newVal != null && InputValidator.isNumber(newVal.split(" - ")[0]);
            returnBookButton.setDisable(!itemSelected);
        });
    }

    private void refreshPrestiti() {
        prestiti.clear();
        LoanService.stampaTuttiPrestiti(prestiti, listaPrestiti);
    }

    private void setupListeners() {
        // Listener per la selezione nella lista dei prestiti
        listaPrestiti.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Estrai l'ID prestito dalla stringa selezionata
                String[] parts = newVal.split(" \\| ");
                if (parts.length > 0) {
                    String idPart = parts[0]; // "ID Prestito: 123"
                    lastSelectedLoanId = Integer.parseInt(idPart.replace("ID Prestito: ", "").trim());
                }
                returnBookButton.setDisable(false);
            } else {
                returnBookButton.setDisable(true);
                lastSelectedLoanId = -1;
            }
        });
    }

    @FXML
    private void initialize() {
        setupListeners();
        BookService.stampaCatalogoBibliotecario(catalogo, listaCatalogo);
        LoanService.stampaTuttiPrestiti(prestiti, listaPrestiti);

        // Disabilita i bottoni inizialmente
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        returnBookButton.setDisable(true);
    }
}