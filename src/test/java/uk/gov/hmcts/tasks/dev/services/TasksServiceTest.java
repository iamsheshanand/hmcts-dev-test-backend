package uk.gov.hmcts.tasks.dev.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.tasks.dev.entities.Status;
import uk.gov.hmcts.tasks.dev.entities.Task;
import uk.gov.hmcts.tasks.dev.mapper.TaskMapper;
import uk.gov.hmcts.tasks.dev.models.TaskRequest;
import uk.gov.hmcts.tasks.dev.repository.TasksRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TasksServiceTest {

    @Mock
    private TasksRepository tasksRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TasksService tasksService;

    @Test
    @DisplayName("Should find all tasks")
    void shouldFindAllTasks() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setStatus(Status.TODO);
        task.setDueDate(LocalDate.now());

        when(tasksRepository.findAll()).thenReturn(List.of(task));

        List<Task> tasks = tasksService.findAll();

        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());

        verify(tasksRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find task by Id")
    void shouldFindTaskById() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setStatus(Status.TODO);
        task.setDueDate(LocalDate.now());

        when(tasksRepository.findById(1L)).thenReturn(Optional.of(task));

        Optional<Task> taskOptional = tasksService.findById(1L);

        assertTrue(taskOptional.isPresent());
        assertEquals("Test Task", taskOptional.get().getTitle());
        verify(tasksRepository, times(1)).findById(1L);

    }

    @Test
    @DisplayName("Should return empty when no task by Id found")
    void shouldReturnEmptyWhenTaskNotFound() {
        when(tasksRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Task> taskOptional = tasksService.findById(1L);

        assertTrue(taskOptional.isEmpty());
        verify(tasksRepository, times(1)).findById(1L);
    }

    @Test
    void shouldCreateTask() {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        taskRequest.setDescription("Test Description");
        taskRequest.setStatus(Status.TODO);
        taskRequest.setDueDate(LocalDate.now());

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(Status.TODO);
        task.setDueDate(LocalDate.now());

        when(taskMapper.toEntity(taskRequest)).thenReturn(task);
        when(tasksRepository.save(task)).thenReturn(task);

        Optional<Task> taskCreated = tasksService.create(taskRequest);

        assertTrue(taskCreated.isPresent());
        assertEquals("Test Task", taskCreated.get().getTitle());
        verify(tasksRepository, times(1)).save(task);
    }

    @Test
    @DisplayName("Should update task")
    void shouldUpdateTask() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(Status.IN_PROGRESS);
        task.setDueDate(LocalDate.now());

        when(tasksRepository.save(task)).thenReturn(task);

        Optional<Task> updatedTask = tasksService.update(task);

        assertTrue(updatedTask.isPresent());
        assertEquals("Test Task", updatedTask.get().getTitle());
        verify(tasksRepository, times(1)).save(task);

    }

    @Test
    @DisplayName("Should delete task")
    void shouldDeleteTask() {
        Task task = new Task();
        task.setId(1L);

        doNothing().when(tasksRepository).delete(task);

        tasksService.delete(task);

        verify(tasksRepository, times(1)).delete(task);

    }


}
