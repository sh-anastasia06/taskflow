package com.anastasia.taskflow.controller;

import com.anastasia.taskflow.TaskFlowApplication;
import com.anastasia.taskflow.model.Project;
import com.anastasia.taskflow.service.ProjectService;
import com.anastasia.taskflow.service.TaskService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
        loadProjects();
    }

    private void loadProjects() {
        projectListView.getItems().setAll(projectService.getAll());
    }

    @FXML
    private void handleCreateProject() {
        System.out.println("create project clicked");
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
