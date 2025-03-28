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

    public static int codiceOpera = -1;
    public static int idEdizione = -1;

    // Campi Opera
    @FXML private TextField codiceOperaField;
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

        // Imposta azioni pulsanti e disabilita campi e tab
        cancelButton.setOnAction(event -> {
            try {
                closeWindow();
            } catch (IOException e) {
                AlertUtil.showErrorAlert("Errore", "Impossibile chiudere la finestra", e.getMessage());
            }
        });
        try {
            setCorrectWindowFields();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCorrectWindowFields() throws SQLException {
        Opera opera = null;
        Edizione edizione = null;

        if(ADD_OPERA.equals(typeToAdd)){
            tabEdizione.setDisable(true);
            tabVolume.setDisable(true);

            saveButton.setOnAction(event -> saveOpera());
            saveButton.setText("Salva opera");

        }else if(ADD_EDIZIONE.equals(typeToAdd)){
            tabVolume.setDisable(true);
            saveButton.setOnAction(event -> saveEdizione());
            saveButton.setText("Salva edizione");
            if(codiceOpera == -1){
                setAndDisableOperaFields();
            }else{
                opera = OperaService.getOperaById(codiceOpera);
                setAndDisableOperaFields(opera);
            }

        }else if(ADD_VOLUME.equals(typeToAdd)){
            saveButton.setOnAction(event -> saveVolume());
            opera = OperaService.getOperaById(codiceOpera);
            edizione = EdizioneService.getEdizione(idEdizione);
            setAndDisableOperaFields(opera);
            setAndDisableEdizioneFields(edizione);

        }
    }

    private void setAndDisableOperaFields(Opera opera){
        if(opera == null) return;

        codiceOperaField.setText(String.valueOf(opera.getId_opera()));
        titoloField.setText(opera.getTitolo());
        autoreField.setText(opera.getAutore());
        genereField.setText(opera.getGenere());
        annoField.setText(String.valueOf(opera.getAnnoPubblicazioneOriginale()));
        descrizioneArea.setText(opera.getDescrizione());

        codiceOperaField.setDisable(true);
        titoloField.setDisable(true);
        autoreField.setDisable(true);
        genereField.setDisable(true);
        annoField.setDisable(true);
        descrizioneArea.setDisable(true);
    }

    private void setAndDisableOperaFields(){
        codiceOperaField.setDisable(false);
        titoloField.setDisable(true);
        autoreField.setDisable(true);
        genereField.setDisable(true);
        annoField.setDisable(true);
        descrizioneArea.setDisable(true);
    }

    private void setAndDisableEdizioneFields(Edizione edizione){
        if(edizione == null) return;

        numeroEdizioneField.setText(String.valueOf(edizione.getId_edizione()));
        editoreField.setText(edizione.getEditore());
        annoEdizioneField.setText(String.valueOf(edizione.getAnnoPubblicazione()));
        isbnField.setText(edizione.getIsbn());

        numeroEdizioneField.setDisable(true);
        editoreField.setDisable(true);
        annoEdizioneField.setDisable(true);
        isbnField.setDisable(true);
    }

    private Opera createOpera(){
        Opera opera = new Opera(
                Integer.parseInt(codiceOperaField.getText()),
                titoloField.getText(),
                autoreField.getText(),
                genereField.getText(),
                InputValidator.isNumber(annoField.getText()) ? Integer.parseInt(annoField.getText()) : 0,
                descrizioneArea.getText()
        );
        return opera;
    }

    private Edizione createEdizione(Opera opera){
        Edizione edizione = new Edizione(
                isbnField.getText(),
                InputValidator.isNumber(annoEdizioneField.getText()) ? Integer.parseInt(annoEdizioneField.getText()) : 0,
                editoreField.getText(),
                Integer.parseInt(numeroEdizioneField.getText()),
                opera,
                -1
        );
        return edizione;
    }

    private Volume createVolume(Edizione edizione){
        Volume volume = new Volume(
                -1,
                edizione,
                statoCombo.getValue(),
                posizioneField.getText()
        );
        return volume;
    }

    private void saveOpera(){
        if(!validateOperaFields()) {
            return;
        }
        try{
            Opera opera = createOpera();
            int result = OperaService.addOpera(opera);

            AlertUtil.showInfoAlert("Successo", "Opera aggiunta correttamente con id:" + result, "");

        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Errore", "Formato numerico non valido", "Inserisci un numero valido nei campi anno e numero edizione");
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Errore", "Impossibile salvare il libro", e.getMessage());
        }
    }

    private void saveEdizione(){
        if(!validateEdizioneFields()) {
            return;
        }
        try{
            Edizione edizione = createEdizione(createOpera());
            boolean result = EdizioneService.aggiungiEdizione(edizione);

            AlertUtil.showInfoAlert("Successo", "Edizione aggiunta correttamente", "");

        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Errore", "Formato numerico non valido", "Inserisci un numero valido nei campi anno e numero edizione");
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Errore", "Impossibile salvare il libro", e.getMessage());
        }
    }

    private void saveVolume() {
        if (!validateAllFields()) {
            return;
        }
        try {
            Volume volume = createVolume(createEdizione(createOpera()));
            boolean result = VolumeService.aggiungiVolume(volume);

            AlertUtil.showInfoAlert("Successo", "Libro aggiunto correttamente", "");

        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Errore", "Formato numerico non valido", "Inserisci un numero valido nei campi anno e numero edizione");
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Errore", "Impossibile salvare il libro", e.getMessage());
        }
    }

    private boolean validateOperaFields() {
        boolean isValid = true;

        if (!InputValidator.validateNotEmpty(codiceOperaField, "Codice opera")) isValid = false;
        if (!InputValidator.validateNotEmpty(titoloField, "Titolo")) isValid = false;
        if (!InputValidator.validateNotEmpty(autoreField, "Autore")) isValid = false;
        if (!InputValidator.validateNotEmpty(genereField, "Genere")) isValid = false;
        if (!InputValidator.validateNotEmpty(annoField, "anno di pubblicazione opera")) isValid = false;
        if (isValid && !InputValidator.validateInteger(annoField, "anno di pubblicazione dell'opera")) isValid = false;
        if (!InputValidator.validateNotEmpty(descrizioneArea, "Descrizione")) isValid = false;

        return isValid;
    }

    private boolean validateEdizioneFields() {
        boolean isValid = true;

        if (!InputValidator.validateNotEmpty(numeroEdizioneField, "numero edizione")) isValid = false;
        if (!InputValidator.validateInteger(numeroEdizioneField, "numero edizione")) isValid = false;
        if (!InputValidator.validateNotEmpty(editoreField, "editore")) isValid = false;
        if (!InputValidator.validateNotEmpty(annoEdizioneField, "anno di pubblicazione dell'edizione")) isValid = false;
        if(isValid && !InputValidator.validateInteger(annoEdizioneField, "anno di pubblicazione dell'edizione")) isValid = false;
        if (!InputValidator.validateNotEmpty(isbnField, "ISBN")) isValid = false;

        return isValid;
    }

    private boolean validateAllFields() {
        boolean isValid = true;

        isValid &= validateOperaFields();
        isValid &= validateEdizioneFields();

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