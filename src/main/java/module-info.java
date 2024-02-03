module com.example.help {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.commons.net;

    requires jBCrypt;

    opens com.example.logimmo to javafx.fxml;
    exports com.example.logimmo;
}