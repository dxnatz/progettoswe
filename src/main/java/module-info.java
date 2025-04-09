module com.progettoswe {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;
    requires java.desktop;

    opens com.progettoswe.controller to javafx.fxml;
    exports com.progettoswe;
    exports com.progettoswe.model;

}
