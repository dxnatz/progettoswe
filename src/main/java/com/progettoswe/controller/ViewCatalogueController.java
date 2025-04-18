package com.progettoswe.controller;

import com.progettoswe.App;
import com.progettoswe.business_logic.InfoCommService;
import com.progettoswe.business_logic.OperaService;
import com.progettoswe.model.Commento;
import com.progettoswe.model.Edizione;
import com.progettoswe.model.Opera;
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
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ViewCatalogueController {
    public static final String SHOW_OPERE = "opera";
    public static final String SHOW_EDIZIONI = "edizione";
    public static final String SHOW_VOLUMI = "volume";
    private static String whatToView = null;

    @FXML private ListView<String> itemsListView;
    @FXML private Label titleLabel;
    @FXML private TextField searchField;

    //Pulsanti
    @FXML private Button deleteButton;
    @FXML private Button editButton;
    @FXML private Button reviewsButton;
    @FXML private Button addVolumeButton;
    @FXML private Button addEdizioneButton;
    @FXML private Button addOperaButton;

    //Hbox che contiene i pulsanti
    @FXML private HBox buttonContainer;

    @FXML
    public void initialize() {
        loadItems();
        setupSearchListener();
        setButtonsVisibility();

        itemsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isSelected = newVal != null;
            deleteButton.setDisable(!isSelected);
            editButton.setDisable(!isSelected);
            reviewsButton.setDisable(!isSelected);
            addVolumeButton.setDisable(!isSelected);
            addEdizioneButton.setDisable(!isSelected);
            addOperaButton.setDisable(!isSelected);
        });
    }

    private void setButtonsVisibility() {
        boolean showReviews = whatToView.equals(SHOW_OPERE) || whatToView.equals(SHOW_VOLUMI);
        reviewsButton.setVisible(showReviews);
        reviewsButton.setManaged(showReviews);

        if (whatToView == null) {
            return;
        }
        switch (whatToView){
            case SHOW_OPERE:
                addVolumeButton.setVisible(false);
                addVolumeButton.setManaged(false);
                break;
            case SHOW_EDIZIONI:
                addOperaButton.setVisible(false);
                addOperaButton.setManaged(false);
                break;
            case SHOW_VOLUMI:
                addOperaButton.setVisible(false);
                addOperaButton.setManaged(false);
                addEdizioneButton.setVisible(false);
                addEdizioneButton.setManaged(false);
                break;
            default:
                break;
        }
    }

    @FXML
    private void handleReviews() throws IOException, SQLException {
        String selectedItem = itemsListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

        if (whatToView.equals(SHOW_OPERE)) {
            // Gestione commenti opera
            int id = Integer.parseInt(selectedItem.split(" - ")[0].trim());
            String title = selectedItem.split(" - ")[1].trim();
            ViewOperaCommentsController.setOperaTitle(title);
            ViewOperaCommentsController.setOperaId(id);
            App.setRoot("view_opera_comments");
        }
        else if (whatToView.equals(SHOW_VOLUMI)) {
            int volumeId = Integer.parseInt(selectedItem.split(" - ")[0].trim());
            String title = selectedItem.split(" - ")[1].trim();

            List<Commento> commenti = InfoCommService.getCommentiVolumeCompleti(volumeId);
            ViewVolumeCommentsController.setVolumeTitle(title);
            ViewVolumeCommentsController.setVolumeId(volumeId);
            App.setRoot("view_volume_comments");
        }
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
                    addEdizioneButton.setDisable(false);

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
                    addVolumeButton.setDisable(false);

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

        String conferma = "Cancella " + cosaCancellare;
        String r = AlertUtil.showCustomButtonAlert("Cancella " + cosaCancellare ,
                "Sei sicuro di voler cancellare questo elemento?",
                null, conferma);

        if(r != null && r.equals(conferma)){
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

    @FXML
    private void handleAddOpera() throws IOException {
        AddBookController.typeToAdd = AddBookController.ADD_OPERA;
        openAddBookWindow();
    }

    @FXML
    private void handleAddVolume(){
        if(whatToView == null) return;
        switch (whatToView){
            case SHOW_OPERE:
                return;

            case SHOW_EDIZIONI: //aperto nella finestra edizione, id edizione recuperabile
                int id = Integer.parseInt(itemsListView.getSelectionModel().getSelectedItem().split(" - ")[0]);
                AddBookController.typeToAdd = AddBookController.ADD_VOLUME;
                AddBookController.setSelectedEdizioneId(id);
                try {
                    openAddBookWindow();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case SHOW_VOLUMI: //aperto nella finestra volume, id edizione da inserire
                AddBookController.typeToAdd = AddBookController.ADD_VOLUME;
                AddBookController.setSelectedEdizioneId(-1);
                try {
                    openAddBookWindow();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            default:
                return;
        }
    }

    @FXML
    private void handleAddEdizione() {
        if(whatToView == null) return;
        switch (whatToView){
            case SHOW_OPERE: //aperto nella finestra opera, id opera recuperabile
                int id = Integer.parseInt(itemsListView.getSelectionModel().getSelectedItem().split(" - ")[0]);
                AddBookController.typeToAdd = AddBookController.ADD_EDIZIONE;
                AddBookController.setSelectedOperaId(id);
                try {
                    openAddBookWindow();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SHOW_EDIZIONI: //aperto nella finestra edizione, id opera da inserire
                AddBookController.typeToAdd = AddBookController.ADD_EDIZIONE;
                AddBookController.setSelectedOperaId(-1);
                try {
                    openAddBookWindow();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case SHOW_VOLUMI: //errato
                return;

            default:
                return;
        }
    }

    private void openAddBookWindow() throws IOException {
        App.setRoot("add_book");
        AddBookController.setReturnWindow("view_catalogue:" + whatToView);
    }

    public static void setWhatToView(String whatToView) {
        ViewCatalogueController.whatToView = whatToView;
    }

    public static String getWhatToView() {
        return whatToView;
    }
}