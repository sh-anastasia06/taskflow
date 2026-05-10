package com.anastasia.taskflow.controller;

import com.anastasia.taskflow.model.Priority;
import com.anastasia.taskflow.model.Project;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.Optional;

public class ProjectDialogController {
    @FXML private HBox deleteBtnContainer;
    @FXML private Button deleteBtn;
    @FXML private Label deadlineError;
    @FXML private Label nameError;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox priorityComboBox;
    @FXML private DatePicker deadlinePicker;

    private Project existingProject;
    private boolean isNameValid = false;
    private boolean isDeadlineValid = true;

    private Runnable onDeleteCallback;

    @FXML
    public void initialize() {
        nameField.focusedProperty().addListener((obs, old, isNowFocused) -> {
            if (!isNowFocused) validateName();
        });

        deadlinePicker.focusedProperty().addListener((obs, old, isNowFocused) -> {
            if (!isNowFocused) validateDeadline();
        });

        priorityComboBox.getItems().addAll(Priority.values());
        priorityComboBox.getSelectionModel().select(Priority.MEDIUM);

        deleteBtn.setOnAction(e -> {
            if (onDeleteCallback != null) onDeleteCallback.run();
        });
    }

    public void setProject(Project project) {
        nameField.setText(project.getName());
        descriptionField.setText(project.getDescription());
        priorityComboBox.setValue(project.getPriority());
        deadlinePicker.setValue(project.getDeadline());

        deleteBtnContainer.setVisible(true);
        deleteBtnContainer.setManaged(true);

        existingProject = project;
    }

    public void setOnDelete(Runnable callback) {
        onDeleteCallback = callback;
    }

    public Optional<Project> getResult() {
        validateName();
        validateDeadline();
        if (!isNameValid || !isDeadlineValid) return Optional.empty();

        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        Priority priority = (Priority) priorityComboBox.getValue();
        LocalDate deadline = deadlinePicker.getValue();

        if (existingProject != null) {
            // edit mode
            existingProject.setName(name);
            existingProject.setDescription(description);
            existingProject.setPriority(priority);
            existingProject.setDeadline(deadline);
            return Optional.of(existingProject);
        } else  {
            return Optional.of(new Project(name, description, priority, deadline));
        }
    }

    private void validateName() {
        String value = nameField.getText();
        if (value == null || value.isBlank()) {
            nameError.setText("Project name is required");
            nameError.setVisible(true);
            nameError.setManaged(true);
            nameField.getStyleClass().add("field-error");
        } else {
            nameError.setVisible(false);
            nameError.setManaged(false);
            nameField.getStyleClass().remove("field-error");
            isNameValid = true;
        }
    }

    private void validateDeadline() {
        LocalDate value = deadlinePicker.getValue();
        LocalDate originalDeadline = existingProject != null ? existingProject.getDeadline() : null;

        boolean isPastAndChanged = value != null
                && value.isBefore(LocalDate.now())
                && !value.equals(originalDeadline);

        if (isPastAndChanged) {
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
