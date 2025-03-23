package com.progettoswe.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Pattern;

import com.progettoswe.App;
import com.progettoswe.business_logic.UserService;
import com.progettoswe.model.Session;
import com.progettoswe.model.Utente;
import com.progettoswe.utilities.AlertUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;


public class RegistrationController {

    @FXML private TextField nomeTextField;
    @FXML private TextField cognomeTextField;
    @FXML private TextField codiceFiscaleTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField passwordTextField;
    @FXML private TextField cellulareTextField;
    @FXML private DatePicker dataNascitaPicker;
    @FXML private TextField indirizzoTextField;
    @FXML private Button registerButton;
    private Utente utente;

    //Espressioni di valdizaione dei campi
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String CF_REGEX = "^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$";
    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[-_+@$!%*?&]).{5,}$";
    private static final String PHONE_REGEX = "^\\+?[0-9]{8,15}$";

    @FXML
    private void switchToLogin() {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegistration() {
        String nome = nomeTextField.getText();
        String cognome = cognomeTextField.getText();
        String codiceFiscale = codiceFiscaleTextField.getText();
        String mail = emailTextField.getText();
        String password = passwordTextField.getText();
        String cellulare = cellulareTextField.getText();
        LocalDate dataNascita = dataNascitaPicker.getValue();
        String indirizzo = indirizzoTextField.getText();

        utente = new Utente(nome, cognome, codiceFiscale, mail, password, cellulare, dataNascita, indirizzo);
        registraUtente(nome, cognome, codiceFiscale, mail, password, cellulare, dataNascita, indirizzo);
    }

    @FXML
    private void handleBack() {
        switchToLogin();
    }

    //Metodi di validazione
    private boolean isValidEmail(String email){
        return Pattern.matches(EMAIL_REGEX, email);
    }
    private boolean isValidCf(String cf){
        return Pattern.matches(CF_REGEX, cf);
    }
    private boolean isValidPassword(String password){
        return Pattern.matches(PASSWORD_REGEX, password);
    }
    private boolean isValidPhone(String phone){
        return Pattern.matches(PHONE_REGEX, phone);
    }
    private boolean isValidDate(LocalDate dataNascita) {
        LocalDate today = LocalDate.now(); // Data attuale
        LocalDate limiteMinimo = today.minusYears(100); // Limite minimo (massimo 100 anni)
        LocalDate limiteMassimo = today.minusYears(10); // Limite massimo (almeno 10 anni)
    
        return !dataNascita.isAfter(limiteMassimo) && !dataNascita.isBefore(limiteMinimo); //verifica se rientra tra 10 e 100 anni
    }

    //metodo per registrare un nuovo utente nel database
    private boolean registraUtente(String nome, String cognome, String cf, String email, String password, String cellulare, LocalDate dataNascita, String indirizzo) {

        //controllo se tutte le informazioni sono state inserite
        if(nome.isEmpty() || cognome.isEmpty() || cf.isEmpty() || email.isEmpty() || password.isEmpty() || cellulare.isEmpty() || dataNascita == null || indirizzo.isEmpty()) {
            AlertUtil.showWarningAlert("Registrazione fallita", 
            "Devi inserire tutte le informazioni richieste", 
            "Non ci possono essere campi vuoti");
            return false;
        }

        if(email.endsWith(Session.ADMIN_EMAIL)) {
            AlertUtil.showErrorAlert("Registrazione fallita",
                                    "Non puoi registrarti con questa email",
                                    "Questa email è riservata agli amministratori");
            emailTextField.clear();
            return false;
        }

        //verifiche di validità dei campi
        if(!isValidCf(cf)){
            AlertUtil.showErrorAlert("Registrazione fallita", 
                                    "Codice fiscale non valido", 
                                    "Il codice fiscale deve essere composto da 16 caratteri alfanumerici.");
            codiceFiscaleTextField.clear();
            return false;
        }
        if(!isValidEmail(email)){
            AlertUtil.showErrorAlert("Registrazione fallita", 
                                    "Email non valida", 
                                    "Inserisci un'email valida");
            emailTextField.clear();
            return false;
        }
        if(!isValidPassword(password)){
            AlertUtil.showErrorAlert("Registrazione fallita", 
                                    "Password non valida", 
                                    "La password deve contenere almeno 5 caratteri, un numero e un carattere speciale.");
            passwordTextField.clear();
            return false;
        }
        if(!isValidPhone(cellulare)){
            AlertUtil.showErrorAlert("Registrazione fallita", 
                                    "Numero di cellulare non valido", 
                                    "Inserisci un numero di cellulare valido");
            cellulareTextField.clear();
            return false;
        }
        if(!isValidDate(dataNascita)){
            AlertUtil.showErrorAlert("Registrazione fallita", 
                                    "Data di nascita non valida", 
                                    "Devi avere almeno 10 anni e non più di 100");
            dataNascitaPicker.getEditor().clear();
            return false;
        }
        
        //verifico se l'email è già presente nel database
        if(UserService.emailEsistente(email)) {
            AlertUtil.showErrorAlert("Registrazione fallita", 
                                    "Questa email non può essere utilizzata", 
                                    "È gia presente nel nostro database");
            emailTextField.clear();
            return false;
        }

        //verifico se il codice fiscale è già presente nel database
        if(UserService.cfEsistente(cf)) {
            AlertUtil.showErrorAlert("Registrazione fallita", 
                                    "Questo codice fiscale non può essere utilizzato", 
                                    "È gia presente nel nostro database");
            codiceFiscaleTextField.clear();
            return false;
        }

        //verifico se il numero di cellulare è già presente nel database
        if(UserService.cellulareEsistente(cellulare)) {
            AlertUtil.showErrorAlert("Registrazione fallita", 
                                    "Questo numero di cellulare non può essere utilizzato", 
                                    "È gia presente nel nostro database");
            cellulareTextField.clear();
            return false;
        }

        if(UserService.inserimentoUtente(utente)) {
            AlertUtil.showInfoAlert("Registrazione avvenuta con successo", 
                                    "Benvenuto in BookStore", 
                                    "Ora puoi effettuare il login");
            switchToLogin();
            return true; //se l'utente è stato registrato con successo
        }else{
            AlertUtil.showErrorAlert("Registrazione fallita", 
                                    "Errore durante la registrazione", 
                                    "Riprova più tardi");
            return false; //se non è stato possibile registrare l'utente a causa di un errore
        }
    }

}