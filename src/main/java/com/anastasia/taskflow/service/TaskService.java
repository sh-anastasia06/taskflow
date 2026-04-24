package com.anastasia.taskflow.service;

import com.anastasia.taskflow.model.Priority;
import com.anastasia.taskflow.model.Task;
import com.anastasia.taskflow.repository.ProjectRepository;
import com.anastasia.taskflow.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public Task createTask(UUID projectId, String title, String description, Priority priority, LocalDate deadline) {
        if (projectId == null) throw new IllegalArgumentException("Project id cannot be null");

        if (title == null || title.isBlank()) throw new IllegalArgumentException("Task title cannot be empty");
        String trimmedTitle = title.trim();

        if (priority == null) throw new IllegalArgumentException("Priority cannot be null");

        projectRepository.getById(projectId).orElseThrow(
                () -> new IllegalArgumentException("Project not found")
        );

        Task task =  new Task(projectId, trimmedTitle,
                description != null ? description.trim() : null,
                priority, deadline);
        return taskRepository.save(task);
    }

    public Optional<Task> getByID(UUID id) {
        if (id == null) throw new IllegalArgumentException("Task id cannot be null");

        return taskRepository.getById(id);
    }

    public List<Task> getAll() {
        return taskRepository.getAll();
    }

    public List<Task> getProjectsTasks(UUID projectId) {
        if (projectId == null) throw new IllegalArgumentException("Project id cannot be null");

        return taskRepository.getAllProjectTasks(projectId);
    }

    public void updateTask(Task task) {
        if (task == null) throw new IllegalArgumentException("Task cannot be null");

        String trimmedTitle = task.getTitle() != null ? task.getTitle().trim() : null;
        if (trimmedTitle == null || trimmedTitle.isBlank()) throw new IllegalArgumentException("Task title cannot be empty");
        task.setTitle(trimmedTitle);

        taskRepository.update(task);
    }

    public void deleteTask(UUID id) {
        if (id == null) throw new IllegalArgumentException("Task id cannot be null");

        taskRepository.delete(id);
    }
}
