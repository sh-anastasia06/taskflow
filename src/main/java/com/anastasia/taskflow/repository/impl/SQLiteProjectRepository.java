package com.anastasia.taskflow.repository.impl;

import com.anastasia.taskflow.model.Priority;
import com.anastasia.taskflow.model.Project;
import com.anastasia.taskflow.repository.ProjectRepository;
import com.anastasia.taskflow.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SQLiteProjectRepository implements ProjectRepository {
    private final Connection connection = DatabaseManager.getInstance().getConnection();

    @Override
    public Project save(Project project) {
        String sql = "INSERT INTO projects (id, name, description, is_archived, priority, deadline, created_at)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, project.getId().toString());
            stmt.setString(2, project.getName());
            stmt.setString(3, project.getDescription());
            stmt.setInt(4, project.isArchived() ? 1 : 0);
            stmt.setString(5, project.getPriority().name());
            stmt.setString(6, formatDate(project.getDeadline()));
            stmt.setString(7, project.getCreatedAt().toString());
            stmt.executeUpdate();
            return project;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save a project", e);
        }
    }

    @Override
    public Optional<Project> getById(UUID id) {
        String sql = "SELECT * FROM projects WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get a project", e);
        }
    }

    @Override
    public List<Project> getAll() {
        String sql = "SELECT * FROM projects ORDER BY created_at DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<Project> projects = new ArrayList<>();
            while (rs.next()) {
                projects.add(mapRow(rs));
            }
            return projects;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get projects", e);
        }
    }

    @Override
    public void update(Project project) {
        String sql = "UPDATE projects SET name = ?, description = ?, is_archived = ?, priority = ?, deadline = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setInt(3, project.isArchived() ? 1 : 0);
            stmt.setString(4, project.getPriority().name());
            stmt.setString(5, formatDate(project.getDeadline()));
            stmt.setString(6, project.getId().toString());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update a project", e);
        }
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete a project", e);
        }
    }

    private Project mapRow(ResultSet rs) throws SQLException {
        return new Project(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("is_archived") == 1,
                Priority.valueOf(rs.getString("priority")),
                rs.getString("deadline") != null ? LocalDate.parse(rs.getString("deadline")) : null,
                LocalDateTime.parse(rs.getString("created_at"))
        );
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.toString() : null;
    }
}
