package com.anastasia.taskflow.service;

import com.anastasia.taskflow.model.Priority;
import com.anastasia.taskflow.model.Project;
import com.anastasia.taskflow.model.Status;
import com.anastasia.taskflow.model.Task;
import com.anastasia.taskflow.repository.ProjectRepository;
import com.anastasia.taskflow.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // JUnit activates Mockito
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    // real class that's testing
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, projectRepository);
    }

    @Test
    void createTask_withValidData_savesAndReturnsTask() {
        // ARRANGE
        Project project = new Project("Test project", null, Priority.MEDIUM, null);
        UUID projectId = project.getId();

        // project exists in db
        when(projectRepository.getById(projectId))
                .thenReturn(Optional.of(project));

        // save() is called -> return task
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Task result = taskService.createTask(
                projectId, "Test task", "Some descr",
                Status.TODO, Priority.HIGH, null
        );

        // ASSERT
        assertNotNull(result);
        assertEquals("Test task", result.getTitle());
        assertEquals(Priority.HIGH, result.getPriority());

        // verify repo was called once and save() wasn't called
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_withNullProjectId_throwsException() {
        // ACT & ASSERT (no ARRANGE cuz there is nothing to set up)
        // verify that calling lambda throws an exception
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(
                        null, "Test task", null,
                        Status.TODO, Priority.HIGH, null
                )
        );

        // verify exception message
        assertEquals("Project id cannot be null", exception.getMessage());

        // verify repo wasn't touched
        verifyNoInteractions(taskRepository);
        verifyNoInteractions(projectRepository);
    }

    @Test
    void createTask_withUntrimmedTitle_savesTrimmedTitle() {
        // ARRANGE
        Project project = new Project("Test project", null, Priority.MEDIUM, null);
        UUID projectId = project.getId();

        when(projectRepository.getById(projectId))
                .thenReturn(Optional.of(project));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Task result = taskService.createTask(
                projectId, " Test task ", null,
                Status.TODO, Priority.HIGH, null
        );

        // ASSERT
        assertEquals("Test task", result.getTitle());
    }

    @Test
    void createTask_withNllPriority_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(
                        UUID.randomUUID(), "Test task", null,
                        Status.TODO, null, null
                )
        );

        assertEquals("Priority cannot be null", exception.getMessage());

        verifyNoInteractions(taskRepository);
        verifyNoInteractions(projectRepository);
    }

    @Test
    void createTask_withNonExistentProject_throwsException() {
        // ARRANGE
        UUID nonExistentId = UUID.randomUUID();
        when(projectRepository.getById(any())).thenReturn(Optional.empty());

        // ACT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(
                        nonExistentId, "Test task", null,
                        Status.TODO, Priority.HIGH, null
                )
        );

        // ASSERT
        assertEquals("Project not found", exception.getMessage());

        verifyNoInteractions(taskRepository);
    }

    @Test
    void createTask_withBlankTitle_throwsException(){
        // ARRANGE
        UUID projectId = UUID.randomUUID();

        // ACT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(
                        projectId, "", null,
                        Status.TODO, Priority.HIGH, null
                )
        );

        // ASSERT
        assertEquals("Task title cannot be empty", exception.getMessage());
        verifyNoInteractions(taskRepository);
        verifyNoInteractions(projectRepository);
    }
}
