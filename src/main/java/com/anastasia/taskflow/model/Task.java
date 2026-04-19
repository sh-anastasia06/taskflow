package com.anastasia.taskflow.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Task {
    private final UUID id;
    private final UUID projectId;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private LocalDate deadline;
    private final LocalDateTime createdAt;

    public Task(UUID projectId, String title, String description, Priority priority) {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.status = Status.TODO;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

//    Reconstructing from DB
    public Task(UUID id, UUID projectId, String title, String description,
                Status status, Priority priority, LocalDate deadline, LocalDateTime createdAt) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.deadline = deadline;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public UUID getProjectId() {
        return projectId;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                '}';
    }
}
