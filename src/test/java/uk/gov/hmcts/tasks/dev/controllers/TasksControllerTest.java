package uk.gov.hmcts.tasks.dev.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.tasks.dev.controllers.controllers.TasksController;
import uk.gov.hmcts.tasks.dev.controllers.entities.Status;
import uk.gov.hmcts.tasks.dev.controllers.entities.Task;
import uk.gov.hmcts.tasks.dev.controllers.mapper.TaskMapper;
import uk.gov.hmcts.tasks.dev.controllers.models.TaskRequest;
import uk.gov.hmcts.tasks.dev.controllers.models.TaskResponse;
import uk.gov.hmcts.tasks.dev.controllers.services.TasksService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TasksControllerTest {

    @Mock
    private TasksService tasksService;

    @InjectMocks
    private TasksController tasksController;

    @Mock
    private TaskMapper taskMapper;

    Task task;

    List<Task> tasks;


    @BeforeEach
    void setUp() {
        tasks = createTasks();
    }

    private List<Task> createTasks() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Test task");
        task.setDescription("Description");
        task.setStatus(Status.TODO);
        task.setDueDate(LocalDate.now());
        tasks = List.of(task);
        return tasks;
    }

    @Test
    @DisplayName("Should create a task")
    void createTask() {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("Test task");
        taskRequest.setDescription("Description");
        taskRequest.setStatus(Status.COMPLETED);
        taskRequest.setDueDate(LocalDate.now());


        when(tasksService.create(taskRequest)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(new TaskResponse());


        ResponseEntity<TaskResponse> taskResponse = tasksController.create(taskRequest);

        assertEquals(HttpStatus.CREATED, taskResponse.getStatusCode());
        assertNotNull(taskResponse.getBody());
        assertEquals(taskMapper.toResponse(task), taskResponse.getBody());
    }

    @Test
    @DisplayName("Should get all tasks")
    void shouldGetAllTasks() {
        when(tasksService.findAll()).thenReturn(tasks);
        when(taskMapper.toResponse(task)).thenReturn(new TaskResponse());

        ResponseEntity<List<TaskResponse>> tasksResponse = tasksController.getAllTasks();

        assertTrue(tasksResponse.getStatusCode().is2xxSuccessful());
        assertEquals(HttpStatus.OK, tasksResponse.getStatusCode());
        assertEquals(List.of(taskMapper.toResponse(task)), tasksResponse.getBody());
    }

    @Test
    @DisplayName("Should get a task by ID")
    void shouldGetTaskById() {
        when(tasksService.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(new TaskResponse());

        ResponseEntity<TaskResponse> taskResponse = tasksController.byId(task.getId());

        assertTrue(taskResponse.getStatusCode().is2xxSuccessful());
        assertEquals(HttpStatus.OK, taskResponse.getStatusCode());
        assertEquals(taskMapper.toResponse(task), taskResponse.getBody());
    }


    @Test
    @DisplayName("Should update task Status")
    void shouldUpdateTaskStatus() {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setStatus(Status.COMPLETED);

        when(tasksService.findById(task.getId())).thenReturn(Optional.ofNullable(task));
        when(tasksService.update(task)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(new TaskResponse());

        ResponseEntity<TaskResponse> taskResponse = tasksController.partialUpdate(1L, taskRequest);

        assertEquals(taskMapper.toResponse(task), taskResponse.getBody());

        verify(tasksService, times(1)).update(task);
    }

    @Test
    @DisplayName("Should delete a task")
    void shouldDeleteTask() {
        doNothing().when(tasksService).delete(task);
        when(tasksService.findById(task.getId())).thenReturn(Optional.ofNullable(task));

        ResponseEntity<Void> response = tasksController.delete(1L);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(tasksService, times(1)).delete(task);
    }

    @Test
    @DisplayName("Should not delete a task as no tasks to delete")
    void shouldNotDeleteTaskAsNoTasksToDelete() {
        when(tasksService.findById(task.getId())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = tasksController.delete(1L);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(tasksService, never()).delete(task);
        verify(tasksService, times(1)).findById(1L);
    }
}
