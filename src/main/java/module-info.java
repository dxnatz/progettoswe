module com.progettoswe {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.progettoswe to javafx.fxml;
    exports com.progettoswe;
}
