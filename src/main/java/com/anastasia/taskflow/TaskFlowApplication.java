package com.anastasia.taskflow;

import com.anastasia.taskflow.repository.ProjectRepository;
import com.anastasia.taskflow.repository.TaskRepository;
import com.anastasia.taskflow.repository.impl.SGLiteTaskRepository;
import com.anastasia.taskflow.repository.impl.SQLiteProjectRepository;
import com.anastasia.taskflow.service.ProjectService;
import com.anastasia.taskflow.service.TaskService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TaskFlowApplication extends Application {
    private static ProjectService projectService;
    private static TaskService taskService;

    public static ProjectService getProjectService() {
        return projectService;
    }

    public static TaskService getTaskService() {
        return taskService;
    }

    @Override
    public void init() {
        ProjectRepository projectRepository = new SQLiteProjectRepository();
        TaskRepository taskRepository = new SGLiteTaskRepository();
        projectService = new ProjectService(projectRepository);
        taskService = new TaskService(taskRepository, projectRepository);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                TaskFlowApplication.class.getResource("fxml/main-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 1024, 768);
        stage.setTitle("TaskFlow");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }
}
