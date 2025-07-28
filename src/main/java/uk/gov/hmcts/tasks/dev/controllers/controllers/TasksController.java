package uk.gov.hmcts.tasks.dev.controllers.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.tasks.dev.controllers.entities.Task;
import uk.gov.hmcts.tasks.dev.controllers.exception.ResourceNotFoundException;
import uk.gov.hmcts.tasks.dev.controllers.mapper.TaskMapper;
import uk.gov.hmcts.tasks.dev.controllers.models.TaskRequest;
import uk.gov.hmcts.tasks.dev.controllers.models.TaskResponse;
import uk.gov.hmcts.tasks.dev.controllers.services.TasksService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/tasks")
public class TasksController {

    private final TasksService tasksService;
    private final TaskMapper taskMapper;

    public TasksController(TasksService tasksService, TaskMapper taskMapper) {
        this.tasksService = tasksService;
        this.taskMapper = taskMapper;
    }

    @Operation(summary = "Get all tasks",
        description = "Retrieves a list of all tasks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "Successfully retrieved list"),
        @ApiResponse(responseCode = "500",
            description = "Internal Server Error")
    })
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> taskResponseList = this.tasksService
            .findAll().stream()
            .map(taskMapper::toResponse).toList();
        return ResponseEntity.ok(taskResponseList);
    }

    @Operation(summary = "Get a task by ID",
        description = "Retrieves a task by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "Successfully retrieved task"),
        @ApiResponse(responseCode = "404",
            description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> byId(@PathVariable("id") Long id) {
        Optional<Task> taskById = this.tasksService.findById(id);
        return taskById.map(task ->
                                ResponseEntity.ok().body(taskMapper.toResponse(task)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new task",
        description = "Creates a new task with the current timestamp as due date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
            description = "Task created successfully"),
        @ApiResponse(responseCode = "404",
            description = "Unexpected error (should not occur)")
    })
    @PostMapping
    public ResponseEntity<TaskResponse> create(@RequestBody TaskRequest taskRequest) {
        Optional<Task> tasks = tasksService.create(taskRequest);
        return tasks.map(task ->
                             ResponseEntity.created(URI.create(String.valueOf(task.getId())))
                                 .body(taskMapper.toResponse(task)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Operation(summary = "Delete a task by ID",
        description = "Deletes a task by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204",
            description = "Task deleted successfully"),
        @ApiResponse(responseCode = "404",
            description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        if (tasksService.findById(id).isPresent()) {
            tasksService.delete(tasksService.findById(id).get());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


    @Operation(summary = "Partially update a task by ID",
        description = "Updates specific fields of a task identified by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "Task partially updated successfully"),
        @ApiResponse(responseCode = "204",
            description = "No content (should not occur with current logic)"),
        @ApiResponse(responseCode = "404",
            description = "Task not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> partialUpdate(@PathVariable Long id,
                                                      @RequestBody TaskRequest patchTaskRequest) {
        Optional<Task> existingTask = tasksService.findById(id);

        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            task.setStatus(patchTaskRequest.getStatus());
            tasksService.update(task).orElseThrow(
                () -> new ResourceNotFoundException("Task not found"));
            return ResponseEntity.ok().body(taskMapper.toResponse(task));
        }
        return ResponseEntity.notFound().build();
    }
}
