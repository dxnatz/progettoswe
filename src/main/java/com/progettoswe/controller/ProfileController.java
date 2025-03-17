package com.progettoswe.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.progettoswe.App;
import com.progettoswe.business_logic.UserService;
import com.progettoswe.model.Session;
import com.progettoswe.model.Utente;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Vecchia password errata");
                alert.setContentText("La vecchia password inserita non è corretta.");
                alert.showAndWait();
                return;
            }

            if (!nuovaPassword.equals(confermaNuovaPassword)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Le password non coincidono");
                alert.setContentText("Per favore, inserisci la stessa password in entrambi i campi.");
                alert.showAndWait();
                return;
            }

            if (!isValidPassword(nuovaPassword)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Password non valida");
                alert.setContentText("La password deve contenere almeno 5 caratteri, un numero e un carattere speciale (_,-,+,@,$,!,%,*,?,&).");
                alert.showAndWait();
                return;
            }

            // Aggiorna la password solo se è stata inserita una nuova password
            utente.setPassword(nuovaPassword);
        }

        if (!isValidEmail(nuovaEmail)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Email non valida");
            alert.setContentText("L'email deve essere valida e non deve contenere spazi (es. esempio@prova.com).");
            alert.showAndWait();
            return;
        }

        if (!nuovaEmail.equals(utente.getEmail()) && UserService.emailEsistente(nuovaEmail)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Email già utilizzata");
            alert.setContentText("Questa email è già stata utilizzata.");
            alert.showAndWait();
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

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successo");
            alert.setHeaderText("Informazioni aggiornate");
            alert.setContentText("Le tue informazioni sono state aggiornate con successo.");
            alert.showAndWait();
            initialize();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Errore durante l'aggiornamento");
            alert.setContentText("Si è verificato un errore durante l'aggiornamento delle informazioni.");
            alert.showAndWait();
        }
    }

    @FXML
    private void cancellaUtente() throws IOException {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Conferma Cancellazione");
        confirmAlert.setHeaderText("Sei sicuro di voler cancellare il tuo account?");
        confirmAlert.setContentText("Questa azione è irreversibile.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Utente utente = Session.getUtente();
            if (UserService.deleteUtente(utente)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Successo");
                alert.setHeaderText("Utente cancellato");
                alert.setContentText("Il tuo account è stato cancellato con successo.");
                alert.showAndWait();
                Session.setUserEmail(null);
                Session.setUtente(null);
                App.setRoot("login");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Errore durante la cancellazione");
                alert.setContentText("Si è verificato un errore durante la cancellazione dell'account.");
                alert.showAndWait();
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
