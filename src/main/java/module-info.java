module com.anastasia.taskflow {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.sql;


    opens com.anastasia.taskflow to javafx.fxml;
    opens com.anastasia.taskflow.controller to javafx.fxml;

    exports com.anastasia.taskflow;
}