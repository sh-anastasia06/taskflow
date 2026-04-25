package com.anastasia.taskflow.service;

import com.anastasia.taskflow.model.Priority;
import com.anastasia.taskflow.model.Project;
import com.anastasia.taskflow.repository.ProjectRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProjectService {
    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public Project createProject(String name, String description, Priority priority, LocalDate deadline) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Project name cannot be null");
        String trimmedName = name.trim();
        if (priority == null) throw new IllegalArgumentException("Priority cannot be null");

        Project project = new Project(trimmedName,
                description != null ? description.trim() : null,
                priority, deadline);
        return repository.save(project);
    }

    public Optional<Project> getById(UUID id) {
        if (id == null) throw new IllegalArgumentException("Project id cannot be null");
        return repository.getById(id);
    }

    public List<Project> getAll() {
        return repository.getAll();
    }

    public void updateProject(Project project) {
        if (project == null) throw new IllegalArgumentException("Project cannot be null");

        String trimmedName = project.getName() != null ? project.getName().trim() : null;
        if (trimmedName == null || trimmedName.isBlank()) throw new IllegalArgumentException("Project name cannot be null");
        project.setName(trimmedName);

        repository.update(project);
    }

    public void setArchived(UUID id, boolean archived) {
        if (id == null) throw new IllegalArgumentException("Project id cannot be null");
        Project project = repository.getById(id).orElseThrow(
                () -> new IllegalArgumentException("Project not found")
        );
        project.setArchived(archived);
        repository.update(project);
    }

    public void deleteProject(UUID id) {
        if (id == null) throw new IllegalArgumentException("Project id cannot be null");
        repository.delete(id);
    }
}
