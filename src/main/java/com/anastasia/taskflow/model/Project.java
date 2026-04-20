package com.anastasia.taskflow.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Project {
    private final UUID id;
    private String name;
    private String description;
    private boolean isArchived;
    private Priority priority;
    private LocalDate deadline;
    private final LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", isArchived=" + isArchived +
                ", priority=" + priority +
                ", deadline=" + deadline +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Project(String name, String description, Priority priority, LocalDate deadline) {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.isArchived = false;
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
    }

//    Reconstructing from DB
    public Project(UUID id, String name, String description, boolean isArchived,
                   Priority priority, LocalDate deadline, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isArchived = isArchived;
        this.priority = priority;
        this.deadline = deadline;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
