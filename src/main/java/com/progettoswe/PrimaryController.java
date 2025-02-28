package com.progettoswe;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class PrimaryController {

    @FXML
    private TextField primaryTextField;

    @FXML
    private Button primaryButton;

    @FXML
    private Label messageLabel;

    @FXML
    private void switchToSecondary() {
        // Logic to switch to the secondary view
    }

    @FXML
    private void handleButtonAction() {
        String inputText = primaryTextField.getText();
        messageLabel.setText("You entered: " + inputText);
    }
}