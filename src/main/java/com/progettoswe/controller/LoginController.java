package com.progettoswe.controller;

import java.io.IOException;

import com.progettoswe.App;
import com.progettoswe.business_logic.UserService;
import com.progettoswe.model.Session;
import com.progettoswe.utilities.AlertUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


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

    private boolean isThisUserEmail(String email){
        return !email.endsWith(Session.ADMIN_EMAIL);
    }

    private void authenticate(String email, String password) {

        if (email.isEmpty() || password.isEmpty()) {
            AlertUtil.showErrorAlert("Errore", "Campi vuoti", "Inserisci email e password");
            return;
        }

        if(UserService.login(email, password)) {
            try {
                if (isThisUserEmail(email)) {
                    switchToHomePage();
                }else{
                    switchToOpUser();
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(UserService.checkPassword(email, password)) {
            AlertUtil.showErrorAlert("Errore", "Password errata", "La password inserita non è corretta");
            passwordTextField.clear();
        }else{
            AlertUtil.showErrorAlert("Errore", "Email non registrata", "L'email inserita non è registrata");
            emailTextField.clear();
            passwordTextField.clear();
        }
    }

}