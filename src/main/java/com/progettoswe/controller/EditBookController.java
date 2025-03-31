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

public class EditBookController {
    public static final String EDIT_OPERA = "edit_opera";
    public static final String EDIT_EDIZIONE = "edit_edizione";
    public static final String EDIT_VOLUME = "edit_volume";
    public static String editType = null;

    // ID dell'elemento da modificare
    public static int operaId = -1;
    public static int edizioneId = -1;
    public static int volumeId = -1;

    // Campi Opera
    @FXML private TextField codiceOperaField;
    @FXML private TextField titoloField;
    @FXML private TextField autoreField;
    @FXML private TextField genereField;
    @FXML private TextField annoField;
    @FXML private TextArea descrizioneArea;

    // Campi Edizione
    @FXML private TextField codiceOperaEdizioneField;
    @FXML private TextField codiceEdizioneField;
    @FXML private TextField numeroEdizioneField;
    @FXML private TextField editoreField;
    @FXML private TextField annoEdizioneField;
    @FXML private TextField isbnField;

    // Campi Volume
    @FXML private TextField codiceVolumeField;
    @FXML private TextField codiceEdizioneVolumeField;
    @FXML private ComboBox<String> statoCombo;
    @FXML private TextField posizioneField;

    // Pulsanti e Tab
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private TabPane tabPane;
    @FXML private Tab tabOpera;
    @FXML private Tab tabEdizione;
    @FXML private Tab tabVolume;
    @FXML private Label titleLabel;

    @FXML
    private void initialize() {
        // Inizializza la ComboBox per lo stato
        statoCombo.getItems().addAll("disponibile", "in prestito", "danneggiato");
        statoCombo.getSelectionModel().selectFirst();

        cancelButton.setOnAction(event -> {
            try {
                closeWindow();
            } catch (IOException e) {
                AlertUtil.showErrorAlert("Errore", "Impossibile chiudere la finestra", e.getMessage());
            }
        });

        try {
            loadDataAndConfigureUI();
        } catch (SQLException e) {
            AlertUtil.showErrorAlert("Errore", "Impossibile caricare i dati", e.getMessage());
        }
    }

    private void loadDataAndConfigureUI() throws SQLException {
        switch (editType) {
            case EDIT_OPERA:
                configureForOperaEdit();
                break;
            case EDIT_EDIZIONE:
                configureForEdizioneEdit();
                break;
            case EDIT_VOLUME:
                configureForVolumeEdit();
                break;
        }
    }

    private void configureForOperaEdit() throws SQLException {
        titleLabel.setText("Modifica Opera");
        tabPane.getSelectionModel().select(tabOpera);
        tabEdizione.setDisable(true);
        tabVolume.setDisable(true);

        Opera opera = OperaService.getOperaById(operaId);
        if (opera != null) {
            codiceOperaField.setText(String.valueOf(opera.getId_opera()));
            titoloField.setText(opera.getTitolo());
            autoreField.setText(opera.getAutore());
            genereField.setText(opera.getGenere());
            annoField.setText(String.valueOf(opera.getAnnoPubblicazioneOriginale()));
            descrizioneArea.setText(opera.getDescrizione());
        }

        saveButton.setOnAction(event -> updateOpera());
    }

    private void configureForEdizioneEdit() throws SQLException {
        titleLabel.setText("Modifica Edizione");
        tabPane.getSelectionModel().select(tabEdizione);
        tabOpera.setDisable(true);
        tabVolume.setDisable(true);

        Edizione edizione = EdizioneService.getEdizione(edizioneId);
        if (edizione != null) {
            codiceOperaEdizioneField.setText(String.valueOf(edizione.getOpera().getId_opera()));
            codiceEdizioneField.setText(String.valueOf(edizione.getId_edizione()));
            numeroEdizioneField.setText(String.valueOf(edizione.getNumero()));
            editoreField.setText(edizione.getEditore());
            annoEdizioneField.setText(String.valueOf(edizione.getAnnoPubblicazione()));
            isbnField.setText(edizione.getIsbn());
        }

        saveButton.setOnAction(event -> updateEdizione());
    }

    private void configureForVolumeEdit() throws SQLException {
        titleLabel.setText("Modifica Volume");
        tabPane.getSelectionModel().select(tabVolume);
        tabOpera.setDisable(true);
        tabEdizione.setDisable(true);

        Volume volume = VolumeService.getVolume(volumeId);
        if (volume != null) {
            codiceVolumeField.setText(String.valueOf(volume.getId_volume()));
            codiceEdizioneVolumeField.setText(String.valueOf(volume.getEdizione().getId_edizione()));
            statoCombo.getSelectionModel().select(volume.getStato());
            posizioneField.setText(volume.getPosizione());
        }

        saveButton.setOnAction(event -> updateVolume());
    }

    private void updateOpera() {
        if (!validateOperaFields()) {
            return;
        }

        try {
            Opera opera = new Opera(
                    operaId,
                    titoloField.getText(),
                    autoreField.getText(),
                    genereField.getText(),
                    Integer.parseInt(annoField.getText()),
                    descrizioneArea.getText()
            );

            boolean success = OperaService.updateOpera(opera);
            if (success) {
                AlertUtil.showInfoAlert("Successo", "Opera aggiornata correttamente", "");
                closeWindow();
            } else {
                AlertUtil.showErrorAlert("Errore", "Impossibile aggiornare l'opera", "");
            }
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Errore", "Si è verificato un errore", e.getMessage());
        }
    }

    private void updateEdizione() {
        if (!validateEdizioneFields()) {
            return;
        }

        try {
            // Verifica che l'opera esista
            int newOperaId = Integer.parseInt(codiceOperaEdizioneField.getText());
            Opera opera = OperaService.getOperaById(newOperaId);
            if (opera == null) {
                AlertUtil.showErrorAlert("Errore", "Opera non trovata", "Inserisci un codice opera valido");
                return;
            }

            Edizione edizione = new Edizione(
                    isbnField.getText(),
                    Integer.parseInt(annoEdizioneField.getText()),
                    editoreField.getText(),
                    Integer.parseInt(numeroEdizioneField.getText()),
                    opera,
                    edizioneId
            );

            boolean success = EdizioneService.modificaEdizione(edizione);
            if (success) {
                AlertUtil.showInfoAlert("Successo", "Edizione aggiornata correttamente", "");
                closeWindow();
            } else {
                AlertUtil.showErrorAlert("Errore", "Impossibile aggiornare l'edizione", "");
            }
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Errore", "Si è verificato un errore", e.getMessage());
        }
    }

    private void updateVolume() {
        if (!validateVolumeFields()) {
            return;
        }

        try {
            // Verifica che l'edizione esista
            int newEdizioneId = Integer.parseInt(codiceEdizioneVolumeField.getText());
            Edizione edizione = EdizioneService.getEdizione(newEdizioneId);
            if (edizione == null) {
                AlertUtil.showErrorAlert("Errore", "Edizione non trovata", "Inserisci un codice edizione valido");
                return;
            }

            Volume volume = new Volume(
                    volumeId,
                    edizione,
                    statoCombo.getValue(),
                    posizioneField.getText()
            );

            boolean success = VolumeService.modificaVolume(volume);
            if (success) {
                AlertUtil.showInfoAlert("Successo", "Volume aggiornato correttamente", "");
                closeWindow();
            } else {
                AlertUtil.showErrorAlert("Errore", "Impossibile aggiornare il volume", "");
            }
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Errore", "Si è verificato un errore", e.getMessage());
        }
    }

    private boolean validateOperaFields() {
        boolean isValid = true;

        if (!InputValidator.validateNotEmpty(titoloField, "Titolo")) isValid = false;
        if (!InputValidator.validateNotEmpty(autoreField, "Autore")) isValid = false;
        if (!InputValidator.validateNotEmpty(genereField, "Genere")) isValid = false;
        if (!InputValidator.validateNotEmpty(annoField, "Anno di pubblicazione")) isValid = false;
        if (!InputValidator.validateInteger(annoField, "Anno di pubblicazione")) isValid = false;

        return isValid;
    }

    private boolean validateEdizioneFields() {
        boolean isValid = true;

        if (!InputValidator.validateNotEmpty(codiceOperaEdizioneField, "Codice opera")) isValid = false;
        if (!InputValidator.validateInteger(codiceOperaEdizioneField, "Codice opera")) isValid = false;
        if (!InputValidator.validateNotEmpty(numeroEdizioneField, "Numero edizione")) isValid = false;
        if (!InputValidator.validateInteger(numeroEdizioneField, "Numero edizione")) isValid = false;
        if (!InputValidator.validateNotEmpty(editoreField, "Editore")) isValid = false;
        if (!InputValidator.validateNotEmpty(annoEdizioneField, "Anno pubblicazione")) isValid = false;
        if (!InputValidator.validateInteger(annoEdizioneField, "Anno pubblicazione")) isValid = false;

        return isValid;
    }

    private boolean validateVolumeFields() {
        boolean isValid = true;

        if (!InputValidator.validateNotEmpty(codiceEdizioneVolumeField, "Codice edizione")) isValid = false;
        if (!InputValidator.validateInteger(codiceEdizioneVolumeField, "Codice edizione")) isValid = false;
        if (statoCombo.getValue() == null) {
            AlertUtil.showErrorAlert("Errore", "Campo mancante", "Seleziona uno stato per il volume");
            isValid = false;
        }

        return isValid;
    }

    private void closeWindow() throws IOException {
        App.setRoot("op_user");
    }
}