module com.progettoswe {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;

    opens com.progettoswe to javafx.fxml;
    exports com.progettoswe;
}
