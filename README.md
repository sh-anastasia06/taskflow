# TaskFlow

A desktop task management application built with JavaFX, featuring a Kanban-style board, project organisation, and a clean layered architecture backed by SQLite.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 24 |
| UI Framework | JavaFX 23 |
| Database | SQLite via JDBC |
| Build Tool | Maven |
| Icons | Ikonli (Font Awesome 5) |
| UI Components | ControlsFX |
| Testing | JUnit 5 |

---

## Features

- **Kanban Board** — Three active columns (To Do / In Progress / Done) with a toggleable Cancelled column
- **Projects** — Create, edit, and delete projects; navigate via sidebar
- **Tasks** — Full CRUD with title, description, priority, deadline, and status
- **Priority Indicators** — Colour-coded task cards (Critical / High / Medium / Low)
- **Overdue Detection** — Past-deadline labels highlighted in red on task cards
- **Dialogs** — Single reusable dialog for both create and edit modes (tasks and projects)
- **Settings & About** — Accessible from the main window

---

## Project Structure

```
com.anastasia.taskflow/
├── model/
│   ├── Task.java
│   ├── Project.java
│   ├── Status.java          # TODO, IN_PROGRESS, DONE, CANCELLED
│   └── Priority.java        # CRITICAL, HIGH, MEDIUM, LOW
├── repository/
│   ├── TaskRepository.java
│   ├── ProjectRepository.java
│   └── impl/
│       ├── SQLiteTaskRepository.java
│       └── SQLiteProjectRepository.java
├── service/
│   ├── TaskService.java
│   └── ProjectService.java
├── controller/
│   ├── MainController.java
│   ├── ProjectDialogController.java
│   └── TaskDialogController.java
├── util/
│   └── DatabaseManager.java
├── TaskFlowApplication.java
└── Launcher.java

resources/com/anastasia/taskflow/
├── fxml/
│   ├── main-view.fxml
│   ├── project-dialog.fxml
│   └── task-dialog.fxml
├── css/
│   ├── styles.css
│   └── colors.css
└── images/
    └── background.png
```

---

## Architecture

TaskFlow follows a classic **layered architecture**:

```
Controller  →  Service  →  Repository  →  SQLite
```

Key patterns used throughout the codebase:

- **Repository pattern** — interfaces (`TaskRepository`, `ProjectRepository`) with SQLite implementations, making storage swappable
- **Singleton** — `DatabaseManager` initialises the connection and auto-creates tables on first run
- **Constructor injection** — services receive their repository dependencies at construction time
- **Dialog pattern** — `FXMLLoader` + `Dialog<T>` + `resultConverter`; a single dialog handles both create and edit modes via `setTask(Task)` / `setProject(Project)`
- **Callback pattern** — `Runnable onDeleteCallback` threads delete actions from dialogs back to the controller
- **`Optional` returns** — repository layer uses `Optional<T>` for nullable lookups
- **Duplicate-listener guard** — `ChangeListener` stored as a field, removed before re-adding
- **Equal Kanban columns** — `HBox.setHgrow(ALWAYS)` + `setPrefWidth(0)`; `VBox.setVgrow(ALWAYS)` for scroll panes

---

## Getting Started

### Prerequisites

- Java 24+
- Maven 3.9+

### Run

```bash
mvn clean javafx:run
```

### Build a fat JAR

```bash
mvn clean package
java -jar target/taskflow-*.jar
```

> The SQLite database file is created automatically in the working directory on first launch.

---

## CSS Design Tokens

All colours and fonts are defined as CSS variables on `.root` in `colors.css`:

```css
-surfaces-color:          #FFFFFF;
-app-background-color:    #F7F8FA;
-sidebar-color:           #F0F1F5;
-border-color:            #E2E4E9;
-primary-text-color:      #1A1A2E;
-secondary-text-color:    #4A4A6A;
-muted-text-color:        #9898B0;
-primary-accent-color:    #91C4C8;
-done-accent-color:       #22C55E;
-warning-accent-color:    #F59E0B;
-critical-accent-color:   #EF4444;
-in-progress-kanban-color:#8B5CF6;
```

---

## Testing

```bash
mvn test
```

Tests are written with **JUnit 5** and cover the service layer.

---

## License

This project is licensed under the [MIT License](LICENSE).
