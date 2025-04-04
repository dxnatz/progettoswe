package com.progettoswe.controller;

import com.progettoswe.App;
import com.progettoswe.business_logic.OperaService;
import com.progettoswe.model.Edizione;
import com.progettoswe.model.Opera;
import com.progettoswe.model.Session;
import com.progettoswe.model.Volume;
import com.progettoswe.business_logic.EdizioneService;
import com.progettoswe.business_logic.VolumeService;
import com.progettoswe.utilities.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ViewCatalogueController {
    public static final String SHOW_OPERE = "opera";
    public static final String SHOW_EDIZIONI = "edizione";
    public static final String SHOW_VOLUMI = "volume";
    public static String whatToView = null;

    @FXML private ListView<String> itemsListView;
    @FXML private Label titleLabel;
    @FXML private TextField searchField;
    @FXML private Button deleteButton;
    @FXML private Button editButton;
    @FXML private Button reviewsButton;

    @FXML
    public void initialize() {
        loadItems();
        setupSearchListener();

        boolean showReviews = whatToView.equals(SHOW_OPERE);
        reviewsButton.setVisible(showReviews);
        reviewsButton.setManaged(showReviews);

        itemsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isSelected = newVal != null;
            deleteButton.setDisable(!isSelected);
            editButton.setDisable(!isSelected);
            reviewsButton.setDisable(!isSelected);
        });
    }

    private void loadItems() {
        try {
            ObservableList<String> items = FXCollections.observableArrayList();

            switch (whatToView) {
                case SHOW_OPERE:
                    titleLabel.setText("Catalogo Opere");
                    List<Opera> opere = OperaService.getAllOpere();
                    for (Opera opera : opere) {
                        items.add(String.format("%d - %s - %s (%s, %d)",
                                opera.getId_opera(),
                                opera.getTitolo(),
                                opera.getAutore(),
                                opera.getGenere(),
                                opera.getAnnoPubblicazioneOriginale()));
                    }
                    break;

                case SHOW_EDIZIONI:
                    titleLabel.setText("Catalogo Edizioni");
                    List<Edizione> edizioni = EdizioneService.getAllEdizioni();
                    for (Edizione edizione : edizioni) {
                        items.add(String.format("%d - %s - %s | Ed.%d (%s, %d) | ISBN: %s",
                                edizione.getId_edizione(),
                                edizione.getOpera().getTitolo(),
                                edizione.getOpera().getAutore(),
                                edizione.getNumero(),
                                edizione.getEditore(),
                                edizione.getAnnoPubblicazione(),
                                edizione.getIsbn()));
                    }
                    break;

                case SHOW_VOLUMI:
                    titleLabel.setText("Catalogo Volumi");
                    List<Volume> volumi = VolumeService.getAllVolumi();
                    for (Volume volume : volumi) {
                        items.add(String.format("%d - %s - Ed.%d | Stato: %s | Pos: %s | ISBN: %s",
                                volume.getId_volume(),
                                volume.getEdizione().getOpera().getTitolo(),
                                volume.getEdizione().getNumero(),
                                volume.getStato(),
                                volume.getPosizione(),
                                volume.getEdizione().getIsbn()));
                    }
                    break;

                default:
                    titleLabel.setText("Selezione non valida");
                    break;
            }

            itemsListView.setItems(items);
        } catch (SQLException e) {
            e.printStackTrace();
            // Mostra un messaggio di errore all'utente
            itemsListView.setItems(FXCollections.observableArrayList(
                    "Errore durante il caricamento dei dati: " + e.getMessage()
            ));
        }
    }

    private void setupSearchListener() {
        searchField.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            String searchText = searchField.getText().toLowerCase();

            if (searchText.isEmpty()) {
                loadItems();
                return;
            }

            try {
                ObservableList<String> filteredItems = FXCollections.observableArrayList();

                //TODO: Non filtra bene i codici
                if (SHOW_OPERE.equals(whatToView)) {
                    List<Opera> opere = OperaService.searchOpere(searchText);
                    for (Opera opera : opere) {
                        filteredItems.add(String.format("%d - %s - %s (%s)", opera.getId_opera(), opera.getTitolo(), opera.getAutore(), opera.getGenere()));
                    }
                } else if (SHOW_EDIZIONI.equals(whatToView)) {
                    List<Edizione> edizioni = EdizioneService.searchEdizioni(searchText);
                    for (Edizione edizione : edizioni) {
                        filteredItems.add(String.format("%d - %s - %s | Ed.%d (%s)", edizione.getId_edizione(), edizione.getOpera().getTitolo(), edizione.getOpera().getAutore(), edizione.getNumero(), edizione.getEditore()));
                    }
                } else if (SHOW_VOLUMI.equals(whatToView)) {
                    List<Volume> volumi = VolumeService.searchVolumi(searchText);
                    for (Volume volume : volumi) {
                        filteredItems.add(String.format("%d - %s - Ed.%d | Stato: %s | Pos: %s", volume.getId_volume(), volume.getEdizione().getOpera().getTitolo(), volume.getEdizione().getNumero(), volume.getStato(), volume.getPosizione()));
                    }
                }

                itemsListView.setItems(filteredItems);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleBack() throws IOException {
        App.setRoot("op_user");
    }

    @FXML
    private void handleClear() {
        searchField.clear();
        loadItems();
    }

    @FXML
    private void handleReviews() throws IOException {
        if(!whatToView.equals(SHOW_OPERE)) return;

        String selectedItem = itemsListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

        int id = Integer.parseInt(selectedItem.split(" - ")[0].trim());
        String title = selectedItem.split(" - ")[1].trim();
        ViewCommentsController.setOperaTitle(title);
        ViewCommentsController.setOperaId(id);
        App.setRoot("view_comments");
    }

    @FXML
    private void handleDelete() throws SQLException {
        if(whatToView == null) return;

        String cosaCancellare = "";
        switch (whatToView){
            case SHOW_OPERE:
                cosaCancellare = "opera";
                break;
            case SHOW_EDIZIONI:
                cosaCancellare = "edizione";
                break;
            case SHOW_VOLUMI:
                cosaCancellare = "volume";
                break;
        }
        String s = itemsListView.getSelectionModel().getSelectedItem();
        if (s == null) return;

        String r = AlertUtil.showCustomButtonAlert("Cancella " + cosaCancellare ,
                "Sei sicuro di voler cancellare questo elemento?",
                null, "Conferma");

        if(r.equals("Conferma")){
            int codice = Integer.parseInt(s.split(" - ")[0]);
            boolean risultato = false;

            switch (whatToView) {
                case SHOW_OPERE:
                    risultato = OperaService.deleteOpera(codice);
                    break;

                case SHOW_EDIZIONI:
                    risultato = EdizioneService.rimuoviEdizione(codice);
                    break;

                case SHOW_VOLUMI:
                    risultato = VolumeService.rimuoviVolume(codice);
                    break;
            }
            if(risultato){
                AlertUtil.showInfoAlert("Successo", "Cancellazione avvenuta con successo", null);
                loadItems();
            } else {
                AlertUtil.showErrorAlert("Errore", "Cancellazione non riuscita", null);
            }
        }

    }

    @FXML
    private void handleEdit() throws IOException, SQLException {
        if (whatToView == null) return;

        String selectedItem = itemsListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

        int id = Integer.parseInt(selectedItem.split(" - ")[0]);

        switch (whatToView) {
            case SHOW_OPERE:
                EditBookController.editType = EditBookController.EDIT_OPERA;
                EditBookController.operaId = id;
                break;

            case SHOW_EDIZIONI:
                EditBookController.editType = EditBookController.EDIT_EDIZIONE;
                EditBookController.edizioneId = id;
                break;

            case SHOW_VOLUMI:
                EditBookController.editType = EditBookController.EDIT_VOLUME;
                EditBookController.volumeId = id;
                break;
        }

        App.setRoot("edit_book");
    }
}