package com.anastasia.taskflow.repository;

import com.anastasia.taskflow.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {
    Task save(Task task);     // returns saved task
    Optional<Task> getById(UUID id);
    List<Task> getAll();
    List<Task> getAllProjectTasks(UUID projectId);
    void update(Task task);
    void delete(UUID id);
}
