module com.anastasia.taskflow {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens com.anastasia.taskflow to javafx.fxml;
    exports com.anastasia.taskflow;
}