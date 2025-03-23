package com.progettoswe.controller;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

import com.progettoswe.App;
import com.progettoswe.business_logic.UserService;
import com.progettoswe.model.Session;
import com.progettoswe.model.Utente;
import com.progettoswe.utilities.AlertUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ProfileController {
    @FXML private TextField nome;
    @FXML private TextField cognome;
    @FXML private TextField email;
    @FXML private PasswordField vecchiaPassword;
    @FXML private PasswordField password;
    @FXML private PasswordField confermaPassword;

    // Espressioni di validazione dei campi
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[-_+@$!%*?&]).{5,}$";

    @FXML
    public void initialize() {
        // Carica le informazioni dell'utente nella pagina del profilo
        Utente utente = Session.getUtente();
        nome.setText(utente.getNome());
        cognome.setText(utente.getCognome());
        email.setText(utente.getEmail());
        password.clear();
        confermaPassword.clear();
        vecchiaPassword.clear();
    }

    @FXML
    private void salvaInformazioni() {
        String nuovoNome = nome.getText();
        String nuovoCognome = cognome.getText();
        String nuovaEmail = email.getText();
        String vecchiaPasswordInput = vecchiaPassword.getText();
        String nuovaPassword = password.getText();
        String confermaNuovaPassword = confermaPassword.getText();

        Utente utente = Session.getUtente();

        if (!nuovaPassword.isEmpty() || !confermaNuovaPassword.isEmpty()) {
            // Verifica la vecchia password solo se l'utente ha inserito una nuova password
            if (!UserService.login(utente.getEmail(), vecchiaPasswordInput)) {
                AlertUtil.showErrorAlert("Errore", 
                                        "Password non corretta", 
                                        "La password inserita non è corretta.");
                return;
            }

            if (!nuovaPassword.equals(confermaNuovaPassword)) {
                AlertUtil.showErrorAlert("Errore", 
                                        "Password non corrispondenti", 
                                        "Le password non corrispondono.");
                return;
            }

            if (!isValidPassword(nuovaPassword)) {
                AlertUtil.showErrorAlert("Errore", 
                                        "Password non valida", 
                                        "La password deve contenere almeno 5 caratteri, un numero e un carattere speciale.");
                return;
            }

            // Aggiorna la password solo se è stata inserita una nuova password
            utente.setPassword(nuovaPassword);
        }

        if (!isValidEmail(nuovaEmail)) {
            AlertUtil.showErrorAlert("Errore", 
                                    "Email non valida", 
                                    "Inserisci un'email valida.");
            return;
        }

        if (!nuovaEmail.equals(utente.getEmail()) && UserService.emailEsistente(nuovaEmail)) {
            AlertUtil.showErrorAlert("Errore", 
                                    "Email già esistente", 
                                    "L'email inserita è già associata ad un altro account.");
            return;
        }

        // Aggiorna le informazioni dell'utente con i nuovi valori nella sessione
        utente.setNome(nuovoNome);
        utente.setCognome(nuovoCognome);
        utente.setEmail(nuovaEmail);

        // Salva le informazioni aggiornate nel database
        if (UserService.updateUtente(utente)) {
            // Aggiorna l'oggetto Utente nella sessione
            Session.setUtente(utente);

            AlertUtil.showInfoAlert("Successo", 
                                    "Informazioni aggiornate", 
                                    "Le informazioni sono state aggiornate con successo.");
            initialize();
        } else {
            AlertUtil.showErrorAlert("Errore", 
                                    "Errore durante l'aggiornamento", 
                                    "Si è verificato un errore durante l'aggiornamento delle informazioni.");
        }
    }

    //TODO: Usare metodi AlertUtil con button custom quando ci sono
    @FXML
    private void cancellaUtente() throws IOException {
        Alert confirmAlert = AlertUtil.showConfirmationAlert("Conferma", 
                                                            "Cancellazione account", 
                                                            "Sei sicuro di voler cancellare il tuo account?", false);

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Utente utente = Session.getUtente();
            if (UserService.deleteUtente(utente)) {
                AlertUtil.showInfoAlert("Successo", 
                                        "Account cancellato", 
                                        "Il tuo account è stato cancellato con successo.");
                Session.setUserEmail(null);
                Session.setUtente(null);
                App.setRoot("login");
            } else {
                AlertUtil.showErrorAlert("Errore", 
                                        "Errore durante la cancellazione", 
                                        "Si è verificato un errore durante la cancellazione dell'account.");
            }
        }
    }

    @FXML
    private void logout() {
        Session.setUserEmail(null);
        Session.setUtente(null);
        try {
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void tornaIndietro() {
        try {
            App.setRoot("homepage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    private boolean isValidPassword(String password) {
        return Pattern.matches(PASSWORD_REGEX, password);
    }
}
