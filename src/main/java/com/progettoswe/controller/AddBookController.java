package com.progettoswe.controller;

import com.progettoswe.App;
import com.progettoswe.model.*;
import com.progettoswe.utilities.AlertUtil;
import com.progettoswe.utilities.InputValidator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;

public class AddBookController {
    public static final String ADD_OPERA = "add_opera_only";
    public static final String ADD_EDIZIONE = "add_edizione_only";
    public static final String ADD_VOLUME = "add_volume_only";
    public static String typeToAdd = null;

    public static String isbnOpera = null;
    public static int idEdizione = -1;

    // Campi Opera
    @FXML private TextField titoloField;
    @FXML private TextField autoreField;
    @FXML private TextField genereField;
    @FXML private TextField annoField;
    @FXML private TextArea descrizioneArea;

    // Campi Edizione
    @FXML private TextField numeroEdizioneField;
    @FXML private TextField editoreField;
    @FXML private TextField annoEdizioneField;
    @FXML private TextField isbnField;

    // Campi Volume
    @FXML private ComboBox<String> statoCombo;
    @FXML private TextField posizioneField;

    // Pulsanti
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    @FXML private Tab tabOpera;
    @FXML private Tab tabEdizione;
    @FXML private Tab tabVolume;

    @FXML
    private void initialize() {
        // Inizializza la ComboBox per lo stato
        statoCombo.getItems().addAll("disponibile", "in prestito", "danneggiato");
        statoCombo.getSelectionModel().selectFirst();

        setCorrectWindowFields();

        // Imposta azioni pulsanti
        cancelButton.setOnAction(event -> {
            try {
                closeWindow();
            } catch (IOException e) {
                AlertUtil.showErrorAlert("Errore", "Impossibile chiudere la finestra", e.getMessage());
            }
        });
        saveButton.setOnAction(event -> saveBook());
    }

    private void setCorrectWindowFields(){
        if(ADD_OPERA.equals(typeToAdd)){
            tabEdizione.setDisable(true);
            tabVolume.setDisable(true);
        }else if(ADD_EDIZIONE.equals(typeToAdd)){
            tabVolume.setDisable(true);
        }else if(ADD_VOLUME.equals(typeToAdd)){

        }
    }

    private boolean saveOpera(){
        return false;
    }

    private boolean saveEdizione(){
        return false;
    }

    private void saveBook() {
        if (!validateFields()) {
            return;
        }

        try {
            // Crea e salva Opera
            Opera opera = new Opera(
                    0,
                    titoloField.getText(),
                    autoreField.getText(),
                    genereField.getText(),
                    InputValidator.isNumber(annoField.getText()) ? Integer.parseInt(annoField.getText()) : 0,
                    descrizioneArea.getText()
            );
            // TODO: OperaDAO.insertOpera(opera);

            // Crea e salva Edizione
            Edizione edizione = new Edizione(
                    isbnField.getText(),
                    InputValidator.isNumber(annoEdizioneField.getText()) ? Integer.parseInt(annoEdizioneField.getText()) : 0,
                    editoreField.getText(),
                    Integer.parseInt(numeroEdizioneField.getText()),
                    opera,
                    0
            );
            // TODO: EdizioneDAO.insertEdizione(edizione);

            // Crea e salva Volume
            Volume volume = new Volume(
                    0,
                    edizione,
                    statoCombo.getValue(),
                    posizioneField.getText()
            );
            // TODO: VolumeDAO.insertVolume(volume);

            AlertUtil.showInfoAlert("Successo", "Libro aggiunto correttamente", "");
            closeWindow();

        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Errore", "Formato numerico non valido", "Inserisci un numero valido nei campi anno e numero edizione");
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Errore", "Impossibile salvare il libro", e.getMessage());
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        // Validazione Opera
        if (!InputValidator.validateNotEmpty(titoloField, "Titolo")) isValid = false;
        if (!InputValidator.validateNotEmpty(autoreField, "Autore")) isValid = false;
        if (!annoField.getText().isEmpty() && !InputValidator.validateInteger(annoField, "Anno pubblicazione")) isValid = false;

        // Validazione Edizione
        if (!InputValidator.validateNotEmpty(numeroEdizioneField, "Numero edizione")) isValid = false;
        if (!InputValidator.validateInteger(numeroEdizioneField, "Numero edizione")) isValid = false;
        if (!InputValidator.validateNotEmpty(editoreField, "Editore")) isValid = false;
        if (!annoEdizioneField.getText().isEmpty() && !InputValidator.validateInteger(annoEdizioneField, "Anno pubblicazione edizione")) isValid = false;

        // Validazione Volume
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