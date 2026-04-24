package com.anastasia.taskflow.repository.impl;

import com.anastasia.taskflow.model.Priority;
import com.anastasia.taskflow.model.Status;
import com.anastasia.taskflow.model.Task;
import com.anastasia.taskflow.repository.TaskRepository;
import com.anastasia.taskflow.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SGLiteTaskRepository implements TaskRepository {
    private final Connection connection = DatabaseManager.getInstance().getConnection();

    @Override
    public Task save(Task task) {
        String sql = "INSERT INTO tasks (id, project_id, title, description, status, priority, deadline, created_at)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, task.getId().toString());
            stmt.setString(2, task.getProjectId().toString());
            stmt.setString(3, task.getTitle());
            stmt.setString(4, task.getDescription());
            stmt.setString(5, task.getStatus().name());
            stmt.setString(6, task.getPriority().name());
            stmt.setString(7, formatDate(task.getDeadline()));
            stmt.setString(8, task.getCreatedAt().toString());
            stmt.executeUpdate();
            return task;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save a task", e);
        }
    }

    @Override
    public Optional<Task> getById(UUID id) {
        String sql = "SELECT id, project_id, title, description, status, priority, deadline, created_at FROM tasks WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return Optional.of(mapRow(rs));
                return Optional.empty(); // task is not found
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get a task", e);
        }
    }

    @Override
    public List<Task> getAll() {
        String sql = "SELECT * FROM tasks ORDER BY created_at DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<Task> tasks = new ArrayList<>();
            while (rs.next()) {
                tasks.add(mapRow(rs));
            }
            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get tasks", e);
        }
    }

    @Override
    public List<Task> getAllProjectTasks(UUID projectId) {
        String sql = "SELECT * FROM tasks WHERE project_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, projectId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                List<Task> tasks = new ArrayList<>();
                while (rs.next()) {
                    tasks.add(mapRow(rs));
                }
                return tasks;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get project's tasks", e);
        }
    }

    @Override
    public void update(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, status = ?, priority = ?, deadline = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus().name());
            stmt.setString(4, task.getPriority().name());
            stmt.setString(5, formatDate(task.getDeadline()));
            stmt.setString(6, task.getId().toString());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("failed to update a task", e);
        }
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete a task", e);
        }
    }

//    converts row into a Task object
    private Task mapRow(ResultSet rs) throws SQLException {
        return new Task(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("project_id")),
                rs.getString("title"),
                rs.getString("description"),
                Status.valueOf(rs.getString("status")),
                Priority.valueOf(rs.getString("priority")),
                rs.getString("deadline") != null ? LocalDate.parse(rs.getString("deadline")) : null,
                LocalDateTime.parse(rs.getString("created_at"))
        );
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.toString() : null;
    }
}
