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
    public static final String ADD_OPERA = "add_opera_only";
    public static final String ADD_EDIZIONE = "add_edizione_only";
    public static final String ADD_VOLUME = "add_volume_only";
    public static String typeToAdd = null;

    // Variabili per il preinserimento
    public static int selectedOperaId = -1;
    public static int selectedEdizioneId = -1;

    // Campi UI
    @FXML private TextField codiceOperaField;
    @FXML private TextField titoloField;
    @FXML private TextField autoreField;
    @FXML private TextField genereField;
    @FXML private TextField annoField;
    @FXML private TextArea descrizioneArea;
    @FXML private TextField codiceEdizioneField;
    @FXML private TextField numeroEdizioneField;
    @FXML private TextField editoreField;
    @FXML private TextField annoEdizioneField;
    @FXML private TextField isbnField;
    @FXML private ComboBox<String> statoCombo;
    @FXML private TextField posizioneField;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private Tab tabOpera;
    @FXML private Tab tabEdizione;
    @FXML private Tab tabVolume;

    @FXML
    private void initialize() {
        setupUI();
        configureForOperationType();
    }

    private void setupUI() {
        statoCombo.getItems().addAll("disponibile", "in prestito", "danneggiato");
        statoCombo.getSelectionModel().selectFirst();

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
        codiceOperaField.setText("Nuovo codice");
        resetAllFields();
    }

    private void configureForEdizione() throws SQLException {
        tabVolume.setDisable(true);
        saveButton.setOnAction(event -> saveEdizione());
        saveButton.setText("Salva edizione");

        // Disabilita il campo codice edizione (verrà generato dal DB)
        codiceEdizioneField.setDisable(true);
        codiceEdizioneField.setText("Nuovo codice");

        if (selectedOperaId != -1) {
            Opera opera = OperaService.getOperaById(selectedOperaId);
            if (opera != null) {
                codiceOperaField.setText(String.valueOf(opera.getId_opera()));
                titoloField.setText(opera.getTitolo());
                autoreField.setText(opera.getAutore());
                genereField.setText(opera.getGenere());
                annoField.setText(String.valueOf(opera.getAnnoPubblicazioneOriginale()));
                descrizioneArea.setText(opera.getDescrizione());

                // Disabilita tutti i campi opera (non modificabili)
                disableOperaFields();
            }
        }
        resetEdizioneFields();
    }

    private void configureForVolume() throws SQLException {
        saveButton.setOnAction(event -> saveVolume());
        saveButton.setText("Salva volume");

        if (selectedOperaId != -1 && selectedEdizioneId != -1) {
            // Caso: nuovo volume per edizione esistente
            Opera opera = OperaService.getOperaById(selectedOperaId);
            Edizione edizione = EdizioneService.getEdizione(selectedEdizioneId);

            if (opera != null && edizione != null) {
                // Precompila i campi opera (sola lettura)
                codiceOperaField.setText(String.valueOf(opera.getId_opera()));
                titoloField.setText(opera.getTitolo());
                autoreField.setText(opera.getAutore());

                // Precompila i campi edizione (sola lettura)
                codiceEdizioneField.setText(String.valueOf(edizione.getId_edizione()));
                numeroEdizioneField.setText(String.valueOf(edizione.getNumero()));
                editoreField.setText(edizione.getEditore());

                // Disabilita tutti i campi tranne volume
                disableOperaFields();
                disableEdizioneFields();
            }
        } else if (selectedOperaId != -1) {
            // Caso: primo volume (nuova edizione)
            Opera opera = OperaService.getOperaById(selectedOperaId);
            if (opera != null) {
                // Precompila solo i campi opera (sola lettura)
                codiceOperaField.setText(String.valueOf(opera.getId_opera()));
                titoloField.setText(opera.getTitolo());
                autoreField.setText(opera.getAutore());

                // Disabilita solo i campi opera (tranne codice edizione)
                codiceOperaField.setDisable(true);
                titoloField.setDisable(true);
                autoreField.setDisable(true);
                genereField.setDisable(true);
                annoField.setDisable(true);
                descrizioneArea.setDisable(true);

                // Per edizione: solo codice edizione modificabile
                codiceEdizioneField.setDisable(false);
                numeroEdizioneField.setDisable(false);
                editoreField.setDisable(false);
                annoEdizioneField.setDisable(false);
                isbnField.setDisable(false);
            }
        }
        resetVolumeFields();
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
        statoCombo.getSelectionModel().selectFirst();
    }

    private void resetEdizioneFields() {
        numeroEdizioneField.clear();
        editoreField.clear();
        annoEdizioneField.clear();
        isbnField.clear();
    }

    private void resetVolumeFields() {
        posizioneField.clear();
        statoCombo.getSelectionModel().selectFirst();
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
            AlertUtil.showInfoAlert("Successo", "Opera aggiunta con ID: " + result, "");
            closeWindow();
        } catch (Exception e) {
            handleSaveError(e);
        }
    }

    private void saveEdizione() {
        if (!validateEdizioneFields()) return;

        try {
            Opera opera = OperaService.getOperaById(selectedOperaId);
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

            boolean result = EdizioneService.aggiungiEdizione(edizione);
            if (result) {
                AlertUtil.showInfoAlert("Successo", "Edizione aggiunta correttamente", "");
                closeWindow();
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
            if (selectedOperaId == -1) {
                AlertUtil.showErrorAlert("Errore", "Nessuna opera selezionata", "");
                return;
            }

            Edizione edizione;
            if (selectedEdizioneId != -1) {
                // Caso: volume per edizione esistente
                edizione = EdizioneService.getEdizione(selectedEdizioneId);
            } else {
                // Caso: primo volume (crea nuova edizione)
                Opera opera = OperaService.getOperaById(selectedOperaId);
                if (opera == null) {
                    AlertUtil.showErrorAlert("Errore", "Opera non trovata", "");
                    return;
                }

                edizione = new Edizione(
                        isbnField.getText(),
                        Integer.parseInt(annoEdizioneField.getText()),
                        editoreField.getText(),
                        Integer.parseInt(numeroEdizioneField.getText()),
                        opera,
                        0 // ID verrà generato dal DB
                );

                // Prima salva la nuova edizione
                if (!EdizioneService.aggiungiEdizione(edizione)) {
                    AlertUtil.showErrorAlert("Errore", "Impossibile creare l'edizione", "");
                    return;
                }
            }

            if (edizione == null) {
                AlertUtil.showErrorAlert("Errore", "Edizione non valida", "");
                return;
            }

            Volume volume = new Volume(
                    0, // ID verrà generato dal DB
                    edizione,
                    statoCombo.getValue(),
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
        return isValid;
    }

    private boolean validateVolumeFields() {
        boolean isValid = true;

        if (selectedEdizioneId == -1) {
            // Validazione per nuova edizione (primo volume)
            isValid &= validateEdizioneFields();
        }

        isValid &= InputValidator.validateNotEmpty(posizioneField, "Posizione");

        if (statoCombo.getValue() == null) {
            AlertUtil.showErrorAlert("Errore", "Campo mancante", "Selezionare uno stato per il volume");
            isValid = false;
        }

        return isValid;
    }

    private void handleSaveError(Exception e) {
        if (e instanceof NumberFormatException) {
            AlertUtil.showErrorAlert("Errore", "Formato non valido", "Inserire valori numerici nei campi richiesti");
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
        App.setRoot("op_user");
    }
}