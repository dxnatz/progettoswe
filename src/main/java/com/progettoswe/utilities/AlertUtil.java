package com.progettoswe.utilities;

import javafx.scene.control.Alert;

public class AlertUtil {

    public static Alert showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
        return alert;
    }

    /**
     * To use when you want to check which button was pressed as you need to call showAndWait() later
     * @param title
     * @param header
     * @param content
     * @param showAndWait
     * @return
     */
    public static Alert showErrorAlert(String title, String header, String content, boolean showAndWait) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        if(showAndWait) alert.showAndWait();
        return alert;
    }

    public static Alert showInfoAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
        return alert;
    }

        /**
     * To use when you want to check which button was pressed as you need to call showAndWait() later
     * @param title
     * @param header
     * @param content
     * @param showAndWait
     * @return
     */
    public static Alert showInfoAlert(String title, String header, String content, boolean showAndWait) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        if(showAndWait) alert.showAndWait();
        return alert;
    }

    public static Alert showWarningAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
        return alert;
    }

        /**
     * To use when you want to check which button was pressed as you need to call showAndWait() later
     * @param title
     * @param header
     * @param content
     * @param showAndWait
     * @return
     */
    public static Alert showWarningAlert(String title, String header, String content, boolean showAndWait) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        if(showAndWait) alert.showAndWait();
        return alert;
    }

    public static Alert showConfirmationAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
        return alert;
    }

        /**
     * To use when you want to check which button was pressed as you need to call showAndWait() later
     * @param title
     * @param header
     * @param content
     * @param showAndWait
     * @return
     */
    public static Alert showConfirmationAlert(String title, String header, String content, boolean showAndWait) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        if(showAndWait) alert.showAndWait();
        return alert;
    }

        //TODO: da fare nuovi alert con button custom come quelli di prenotaLibro su HomePageController

}
