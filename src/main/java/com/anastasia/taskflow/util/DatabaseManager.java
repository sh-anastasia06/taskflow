package com.anastasia.taskflow.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String CREATE_PROJECTS_TABLE = """
        CREATE TABLE IF NOT EXISTS projects (
            id          TEXT PRIMARY KEY,
            name        TEXT NOT NULL,
            description TEXT,
            is_archived INTEGER NOT NULL DEFAULT 0,
            priority    TEXT NOT NULL,
            deadline    TEXT,
            created_at  TEXT NOT NULL
        )""";

    private static final String CREATE_TASKS_TABLE = """
        CREATE TABLE IF NOT EXISTS tasks (
            id          TEXT PRIMARY KEY,
            project_id  TEXT NOT NULL,
            title       TEXT NOT NULL,
            description TEXT,
            status      TEXT NOT NULL,
            priority    TEXT NOT NULL,
            deadline    TEXT,
            created_at  TEXT NOT NULL,
            FOREIGN KEY (project_id) REFERENCES projects(id)
        )""";

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:taskflow.db");
            initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }

    }

    public Connection getConnection() {
        return connection;
    }

    private void initializeDatabase() throws SQLException {
        connection.createStatement().execute(CREATE_PROJECTS_TABLE);
        connection.createStatement().execute(CREATE_TASKS_TABLE);
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
}
