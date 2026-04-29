package com.anastasia.taskflow.controller;

import com.anastasia.taskflow.TaskFlowApplication;
import com.anastasia.taskflow.model.Project;
import com.anastasia.taskflow.service.ProjectService;
import com.anastasia.taskflow.service.TaskService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.DataInput;
import java.io.IOException;
import java.util.Optional;

public class MainController {
    @FXML private Button createProjectBtn;
    @FXML private VBox welcomePane;
    @FXML private ListView<Project> projectListView;
    @FXML private HBox kanbanBoard;

    private ProjectService projectService;
    private TaskService taskService;

    @FXML
    public void initialize() {
        projectService = TaskFlowApplication.getProjectService();
        taskService = TaskFlowApplication.getTaskService();
        projectListView.setMaxWidth(Double.MAX_VALUE);
        projectListView.setCellFactory(listView -> new ListCell<Project>() {
            @Override
            protected void updateItem(Project project, boolean empty) {
                super.updateItem(project, empty);
                if (empty || project == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(project.getName());
                    setStyle("");
                }
            }
        });

        loadProjects();
    }

    private void loadProjects() {
        projectListView.getItems().setAll(projectService.getAll());
    }

    @FXML
    private void handleCreateProject() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/com/anastasia/taskflow/fxml/project-dialog.fxml"));
            Parent root = loader.load();

            Dialog<Project> dialog = new Dialog<>();
            dialog.setTitle("New Project");
            dialog.getDialogPane().setContent(root);
            dialog.getDialogPane().getStyleClass().add("project-dialog");
            dialog.getDialogPane().getButtonTypes().addAll(
                    ButtonType.OK,
                    ButtonType.CANCEL
            );
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/anastasia/taskflow/css/styles.css").toExternalForm());

            Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            okButton.addEventFilter(ActionEvent.ACTION, event -> {
                ProjectDialogController controller = loader.getController();
                if (controller.getResult().isEmpty()) {
                    event.consume();
                }
            });

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    ProjectDialogController controller = loader.getController();
                    return controller.getResult().orElse(null);
                }
                return null;
            });

            Optional<Project> result = dialog.showAndWait();
            result.ifPresent(project -> {
                projectService.createProject(
                        project.getName(),
                        project.getDescription(),
                        project.getPriority(),
                        project.getDeadline()
                );
                loadProjects();
            });
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to open dialog: " + e.getMessage()).show();
        }
    }

    public void handleSettings(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings");
        alert.setHeaderText("Settings");
        alert.setContentText("No settings available yet.");
        alert.showAndWait();
    }

    public void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About TaskFlow");
        alert.setHeaderText("TaskFlow v1.0");
        alert.setContentText("A task management app built with JavaFX and SQLite.");
        alert.showAndWait();
    }
}