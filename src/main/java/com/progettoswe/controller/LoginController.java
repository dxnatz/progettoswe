package com.progettoswe.controller;

import com.progettoswe.utilities.InputValidator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;
import com.progettoswe.App;
import com.progettoswe.model.Session;
import com.progettoswe.business_logic.UserService;


public class LoginController {

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button loginButton;

    @FXML
    private void switchToRegistrate() throws IOException {
        App.setRoot("registrate");
    }

    @FXML
    private void switchToHomePage() throws IOException {
        App.setRoot("homePage");
    }

    @FXML
    private void switchToOpUser() throws IOException {
        App.setRoot("op_user");
    }

    @FXML
    private void handleLogin() {
        String mail = emailTextField.getText();
        String password = passwordTextField.getText();
        Session.setUserEmail(mail);
        authenticate(mail, password);
    }

    private void loginByEmailType(String email) throws IOException {
        if(!email.endsWith(Session.ADMIN_EMAIL)){
            switchToHomePage();
        }else{
            switchToOpUser();
        }
    }

    private void authenticate(String email, String password) {

        //TODO: serve solo per testing, togliere dopo
        if(email.equals("admin")){
            try {
                switchToOpUser();
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (!InputValidator.validateNotEmpty(emailTextField, "Email")
        || !InputValidator.validateNotEmpty(passwordTextField, "Password")) {
            return;
        }

        if(UserService.login(email, password)) {
            try {
                loginByEmailType(email);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(UserService.checkPassword(email, password)) {
            Alert a = new Alert(AlertType.ERROR, "La password inserta è errata");
            a.setHeaderText("Errore di accesso");
            a.setTitle("Errore durante l'accesso");
            a.showAndWait();
            passwordTextField.clear();
        }else{
            Alert a = new Alert(AlertType.ERROR, "L'email e la password non sono corretti\n\nSe non sei ancora registrato, fallo ora!");
            a.setHeaderText("Errore di accesso");
            a.setTitle("Errore durante l'accesso");
            a.showAndWait();
            emailTextField.clear();
            passwordTextField.clear();
        }
    }

}