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

    // Campi UI
    @FXML private TextField codiceOperaField, titoloField, autoreField, genereField, annoField;
    @FXML private TextArea descrizioneArea;
    @FXML private TextField codiceOperaEdizioneField, codiceEdizioneField, numeroEdizioneField;
    @FXML private TextField editoreField, annoEdizioneField, isbnField;
    @FXML private TextField codiceVolumeField, codiceEdizioneVolumeField, posizioneField;
    @FXML private ComboBox<String> statoCombo;
    @FXML private Button cancelButton, saveButton;
    @FXML private TabPane tabPane;
    @FXML private Tab tabOpera, tabEdizione, tabVolume;
    @FXML private Label titleLabel;

    @FXML
    private void initialize() {
        setupUIComponents();
        setupEventHandlers();
        loadData();
    }

    private void setupUIComponents() {
        statoCombo.getItems().addAll("disponibile", "in prestito", "danneggiato");
        statoCombo.getSelectionModel().selectFirst();
    }

    private void setupEventHandlers() {
        cancelButton.setOnAction(event -> {
            try {
                closeWindow();
            } catch (IOException e) {
                showError("Errore", "Impossibile chiudere la finestra", e.getMessage());
            }
        });
    }

    private void loadData() {
        try {
            switch (editType) {
                case EDIT_OPERA:
                    configureOperaEdit();
                    break;
                case EDIT_EDIZIONE:
                    configureEdizioneEdit();
                    break;
                case EDIT_VOLUME:
                    configureVolumeEdit();
                    break;
            }
        } catch (SQLException e) {
            showError("Errore", "Impossibile caricare i dati", e.getMessage());
        }
    }

    private void configureOperaEdit() throws SQLException {
        titleLabel.setText("Modifica Opera");
        tabPane.getSelectionModel().select(tabOpera);
        disableUnusedTabs(tabEdizione, tabVolume);

        Opera opera = OperaService.getOperaById(operaId);
        if (opera != null) {
            populateOperaFields(opera);
        }

        saveButton.setOnAction(event -> updateOpera());
    }

    private void configureEdizioneEdit() throws SQLException {
        titleLabel.setText("Modifica Edizione");
        tabPane.getSelectionModel().select(tabEdizione);
        disableUnusedTabs(tabOpera, tabVolume);

        Edizione edizione = EdizioneService.getEdizione(edizioneId);
        if (edizione != null) {
            populateEdizioneFields(edizione);
        }

        saveButton.setOnAction(event -> updateEdizione());
    }

    private void configureVolumeEdit() throws SQLException {
        titleLabel.setText("Modifica Volume");
        tabPane.getSelectionModel().select(tabVolume);
        disableUnusedTabs(tabOpera, tabEdizione);

        Volume volume = VolumeService.getVolume(volumeId);
        if (volume != null) {
            populateVolumeFields(volume);
        }

        saveButton.setOnAction(event -> updateVolume());
    }

    private void disableUnusedTabs(Tab... tabs) {
        for (Tab tab : tabs) {
            tab.setDisable(true);
        }
    }

    private void populateOperaFields(Opera opera) {
        codiceOperaField.setText(String.valueOf(opera.getId_opera()));
        titoloField.setText(opera.getTitolo());
        autoreField.setText(opera.getAutore());
        genereField.setText(opera.getGenere());
        annoField.setText(String.valueOf(opera.getAnnoPubblicazioneOriginale()));
        descrizioneArea.setText(opera.getDescrizione());
    }

    private void populateEdizioneFields(Edizione edizione) {
        codiceOperaEdizioneField.setText(String.valueOf(edizione.getOpera().getId_opera()));
        codiceEdizioneField.setText(String.valueOf(edizione.getId_edizione()));
        numeroEdizioneField.setText(String.valueOf(edizione.getNumero()));
        editoreField.setText(edizione.getEditore());
        annoEdizioneField.setText(String.valueOf(edizione.getAnnoPubblicazione()));
        isbnField.setText(edizione.getIsbn());
    }

    private void populateVolumeFields(Volume volume) {
        codiceVolumeField.setText(String.valueOf(volume.getId_volume()));
        codiceEdizioneVolumeField.setText(String.valueOf(volume.getEdizione().getId_edizione()));
        statoCombo.getSelectionModel().select(volume.getStato());
        posizioneField.setText(volume.getPosizione());
    }

    // Metodi di aggiornamento
    private void updateOpera() {
        if (!validateOperaFields()) return;

        try {
            Opera opera = createOperaFromFields();
            boolean success = OperaService.updateOpera(opera);
            handleUpdateResult(success, "Opera", "aggiornata");
        } catch (Exception e) {
            showError("Errore", "Si è verificato un errore", e.getMessage());
        }
    }

    private void updateEdizione() {
        if (!validateEdizioneFields()) return;

        try {
            Edizione edizione = createEdizioneFromFields();
            boolean success = EdizioneService.modificaEdizione(edizione);
            handleUpdateResult(success, "Edizione", "aggiornata");
        } catch (Exception e) {
            showError("Errore", "Si è verificato un errore", e.getMessage());
        }
    }

    private void updateVolume() {
        if (!validateVolumeFields()) return;

        try {
            Volume volume = createVolumeFromFields();
            boolean success = VolumeService.modificaVolume(volume);
            handleUpdateResult(success, "Volume", "aggiornato");
        } catch (Exception e) {
            showError("Errore", "Si è verificato un errore", e.getMessage());
        }
    }

    private Opera createOperaFromFields() {
        return new Opera(
                operaId,
                titoloField.getText(),
                autoreField.getText(),
                genereField.getText(),
                Integer.parseInt(annoField.getText()),
                descrizioneArea.getText()
        );
    }

    private Edizione createEdizioneFromFields() throws SQLException {
        int newOperaId = Integer.parseInt(codiceOperaEdizioneField.getText());
        Opera opera = OperaService.getOperaById(newOperaId);
        if (opera == null) {
            throw new IllegalArgumentException("Opera non trovata");
        }

        return new Edizione(
                isbnField.getText(),
                Integer.parseInt(annoEdizioneField.getText()),
                editoreField.getText(),
                Integer.parseInt(numeroEdizioneField.getText()),
                opera,
                edizioneId
        );
    }

    private Volume createVolumeFromFields() throws SQLException {
        int newEdizioneId = Integer.parseInt(codiceEdizioneVolumeField.getText());
        Edizione edizione = EdizioneService.getEdizione(newEdizioneId);
        if (edizione == null) {
            throw new IllegalArgumentException("Edizione non trovata");
        }

        return new Volume(
                volumeId,
                edizione,
                statoCombo.getValue(),
                posizioneField.getText()
        );
    }

    private void handleUpdateResult(boolean success, String entity, String action) throws IOException {
        if (success) {
            AlertUtil.showInfoAlert("Successo", entity + " " + action + " correttamente", "");
            closeWindow();
        } else {
            AlertUtil.showErrorAlert("Errore", "Impossibile " + action + " " + entity.toLowerCase(), "");
        }
    }

    // Metodi di validazione
    private boolean validateOperaFields() {
        boolean valid = InputValidator.validateNotEmpty(titoloField, "Titolo");
        valid &= InputValidator.validateNotEmpty(autoreField, "Autore");
        valid &= InputValidator.validateNotEmpty(genereField, "Genere");
        valid &= InputValidator.validateNotEmpty(annoField, "Anno di pubblicazione");
        valid &= InputValidator.validateInteger(annoField, "Anno di pubblicazione");
        return valid;
    }

    private boolean validateEdizioneFields() {
        boolean valid = InputValidator.validateNotEmpty(codiceOperaEdizioneField, "Codice opera");
        valid &= InputValidator.validateInteger(codiceOperaEdizioneField, "Codice opera");
        valid &= InputValidator.validateNotEmpty(numeroEdizioneField, "Numero edizione");
        valid &= InputValidator.validateInteger(numeroEdizioneField, "Numero edizione");
        valid &= InputValidator.validateNotEmpty(editoreField, "Editore");
        valid &= InputValidator.validateNotEmpty(annoEdizioneField, "Anno pubblicazione");
        valid &= InputValidator.validateInteger(annoEdizioneField, "Anno pubblicazione");
        return valid;
    }

    private boolean validateVolumeFields() {
        boolean valid = InputValidator.validateNotEmpty(codiceEdizioneVolumeField, "Codice edizione");
        valid &= InputValidator.validateInteger(codiceEdizioneVolumeField, "Codice edizione");
        if (statoCombo.getValue() == null) {
            showError("Errore", "Campo mancante", "Seleziona uno stato per il volume");
            valid = false;
        }
        return valid;
    }

    private void showError(String title, String header, String content) {
        AlertUtil.showErrorAlert(title, header, content);
    }

    private void closeWindow() throws IOException {
        App.setRoot("op_user");
    }
}