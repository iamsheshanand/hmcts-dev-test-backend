package uk.gov.hmcts.tasks.dev.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.hmcts.tasks.dev.entities.Status;
import uk.gov.hmcts.tasks.dev.entities.Task;
import uk.gov.hmcts.tasks.dev.mapper.TaskMapper;
import uk.gov.hmcts.tasks.dev.models.TaskRequest;
import uk.gov.hmcts.tasks.dev.models.TaskResponse;
import uk.gov.hmcts.tasks.dev.services.TasksService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TasksController.class)
class TaskControllerTest {

    public static final String TASKS_ID = "/tasks/{id}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TasksService tasksService;

    @MockitoBean
    private TaskMapper taskMapper;

    @InjectMocks
    private TasksController tasksController;


    @Test
    @DisplayName("Should get tasks")
    public void shouldGetTasks() throws Exception {
        TaskResponse taskResponse = new TaskResponse();
        Task task = mock(Task.class);
        when(tasksService.findAll()).thenReturn(List.of(task));
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @DisplayName("Should get task by Id")
    public void shouldGetTaskById() throws Exception {
        Long taskId = 1L;
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(taskId);
        taskResponse.setTitle("Title");
        Task task = mock(Task.class);

        when(tasksService.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(TASKS_ID, taskId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$.id").value(taskId))
            .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    @DisplayName("Should partially update task")
    public void shouldPartiallyUpdateTask() throws Exception {
        Long id = 1L;
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setStatus(Status.COMPLETED);
        TaskResponse taskResponse = new TaskResponse();
        Task task = mock(Task.class);
        when(tasksService.findById(id)).thenReturn(Optional.of(task));
        when(task.getStatus()).thenReturn(Status.COMPLETED);
        when(tasksService.update(task)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch(TASKS_ID, id)
                            .contentType("application/json")
                            .content("{\"status\": \"COMPLETED\"}")) // Adjust JSON based on TaskRequest
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").exists());
    }

    @Test
    @DisplayName("Should delete task")
    public void shouldDeleteTask() throws Exception {
        Long id = 1L;
        Task task = mock(Task.class);
        when(tasksService.findById(id)).thenReturn(Optional.of(task));

        mockMvc.perform(MockMvcRequestBuilders.delete(TASKS_ID, id))
            .andExpect(status().isNoContent());
    }
}
