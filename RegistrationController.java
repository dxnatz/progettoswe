package com.progettoswe;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Pattern;
import java.sql.Date;


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

        registraUtente(nome, cognome, codiceFiscale, mail, password, cellulare, dataNascita, indirizzo);
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

    //metodo per verificare se l'email è già presente nel database
    private static boolean emailEsistente(String email) {
        String query = "SELECT count (*) FROM utente WHERE email = ?"; //query per contare il numero di email uguali a quella inserita

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email); // imposto il parametro della query, sostituisco il ? con l'email

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; //email trovata
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; //email non trovata
    }

    //metodo per verificare se il codice fiscale è già presente nel database
    private static boolean cfEsistente(String cf) {
        String query = "SELECT count (*) FROM utente WHERE cf = ?"; //query per contare il numero di codici fiscali uguali a quello inserito

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, cf); // imposto il parametro della query, sostituisco il ? con il codice fiscale

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; //codice fiscale trovato
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; //codice fiscale non trovato
    }

    //metodo per verificare se il cellulare è già presente nel database
    private static boolean cellulareEsistente(String cellulare) {
        String query = "SELECT count (*) FROM utente WHERE cellulare = ?"; //query per contare il numero di numeri di cellulare uguali a quello inserito

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, cellulare); // imposto il parametro della query, sostituisco il ? con il numero di cellulare

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; //numero di cellulare trovato
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; //numero di cellulare non trovato
    }

    //metodo per registrare un nuovo utente nel database
    private boolean registraUtente(String nome, String cognome, String cf, String email, String password, String cellulare, LocalDate dataNascita, String indirizzo) {

        //controllo se tutte le informazioni sono state inserite
        if(nome.isEmpty() || cognome.isEmpty() || cf.isEmpty() || email.isEmpty() || password.isEmpty() || cellulare.isEmpty() || dataNascita == null || indirizzo.isEmpty()) {
            Alert a = new Alert(AlertType.INFORMATION, "Devi inserire tutte le informazioni richieste\n\nNon ci possono essere campi vuoti");
            a.setHeaderText("Registrazione fallita");
            a.setTitle("Errore nella registrazione");
            a.showAndWait();
            return false;

        }/*else if(email.length() < 5 || email.length() > 50 || email.contains(" ") || password.contains(" ") || password.length() < 5) { //controllo se email e password sono valide
            Alert a = new Alert(AlertType.ERROR, "Email o password non validi\n\nL'email deve essere tra i 5 e i 50 caratteri\n\nLa password deve essere lunga almeno 5 caratteri\n\nNella email e nella password non devono essere presenti spazi");
            a.setHeaderText("Informazioni mancanti");
            a.setTitle("Errore nella registrazione");
            a.showAndWait();
            return false;
        }*/

        //verifiche di validità dei campi
        if(!isValidEmail(email)){
            Alert a = new Alert(AlertType.ERROR, "Email non valida\n\nL'email deve essere valida e non deve contenere spazi (es. esempio@prova.com).");
            a.setHeaderText("Registrazione fallita");
            a.setTitle("Email non valida");
            a.showAndWait();
            emailTextField.clear();
            return false;
        }
        if(!isValidCf(cf)){
            Alert a = new Alert(AlertType.ERROR, "Codice fiscale non valido\n\nIl codice fiscale deve essere conforme e non deve contenere spazi.");
            a.setHeaderText("Registrazione fallita");
            a.setTitle("Codice fiscale non valido");
            a.showAndWait();
            codiceFiscaleTextField.clear();
            return false;
        }
        if(!isValidPassword(password)){
            Alert a = new Alert(AlertType.ERROR, "Password non valida\n\nLa password deve contenere almeno 5 caratteri, un numero e un carattere speciale (_,-,+,@,$,!,%,*,?,&).");
            a.setHeaderText("Registrazione fallita");
            a.setTitle("Password non valida");
            a.showAndWait();
            passwordTextField.clear();
            return false;
        }
        if(!isValidPhone(cellulare)){
            Alert a = new Alert(AlertType.ERROR, "Numero di cellulare non valido\n\nIl numero di cellulare deve contenere tra le 8 e le 15 cifre e non deve contenere spazi.");
            a.setHeaderText("Registrazione fallita");
            a.setTitle("Numero di cellulare non valido");
            a.showAndWait();
            cellulareTextField.clear();
            return false;
        }
        if(!isValidDate(dataNascita)){
            Alert a = new Alert(AlertType.ERROR, "Data di nascita non valida\n\nL'età dell'utente deve essere compresa tra i 10 e i 100 anni.");
            a.setHeaderText("Registrazione fallita");
            a.setTitle("Data di nascita non valida");
            a.showAndWait();
            dataNascitaPicker.getEditor().clear();
            return false;
        }

        //verifico se l'email è già presente nel database
        if(emailEsistente(email)) {
            Alert a = new Alert(AlertType.ERROR, "Questa email è già stata utilizzata");
            a.setHeaderText("Registrazione fallita");
            a.setTitle("Errore nella registrazione");
            a.showAndWait();
            emailTextField.clear();
            return false;
        }

        //verifico se il codice fiscale è già presente nel database
        if(cfEsistente(cf)) {
            Alert a = new Alert(AlertType.ERROR, "Questo codice fiscale non può essere utilizzato\n\nÈ gia presente nel nostro database");
            a.setHeaderText("Registrazione fallita");
            a.setTitle("Errore nella registrazione");
            a.showAndWait();
            codiceFiscaleTextField.clear();
            return false;
        }

        //verifico se il numero di cellulare è già presente nel database
        if(cellulareEsistente(cellulare)) {
            Alert a = new Alert(AlertType.ERROR, "Questo numero di cellulare non può essere utilizzato\n\nÈ gia presente nel nostro database");
            a.setHeaderText("Registrazione fallita");
            a.setTitle("Errore nella registrazione");
            a.showAndWait();
            cellulareTextField.clear();
            return false;
        }

        //query per inserire un nuovo utente nel database
        String query = "INSERT INTO utente (nome, cognome, cf, email, pw, cellulare, data_nascita, indirizzo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            //imposto i parametri della query
            Date sqlDate = Date.valueOf(dataNascita);
            pstmt.setString(1, nome);
            pstmt.setString(2, cognome);
            pstmt.setString(3, cf);
            pstmt.setString(4, email);
            pstmt.setString(5, password);
            pstmt.setString(6, cellulare);
            pstmt.setDate(7, sqlDate);
            pstmt.setString(8, indirizzo);

            int rowsInserted = pstmt.executeUpdate(); //eseguo la query
            Alert a = new Alert(AlertType.INFORMATION, "Ti sei registrato con successo, verrai reindirizzato alla pagina di login");
            a.setHeaderText("Registrazione avvenuta con successo");
            a.setTitle("Registrazione avvenuta");
            a.showAndWait();
            switchToLogin();
            return rowsInserted > 0; //restituisce true se l'utente è stato registrato con successo

        } catch (SQLException e) {
            e.printStackTrace();
        }
        Alert a = new Alert(AlertType.ERROR, "Errore non previsto nella registrazione");
        a.setHeaderText("Registrazione fallita");
        a.setTitle("Errore nella registrazione");
        a.showAndWait();
        return false; //se non è stato possibile registrare l'utente a causa di un errore
    }

}