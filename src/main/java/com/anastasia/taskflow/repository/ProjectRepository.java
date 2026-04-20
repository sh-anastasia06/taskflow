package com.anastasia.taskflow.repository;

import com.anastasia.taskflow.model.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> getById(UUID id);
    List<Project> getAll();
    void update(Project project);
    void delete(UUID id);
}
