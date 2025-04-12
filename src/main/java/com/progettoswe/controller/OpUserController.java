package com.progettoswe.controller;

import com.progettoswe.App;
import com.progettoswe.business_logic.BookService;
import com.progettoswe.business_logic.LoanService;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Prestito;
import com.progettoswe.utilities.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;

public class OpUserController {

    // Elementi UI
    @FXML private TextField ricerca;
    @FXML private TextField ricercaPrestito;
    @FXML private ListView<String> listaCatalogo;
    @FXML private ListView<String> listaPrestiti;
    @FXML private Button returnBookButton;

    // Modelli dati
    private final Catalogo catalogo = new Catalogo();
    private final ArrayList<Prestito> prestiti = new ArrayList<>();

    private boolean useServerSideSearch = false;

    @FXML
    private void toggleSearchMode() {
        useServerSideSearch = !useServerSideSearch;
        String mode = useServerSideSearch ? "Server-side" : "Client-side";
        AlertUtil.showInfoAlert("Modalità ricerca",
                "Modalità ricerca cambiata a: " + mode,
                "Ora le ricerche verranno effettuate " +
                        (useServerSideSearch ? "sul server" : "localmente"));
    }

    @FXML
    private void searchBooks() {
        String searchText = ricerca.getText().trim();
        if(searchText.isEmpty()) {
            BookService.stampaCatalogoBibliotecario(catalogo, listaCatalogo);
        } else {
            if(useServerSideSearch) {
                BookService.searchBooksBibliotecario(catalogo, listaCatalogo, ricerca);
            } else {
                filterCatalogueList(searchText);
            }
        }
    }

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
    private void openUpdateBookWindow() throws IOException {
        String selectedItem = listaCatalogo.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertUtil.showErrorAlert("Errore", "Nessun elemento selezionato", "Seleziona un elemento da modificare");
            return;
        }
        App.setRoot("update_book");
    }

    @FXML
    private void addBookHandler() throws IOException {
        AddBookController.typeToAdd = AddBookController.ADD_OPERA;
        openAddBookWindow();
    }

    private void openAddBookWindow() throws IOException {
        App.setRoot("add_book");
        AddBookController.setReturnWindow("op_user");
    }

    @FXML
    private void openReturnBookAlert() {
        String selectedItem = listaPrestiti.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int loanId = extractFirstNumber(selectedItem);

            boolean confirmed = AlertUtil.showConfirmationAlert(
                    "Concludi prestito",
                    "Sei sicuro di voler concludere questo prestito?",
                    "ID Prestito: " + loanId
            );

            if (confirmed && LoanService.concludiPrestito(loanId)) {
                AlertUtil.showInfoAlert("Successo", "Prestito concluso con successo", null);
                refreshPrestiti();
                BookService.stampaCatalogoBibliotecario(catalogo, listaCatalogo);
            } else {
                if(confirmed)
                    AlertUtil.showErrorAlert("Errore", "Impossibile concludere il prestito", null);
            }
        }
    }

    @FXML
    private void openViewAlert() throws IOException {
        String result = AlertUtil.showCustomButtonAlert("Visualizza Catalogo",
                "Scegli un'opzione da visualizzare", null,
                "Opere", "Edizioni", "Volumi");

        if (result != null) {
            switch (result) {
                case "Opere":
                    ViewCatalogueController.setWhatToView(ViewCatalogueController.SHOW_OPERE);
                    break;
                case "Edizioni":
                    ViewCatalogueController.setWhatToView(ViewCatalogueController.SHOW_EDIZIONI);
                    break;
                case "Volumi":
                    ViewCatalogueController.setWhatToView(ViewCatalogueController.SHOW_VOLUMI);
                    break;
                default:
                    ViewCatalogueController.setWhatToView(null);
            }
            App.setRoot("view_catalogue");
        }
    }

    // Metodi di supporto
    private int extractFirstNumber(String input) {
        String[] parts = input.split(" - ");
        for (String part : parts) {
            part = part.replaceAll("[^0-9]", "");
            if (!part.isEmpty()) {
                return Integer.parseInt(part);
            }
        }
        return -1;
    }

    private int extractEditionNumber(String input) {
        String[] parts = input.split(" - ");
        if (parts.length > 2) {
            String editionPart = parts[2].replaceAll("[^0-9]", "");
            if (!editionPart.isEmpty()) {
                return Integer.parseInt(editionPart);
            }
        }
        return -1;
    }

    private void refreshPrestiti() {
        prestiti.clear();
        LoanService.stampaTuttiPrestiti(prestiti, listaPrestiti);
    }

    @FXML
    private void initialize() {
        // Setup listener
        listaPrestiti.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            returnBookButton.setDisable(newVal == null);
        });

        // Caricamento iniziale
        BookService.stampaCatalogoBibliotecario(catalogo, listaCatalogo);
        LoanService.stampaTuttiPrestiti(prestiti, listaPrestiti);

        // Stato iniziale pulsante
        returnBookButton.setDisable(true);
    }

    private void filterCatalogueList(String searchText) {
        ArrayList<String> filteredItems = new ArrayList<>();
        for (String item : listaCatalogo.getItems()) {
            if (item.toLowerCase().contains(searchText.toLowerCase())) {
                filteredItems.add(item);
            }
        }
        listaCatalogo.getItems().setAll(filteredItems);
    }
}