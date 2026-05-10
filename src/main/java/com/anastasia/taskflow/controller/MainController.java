package com.anastasia.taskflow.controller;

import com.anastasia.taskflow.TaskFlowApplication;
import com.anastasia.taskflow.model.Priority;
import com.anastasia.taskflow.model.Project;
import com.anastasia.taskflow.model.Status;
import com.anastasia.taskflow.model.Task;
import com.anastasia.taskflow.service.ProjectService;
import com.anastasia.taskflow.service.TaskService;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.Ikonli;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.DataInput;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MainController {
    @FXML private VBox projectView;
    @FXML private HBox projectHeader;
    @FXML private Label projectNameLabel;
    @FXML private ToggleButton showCancelledBtn;
    @FXML private Button editProjectBtn;
    @FXML private Button createProjectBtn;
    @FXML private VBox welcomePane;
    @FXML private ListView<Project> projectListView;
    @FXML private HBox kanbanBoard;

    private ProjectService projectService;
    private TaskService taskService;

    private ChangeListener<Boolean> cancelledToggleListener;

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
                    setCursor(Cursor.DEFAULT);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(project.getName());
                    setCursor(Cursor.HAND);
                    setStyle("");
                }
            }
        });

        projectListView.getSelectionModel().selectedItemProperty()
                        .addListener((obs, oldProject, newProject) -> {
                            if (newProject != null) {
                                handleProjectSelected(newProject);
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

    private void handleEditProject(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/com/anastasia/taskflow/fxml/project-dialog.fxml"));
            Parent root = loader.load();

            ProjectDialogController controller = loader.getController();
            controller.setProject(project);

            Dialog<Project> dialog = new Dialog<>();
            dialog.setTitle("Edit Project");
            dialog.getDialogPane().setContent(root);
            dialog.getDialogPane().getStyleClass().add("project-dialog");
            dialog.getDialogPane().getButtonTypes().addAll(
                    ButtonType.OK,
                    ButtonType.CANCEL
            );
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/anastasia/taskflow/css/styles.css").toExternalForm());

            Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            okButton.addEventFilter(ActionEvent.ACTION, event -> {
                if (controller.getResult().isEmpty()) {
                    event.consume();
                }
            });

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    return controller.getResult().orElse(null);
                }
                return null;
            });

            controller.setOnDelete(() -> {
                projectService.deleteProject(project.getId());
                dialog.close();
                loadProjects();
                welcomePane.setVisible(true);
                projectView.setVisible(false);
            });

            Optional<Project> result = dialog.showAndWait();
            result.ifPresent(updatedProject -> {
                projectService.updateProject(updatedProject);
                loadProjects();
                loadKanbanBoard(project);
            });
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to open dialog: " + e.getMessage()).show();
        }
    }

    private void handleProjectSelected(Project project) {
        welcomePane.setVisible(false);
        projectView.setVisible(true);
        loadKanbanBoard(project);
    }

    private void handleCreateTask(Project project, Status status) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/com/anastasia/taskflow/fxml/task-dialog.fxml"));
            Parent root = loader.load();

            Dialog<Task> dialog = new Dialog<>();
            dialog.setTitle("New Task");
            dialog.getDialogPane().setContent(root);
            dialog.getDialogPane().getStyleClass().add("task-dialog");
            dialog.getDialogPane().getButtonTypes().addAll(
                    ButtonType.OK,
                    ButtonType.CANCEL
            );
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/anastasia/taskflow/css/styles.css").toExternalForm());

            Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            okButton.addEventFilter(ActionEvent.ACTION, event -> {
                TaskDialogController controller = loader.getController();
                if (controller.getResult(project.getId(), status).isEmpty()) {
                    event.consume();
                }
            });

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    TaskDialogController controller = loader.getController();
                    return controller.getResult(project.getId(), status).orElse(null);
                }
                return null;
            });

            Optional<Task> result = dialog.showAndWait();
            result.ifPresent(task -> {
                taskService.createTask(
                        project.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        status,
                        task.getPriority(),
                        task.getDeadline()
                );
                loadKanbanBoard(project);
            });
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to open dialog: " + e.getMessage()).showAndWait();
        }
    }

    private void handleEditTask(Task task, Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/com/anastasia/taskflow/fxml/task-dialog.fxml"));
            Parent root = loader.load();

            TaskDialogController controller = loader.getController();
            controller.setTask(task);

            Dialog<Task> dialog = new Dialog<>();
            dialog.setTitle("Edit Task");
            dialog.getDialogPane().setContent(root);
            dialog.getDialogPane().getStyleClass().add("task-dialog");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.getDialogPane().getStylesheets().add(
                    getClass().getResource("/com/anastasia/taskflow/css/styles.css").toExternalForm()
            );

            controller.setOnDelete(() -> {
                taskService.deleteTask(task.getId());
                dialog.close();
                loadKanbanBoard(project);
            });

            Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            okButton.addEventFilter(ActionEvent.ACTION, event -> {
                if (controller.getResult(null, null).isEmpty()) { // null cuz edit mode ignores these
                    event.consume();
                }
            });

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    return controller.getResult(null, null).orElse(null);
                }
                return null;
            });

            Optional<Task> result = dialog.showAndWait();
            result.ifPresent(updatedTask -> {
                taskService.updateTask(updatedTask);
                loadKanbanBoard(project);
            });
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to open dialog: " + e.getMessage()).showAndWait();
        }
    }

    private void loadKanbanBoard(Project project) {
        projectNameLabel.setText(project.getName());

        editProjectBtn.setOnAction(e -> handleEditProject(project));

        kanbanBoard.getChildren().clear();
        kanbanBoard.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(kanbanBoard, javafx.scene.layout.Priority.ALWAYS);

        List<Task> tasks = taskService.getProjectsTasks(project.getId());

        List<Task> todoTasks = tasks.stream()
                .filter(t -> t.getStatus() == Status.TODO)
                .toList();
        List<Task> inProgressTasks = tasks.stream()
                .filter(t -> t.getStatus() == Status.IN_PROGRESS)
                .toList();
        List<Task> doneTasks = tasks.stream()
                .filter(t -> t.getStatus() == Status.DONE)
                .toList();

        VBox todoCol = createColumn("TO DO", Status.TODO, todoTasks, project);
        VBox progressCol = createColumn("IN PROGRESS", Status.IN_PROGRESS, inProgressTasks, project);
        VBox doneCol = createColumn("DONE", Status.DONE, doneTasks, project);

        HBox.setHgrow(todoCol, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(progressCol, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(doneCol, javafx.scene.layout.Priority.ALWAYS);

        // сброс предпочитаемой ширины => HBox делится на 3 части
        todoCol.setPrefWidth(0);
        progressCol.setPrefWidth(0);
        doneCol.setPrefWidth(0);

        kanbanBoard.getChildren().addAll(todoCol, progressCol, doneCol);

        showCancelledBtn.setSelected(false);
        showCancelledBtn.setText("Show Cancelled");

        if (cancelledToggleListener != null) {
            showCancelledBtn.selectedProperty().removeListener(cancelledToggleListener);
        }

         cancelledToggleListener = ((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                List<Task> cancelledTasks = tasks.stream()
                        .filter(t -> t.getStatus() == Status.CANCELLED)
                        .toList();
                VBox cancelledCol = createColumn("CANCELLED", Status.CANCELLED, cancelledTasks, project);
                HBox.setHgrow(cancelledCol, javafx.scene.layout.Priority.ALWAYS);
                cancelledCol.setPrefWidth(0);
                kanbanBoard.getChildren().add(cancelledCol);
                showCancelledBtn.setText("Hide Cancelled");
            } else {
                kanbanBoard.getChildren().removeIf(node ->
                        node.getUserData() != null && node.getUserData().equals("CANCELLED")
                );
                showCancelledBtn.setText("Show Cancelled");
            }
        });

        showCancelledBtn.selectedProperty().addListener(cancelledToggleListener);
    }

    private VBox createColumn(String title, Status status, List<Task> tasks, Project project) {
        VBox column = new VBox(10);
        column.getStyleClass().add("kanban-column");
        column.setMinWidth(170);
        column.setMaxWidth(Double.MAX_VALUE);
        column.setPadding(new Insets(10));

        if (status == Status.CANCELLED) {
            column.setUserData("CANCELLED");
        }

        Label columnTitle = new Label(title);
        columnTitle.getStyleClass().add("column-title");

        VBox cardsContainer = new VBox(10);
        cardsContainer.setPadding(new Insets(5, 14, 5, 5));
        cardsContainer.getStyleClass().add("cards-container");

        tasks.forEach(task -> {
            HBox taskCard = createTaskCard(task, project);
            cardsContainer.getChildren().add(taskCard);
        });

        ScrollPane scrollPane = new ScrollPane(cardsContainer);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("column-scroll-pane");
        scrollPane.setFitToWidth(true);

        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

        Button addTaskBtn = new Button("+ Add task");
        addTaskBtn.getStyleClass().add("add-task-btn");
        addTaskBtn.setMaxWidth(Double.MAX_VALUE);
        addTaskBtn.setOnAction(e -> handleCreateTask(project, status));

        column.getChildren().setAll(columnTitle, scrollPane, addTaskBtn);
        column.setAlignment(Pos.TOP_CENTER);

        return column;
    }

    private HBox createTaskCard(Task task, Project project) {
        HBox card = new HBox(8);
        card.setPrefHeight(Region.USE_PREF_SIZE);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-background-color: rgba(0,0,0,0.15)");
        card.getStyleClass().add("task-card");

        Rectangle priority = new Rectangle();
        priority.setWidth(5);
        priority.heightProperty().bind(card.heightProperty());
        priority.setFill(Color.web(getPriorityColor(task.getPriority())));

        VBox cardContent = new VBox(6);
        cardContent.setPadding(new Insets(8));
        HBox.setHgrow(cardContent, javafx.scene.layout.Priority.ALWAYS);

        Label title = new Label(task.getTitle());
        title.getStyleClass().add("task-title");
        title.setWrapText(true);

        Separator line = new Separator(Orientation.HORIZONTAL);

        Label descr = new Label(task.getDescription());
        descr.getStyleClass().add("task-descr");
        descr.setWrapText(true);
        descr.setMaxHeight(60);

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_LEFT);

        FontIcon clockIcon = new FontIcon("far-clock");
        clockIcon.setIconSize(16);
        clockIcon.getStyleClass().add("icon-gray");

        String deadlineText = task.getDeadline() != null ? task.getDeadline().toString() : "No deadline";
        Label deadline = new Label(deadlineText);
        deadline.getStyleClass().add("task-deadline");

        if(task.getDeadline() == null) {
            clockIcon.setVisible(false);
            deadline.setVisible(false);
        }

        if (task.getDeadline() != null && task.getDeadline().isBefore(LocalDate.now())
            && task.getStatus() != Status.DONE) {
            clockIcon.getStyleClass().add("overdue");
        }

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, javafx.scene.layout.Priority.ALWAYS);

        FontIcon editIcon = new FontIcon("far-edit");
        editIcon.getStyleClass().add("icon");
        editIcon.setIconSize(16);

        Button editBtn = new Button();
        editBtn.setGraphic(editIcon);
        editBtn.getStyleClass().add("edit-button");
        editBtn.setOnAction(e -> handleEditTask(task, project));

        footer.getChildren().addAll(clockIcon, deadline, footerSpacer, editBtn);

        cardContent.getChildren().addAll(title, line, descr, spacer, footer);
        card.getChildren().addAll(priority, cardContent);

        return card;
    }

    private String getPriorityColor(Priority priority) {
        return switch (priority) {
            case Priority.CRITICAL -> "#8B5CF6";
            case Priority.HIGH -> "#DB6060";
            case Priority.MEDIUM -> "#F59E0B";
            default -> "#22C55E";
        };
    }

    public void handleSettings() {
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