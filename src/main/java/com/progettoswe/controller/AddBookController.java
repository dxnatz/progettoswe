package com.progettoswe.controller;

import com.progettoswe.App;
import com.progettoswe.business_logic.EdizioneService;
import com.progettoswe.business_logic.OperaService;
import com.progettoswe.business_logic.VolumeService;
import com.progettoswe.model.*;
import com.progettoswe.utilities.AlertUtil;
import com.progettoswe.utilities.InputValidator;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;

public class AddBookController {
    public static final String ADD_OPERA = "add_opera";
    public static final String ADD_EDIZIONE = "add_edizione";
    public static final String ADD_VOLUME = "add_volume";
    public static String typeToAdd = null;

    private static String returnWindow = "op_user";

    // Variabili per il preinserimento
    private static int selectedOperaId = -1;
    private static int selectedEdizioneId = -1;

    // Campi UI
    //opera
    @FXML private TextField codiceOperaField;
    @FXML private TextField titoloField;
    @FXML private TextField autoreField;
    @FXML private TextField genereField;
    @FXML private TextField annoField;
    @FXML private TextArea descrizioneArea;

    //edizione
    @FXML private TextField codiceEdizioneField;
    @FXML private TextField codiceOperaTabEdizioneField;
    @FXML private TextField numeroEdizioneField;
    @FXML private TextField editoreField;
    @FXML private TextField annoEdizioneField;
    @FXML private TextField isbnField;

    //volume
    @FXML private TextField posizioneField;
    @FXML private ComboBox<String> statoCombo;

    // pulsanti
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    // tabs
    @FXML private Tab tabOpera;
    @FXML private Tab tabEdizione;
    @FXML private Tab tabVolume;
    @FXML private TabPane tabPane;

    public static String getReturnWindow() {
        return returnWindow;
    }

    public static void setReturnWindow(String returnWindow) {
        AddBookController.returnWindow = returnWindow;
    }

    @FXML
    private void initialize() {
        setupUI();
        statoCombo.getItems().addAll("Disponibile");
        statoCombo.setDisable(true);

        configureForOperationType();
    }

    private void setupUI() {
        cancelButton.setOnAction(event -> {
            try {
                closeWindow();
            } catch (IOException e) {
                AlertUtil.showErrorAlert("Errore", "Impossibile chiudere la finestra", e.getMessage());
            }
        });
    }

    private void configureForOperationType() {
        try {
            switch (typeToAdd) {
                case ADD_OPERA:
                    configureForOpera();
                    break;
                case ADD_EDIZIONE:
                    configureForEdizione();
                    break;
                case ADD_VOLUME:
                    configureForVolume();
                    break;
            }
        } catch (SQLException e) {
            AlertUtil.showErrorAlert("Errore", "Errore di configurazione", e.getMessage());
        }
    }

    private void configureForOpera() {
        tabEdizione.setDisable(true);
        tabVolume.setDisable(true);
        saveButton.setOnAction(event -> saveOpera());
        saveButton.setText("Salva opera");

        // Disabilita il campo codice opera (verrà generato dal DB)
        codiceOperaField.setDisable(true);
        codiceOperaField.setText("Generato automaticamente");
        resetAllFields();
    }

    private void configureForEdizione() throws SQLException {
        saveButton.setOnAction(event -> saveEdizione());
        saveButton.setText("Salva edizione");

        System.out.println("Selected opera ID: " + selectedOperaId);

        codiceEdizioneField.setDisable(true);
        codiceEdizioneField.setText("Generato automaticamente");

        disableOperaFields();
        if(selectedOperaId != -1) {
            codiceOperaTabEdizioneField.setText(String.valueOf(selectedOperaId));
            codiceOperaField.setText(String.valueOf(selectedOperaId));
            tabOpera.setDisable(true);
        }else{
            codiceOperaTabEdizioneField.setText("Da inserire nel tab opera");
            codiceOperaTabEdizioneField.setDisable(true);
            tabOpera.setDisable(false);
            codiceOperaField.setDisable(false);
        }

        tabVolume.setDisable(true);
        tabPane.getSelectionModel().select(tabEdizione);
    }

    private void configureForVolume() throws SQLException {
        saveButton.setOnAction(event -> saveVolume());
        saveButton.setText("Salva volume");
        tabPane.getSelectionModel().select(tabVolume);
        statoCombo.setValue("Disponibile");

        if(selectedEdizioneId != -1) {
            codiceEdizioneField.setText(String.valueOf(selectedEdizioneId));
        }
        disableEdizioneFields();
        codiceEdizioneField.setDisable(false);

        disableOperaFields();
        tabOpera.setDisable(true);
    }

    private void disableOperaFields() {
        codiceOperaField.setDisable(true);
        titoloField.setDisable(true);
        autoreField.setDisable(true);
        genereField.setDisable(true);
        annoField.setDisable(true);
        descrizioneArea.setDisable(true);
    }

    private void disableEdizioneFields() {
        codiceEdizioneField.setDisable(true);
        codiceOperaTabEdizioneField.setDisable(true);
        numeroEdizioneField.setDisable(true);
        editoreField.setDisable(true);
        annoEdizioneField.setDisable(true);
        isbnField.setDisable(true);
    }

    private void resetAllFields() {
        titoloField.clear();
        autoreField.clear();
        genereField.clear();
        annoField.clear();
        descrizioneArea.clear();
        numeroEdizioneField.clear();
        editoreField.clear();
        annoEdizioneField.clear();
        isbnField.clear();
        posizioneField.clear();
    }

    private void resetEdizioneFields() {
        numeroEdizioneField.clear();
        editoreField.clear();
        annoEdizioneField.clear();
        isbnField.clear();
    }

    private void resetVolumeFields() {
        posizioneField.clear();
    }

    private void saveOpera() {
        if (!validateOperaFields()) return;

        try {
            Opera opera = new Opera(
                    0, // ID verrà generato dal DB
                    titoloField.getText(),
                    autoreField.getText(),
                    genereField.getText(),
                    Integer.parseInt(annoField.getText()),
                    descrizioneArea.getText()
            );

            int result = OperaService.addOpera(opera);
            if(result >= 1) {
                AlertUtil.showInfoAlert("Successo", "Opera aggiunta con ID: " + result, "");
                tabEdizione.setDisable(false);
                tabPane.getSelectionModel().select(tabEdizione);
                selectedOperaId = result;
                configureForEdizione();
            }else{
                AlertUtil.showErrorAlert("Errore", "Impossibile salvare l'opera", null);
            }
        } catch (Exception e) {
            handleSaveError(e);
        }
    }

    private void saveEdizione() {
        if (!validateEdizioneFields()) return;

        try {
            Opera opera = null;
            if(selectedOperaId != -1){
                opera = OperaService.getOperaById(selectedOperaId);
            }else{
                opera = OperaService.getOperaById(Integer.parseInt(codiceOperaField.getText()));
            }

            if (opera == null) {
                AlertUtil.showErrorAlert("Errore", "Opera non trovata", "");
                return;
            }

            Edizione edizione = new Edizione(
                    isbnField.getText(),
                    Integer.parseInt(annoEdizioneField.getText()),
                    editoreField.getText(),
                    Integer.parseInt(numeroEdizioneField.getText()),
                    opera,
                    0 // ID verrà generato dal DB
            );

            int result = EdizioneService.aggiungiEdizione(edizione);
            if (result >= 1) {
                AlertUtil.showInfoAlert("Successo", "Edizione aggiunta correttamente", "");
                tabVolume.setDisable(false);
                tabPane.getSelectionModel().select(tabVolume);
                selectedEdizioneId = result;
                configureForVolume();

            } else {
                AlertUtil.showErrorAlert("Errore", "Impossibile salvare l'edizione", "");
            }
        } catch (Exception e) {
            handleSaveError(e);
        }
    }

    private void saveVolume() {
        if (!validateVolumeFields()) return;

        try {
            Edizione edizione = null;
            if (selectedEdizioneId != -1) {
                // Caso: volume per edizione esistente
                edizione = EdizioneService.getEdizione(selectedEdizioneId);
            }else{
                edizione = EdizioneService.getEdizione(Integer.parseInt(codiceEdizioneField.getText()));
            }

            if (edizione == null) {
                AlertUtil.showErrorAlert("Errore", "Edizione non valida", "");
                return;
            }

            Volume volume = new Volume(
                    0, // ID verrà generato dal DB
                    edizione,
                    "disponibile",
                    posizioneField.getText()
            );

            boolean result = VolumeService.aggiungiVolume(volume);
            if (result) {
                AlertUtil.showInfoAlert("Successo", "Volume aggiunto correttamente", "");
                closeWindow();
            } else {
                AlertUtil.showErrorAlert("Errore", "Impossibile salvare il volume", "");
            }
        } catch (Exception e) {
            handleSaveError(e);
        }
    }

    private boolean validateOperaFields() {
        boolean isValid = InputValidator.validateNotEmpty(titoloField, "Titolo");
        isValid &= InputValidator.validateNotEmpty(autoreField, "Autore");
        isValid &= InputValidator.validateNotEmpty(genereField, "Genere");
        isValid &= InputValidator.validateNotEmpty(annoField, "Anno pubblicazione");
        isValid &= InputValidator.validateInteger(annoField, "Anno pubblicazione");
        isValid &= InputValidator.validateNotEmpty(descrizioneArea, "Descrizione");
        return isValid;
    }

    private boolean validateEdizioneFields() {
        boolean isValid = InputValidator.validateNotEmpty(numeroEdizioneField, "Numero edizione");
        isValid &= InputValidator.validateInteger(numeroEdizioneField, "Numero edizione");
        isValid &= InputValidator.validateNotEmpty(editoreField, "Editore");
        isValid &= InputValidator.validateNotEmpty(annoEdizioneField, "Anno pubblicazione");
        isValid &= InputValidator.validateInteger(annoEdizioneField, "Anno pubblicazione");
        isValid &= InputValidator.validateNotEmpty(isbnField, "ISBN");

        if(selectedOperaId == -1) {
            isValid &= InputValidator.validateNotEmpty(codiceOperaField, "codice opera");
            isValid &= InputValidator.validateInteger(codiceOperaField, "codice opera");
        }

        return isValid;
    }

    private boolean validateVolumeFields() {
        boolean isValid = true;

        if (selectedEdizioneId == -1) {
            isValid &= InputValidator.validateNotEmpty(codiceEdizioneField, "codice edizione");
        }

        isValid &= InputValidator.validateNotEmpty(posizioneField, "posizione");
        return isValid;
    }

    private void handleSaveError(Exception e) {
        if (e instanceof NumberFormatException) {
            AlertUtil.showErrorAlert("Errore", "Formato non valido", "Inserire valori numerici nei campi richiesti: " + e.getMessage());
        } else if (e instanceof SQLException) {
            AlertUtil.showErrorAlert("Errore DB", "Errore database", e.getMessage());
        } else {
            AlertUtil.showErrorAlert("Errore", "Errore generico", e.getMessage());
        }
    }

    private void closeWindow() throws IOException {
        typeToAdd = null;
        selectedOperaId = -1;
        selectedEdizioneId = -1;
        if(returnWindow == null) {
            returnWindow = "op_user";
        }else if(returnWindow.contains(":")) {
            ViewCatalogueController.setWhatToView(returnWindow.split(":")[1]);
            returnWindow = returnWindow.split(":")[0];
        }
        App.setRoot(returnWindow);
    }

    public static void setSelectedOperaId(int selectedOperaId) {
        AddBookController.selectedOperaId = selectedOperaId;
    }

    public static void setSelectedEdizioneId(int selectedEdizioneId) {
        AddBookController.selectedEdizioneId = selectedEdizioneId;
    }
}