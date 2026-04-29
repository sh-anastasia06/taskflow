package com.anastasia.taskflow.controller;

import com.anastasia.taskflow.model.Priority;
import com.anastasia.taskflow.model.Project;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.Optional;

public class ProjectDialogController {
    @FXML private Label deadlineError;
    @FXML private Label nameError;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox priorityComboBox;
    @FXML private DatePicker deadlinePicker;

    private boolean isNameValid = false;
    private boolean isDeadlineValid = true;

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
    }

    public Optional<Project> getResult() {
        validateName();
        validateDeadline();
        if (!isNameValid || !isDeadlineValid) return Optional.empty();

        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        Priority priority = (Priority) priorityComboBox.getValue();
        LocalDate deadline = deadlinePicker.getValue();

        return Optional.of(new Project(name, description, priority, deadline));
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
