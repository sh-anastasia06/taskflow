package com.anastasia.taskflow.controller;

import com.anastasia.taskflow.model.Priority;
import com.anastasia.taskflow.model.Status;
import com.anastasia.taskflow.model.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class TaskDialogController {
    @FXML public VBox statusContainer;
    @FXML private Button deleteBtn;
    @FXML private HBox deleteBtnContainer;
    @FXML private ComboBox<Status> statusComboBox;
    @FXML private TextField titleField;
    @FXML private Label titleError;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<Priority> priorityComboBox;
    @FXML private DatePicker deadlinePicker;
    @FXML private Label deadlineError;

    private Task existingTask; // null => create mode, not null => edit mode
    private boolean isTitleValid = false;
    private boolean isDeadlineValid = false;

    private Runnable onDeleteCallback;
    
    @FXML
    public void initialize() {
        titleField.focusedProperty().addListener((obs, old, isNowFocused) ->{
            if (!isNowFocused) validateTitle();
        });

        deadlinePicker.focusedProperty().addListener((obs, old, isNowFocused) -> {
            if (!isNowFocused) validateDeadline();
        });

        priorityComboBox.getItems().addAll(Priority.values());
        priorityComboBox.getSelectionModel().select(Priority.MEDIUM);

        statusComboBox.getItems().addAll(Status.values());

        deleteBtn.setOnAction(e -> {
            if (onDeleteCallback != null) onDeleteCallback.run();
        });
    }

    public void setTask(Task task) {
        titleField.setText(task.getTitle());
        descriptionField.setText(task.getDescription());
        statusComboBox.setValue(task.getStatus());
        priorityComboBox.setValue(task.getPriority());
        deadlinePicker.setValue(task.getDeadline());

        statusContainer.setManaged(true);
        statusContainer.setVisible(true);
        deleteBtnContainer.setManaged(true);
        deleteBtnContainer.setVisible(true);

        existingTask = task;
    }

    public void setOnDelete(Runnable callback) {
        onDeleteCallback = callback;
    }

    public Optional<Task> getResult(UUID projectId, Status status) {
        validateTitle();
        validateDeadline();
        if (!isTitleValid || !isDeadlineValid) return Optional.empty();

        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        Priority priority = (Priority) priorityComboBox.getValue();
        LocalDate deadline = deadlinePicker.getValue();

        if (existingTask != null) {
            // edit mode: update the existing task's fields, keep its ID
            existingTask.setTitle(title);
            existingTask.setDescription(description);
            existingTask.setPriority(priority);
            existingTask.setDeadline(deadline);
            existingTask.setStatus(statusComboBox.getValue());
            return Optional.of(existingTask);
        } else {
            // create mode
            return Optional.of(new Task(projectId, title, description, status, priority, deadline));
        }
    }

    private void validateTitle() {
        String value = titleField.getText();
        if (value == null || value.isBlank()) {
            titleError.setText("Task title is required");
            titleError.setVisible(true);
            titleError.setManaged(true);
            titleField.getStyleClass().add("field-error");
        } else {
            titleError.setVisible(false);
            titleError.setManaged(false);
            titleField.getStyleClass().remove("field-error");
            isTitleValid = true;
        }
    }

    private void validateDeadline() {
        LocalDate value = deadlinePicker.getValue();
        if (value != null && value.isBefore(LocalDate.now())) {
            deadlineError.setText("Deadline cannot be in the past");
            deadlineError.setVisible(true);
            deadlineError.setManaged(true);
            deadlinePicker.getStyleClass().add("field-error");
            isDeadlineValid = false;
        } else {
            deadlineError.setVisible(false);
            deadlineError.setManaged(false);
            deadlinePicker.getStyleClass().remove("field-error");
            isDeadlineValid = true;
        }
    }
}
