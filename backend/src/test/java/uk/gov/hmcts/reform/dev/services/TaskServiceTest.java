package uk.gov.hmcts.reform.dev.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.dev.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Fix production bug");
        sampleTask.setDescription("Critical bug in service");
        sampleTask.setStatus(TaskStatus.TODO);
        sampleTask.setDueDateTime(LocalDateTime.now().plusDays(1));
        sampleTask.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createTask_shouldSaveAndReturnTask() {
        CreateTaskRequest request = new CreateTaskRequest(
            "Fix production bug", "Critical bug in service",
            TaskStatus.TODO, LocalDateTime.now().plusDays(1)
        );
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        Task result = taskService.createTask(request);

        assertNotNull(result);
        assertEquals("Fix production bug", result.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getTaskById_shouldReturnTask_whenFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        Task result = taskService.getTaskById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Fix production bug", result.getTitle());
    }

    @Test
    void getTaskById_shouldThrow_whenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(99L));
    }

    @Test
    void getAllTasks_shouldReturnAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(sampleTask));

        List<Task> result = taskService.getAllTasks();

        assertEquals(1, result.size());
        assertEquals(sampleTask.getId(), result.get(0).getId());
    }

    @Test
    void updateTaskStatus_shouldUpdateStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        sampleTask.setStatus(TaskStatus.DONE);
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        Task result = taskService.updateTaskStatus(1L, TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, result.getStatus());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTaskStatus_shouldThrow_whenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskStatus(99L, TaskStatus.DONE));
    }

    @Test
    void deleteTask_shouldDelete_whenTaskExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_shouldThrow_whenTaskNotFound() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(99L));
    }
}
