package com.progettoswe.controller;

import com.progettoswe.App;
import com.progettoswe.business_logic.OperaService;
import com.progettoswe.model.Edizione;
import com.progettoswe.model.Opera;
import com.progettoswe.model.Volume;
import com.progettoswe.business_logic.EdizioneService;
import com.progettoswe.business_logic.VolumeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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

    @FXML
    private ListView<String> itemsListView;
    @FXML
    private Label titleLabel;
    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        loadItems();
        setupSearchListener();
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
                        items.add(String.format("%s - Ed.%d | Stato: %s | Pos: %s | ISBN: %s",
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
                        filteredItems.add(String.format("%s - Ed.%d | Stato: %s | Pos: %s", volume.getEdizione().getOpera().getTitolo(), volume.getEdizione().getNumero(), volume.getStato(), volume.getPosizione()));
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
}