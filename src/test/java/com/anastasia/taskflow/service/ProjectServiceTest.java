package com.anastasia.taskflow.service;

import com.anastasia.taskflow.model.Priority;
import com.anastasia.taskflow.model.Project;
import com.anastasia.taskflow.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepository);
    }

    @Test
    void createProject_withValidData_savesAndReturnsProject() {
        // ARRANGE
        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Project result = projectService.createProject(
                "Test project", null,
                Priority.HIGH, null
        );

        // ASSERT
        assertNotNull(result);
        assertEquals("Test project", result.getName());
        assertEquals(Priority.HIGH, result.getPriority());

        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void createProject_withBlankName_throwsException() {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> projectService.createProject(
                        "", null,
                        Priority.HIGH, null
                )
        );

        assertEquals("Project name cannot be null", exception.getMessage());
        verifyNoInteractions(projectRepository);
    }

    @Test
    void createProject_withUntrimmedTitle_savesTrimmedTitle() {
        // ARRANGE
        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // ACT
        Project result = projectService.createProject(
                " Test project ", null,
                Priority.HIGH, null
        );

        // ASSERT
        assertEquals("Test project", result.getName());
    }

    @Test
    void createProject_withNullPriority_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> projectService.createProject(
                        "Test project", null,
                        null, null
                )
        );

        assertEquals("Priority cannot be null", exception.getMessage());
        verifyNoInteractions(projectRepository);
    }

    @Test
    void getById_withNullId_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> projectService.getById(null)
        );

        assertEquals("Project id cannot be null", exception.getMessage());
        verifyNoInteractions(projectRepository);
    }

    @Test
    void getById_withValidId_returnsProject() {
        // ARRANGE
        Project project = new Project("Test project", null, Priority.HIGH, null);
        UUID projectId = project.getId();

        when(projectRepository.getById(eq(projectId)))
                .thenReturn(Optional.of(project));

        // ACT
        Optional<Project> result = projectService.getById(projectId);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals("Test project", result.get().getName());
    }

    @Test
    void updateProject_withNullProject_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> projectService.updateProject(null)
        );

        assertEquals("Project cannot be null", exception.getMessage());
        verifyNoInteractions(projectRepository);
    }

    @Test
    void updateProject_withBlankName_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> projectService.updateProject(
                        new Project("", null, Priority.HIGH, null)
                )
        );

        assertEquals("Project name cannot be null", exception.getMessage());
        verifyNoInteractions(projectRepository);
    }

    @Test
    void updateProject_withUntrimmedName_savesTrimmedName() {
        // ARRANGE
        Project project = new Project(" Test project ", null, Priority.HIGH, null);

        // ACT
        projectService.updateProject(project);

        // ASSERT
        assertEquals("Test project", project.getName());
        verify(projectRepository, times(1)).update(any(Project.class));
    }

    @Test
    void deleteProject_withNullId_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> projectService.deleteProject(null)
        );

        assertEquals("Project id cannot be null", exception.getMessage());
        verifyNoInteractions(projectRepository);
    }

    @Test
    void deleteProject_withValidId_callsRepository() {
        // ARRANGE
        Project project = new Project("Test project", null, Priority.HIGH, null);
        UUID projectId = project.getId();

        // ACT
        projectService.deleteProject(projectId);

        // ASSERT
        verify(projectRepository, times(1)).delete(eq(projectId));
    }

    @Test
    void getAllProjects_returnsListFromRepository() {
        // ARRANGE
        Project project1 = new Project("Test project 1", null, Priority.HIGH, null);
        Project project2 = new Project("Test project 2", null, Priority.HIGH, null);

        when(projectRepository.getAll())
                .thenReturn(List.of(project1, project2));

        // ACT
        List<Project> result = projectService.getAll();

        // ASSERT
        assertEquals(2, result.size());
        verify(projectRepository, times(1)).getAll();
    }
}
