package uk.gov.hmcts.tasks.dev.controllers.services;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.tasks.dev.controllers.entities.Task;
import uk.gov.hmcts.tasks.dev.controllers.mapper.TaskMapper;
import uk.gov.hmcts.tasks.dev.controllers.models.TaskRequest;
import uk.gov.hmcts.tasks.dev.controllers.repository.TasksRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TasksService {

    private final TasksRepository tasksRepository;
    private final TaskMapper taskMapper;

    public TasksService(TasksRepository tasksRepository, TaskMapper taskMapper) {
        this.tasksRepository = tasksRepository;
        this.taskMapper = taskMapper;
    }

    public List<Task> findAll() {
        return tasksRepository.findAll();
    }

    public Optional<Task> findById(Long id) {
        return tasksRepository.findById(id);
    }

    public Optional<Task> create(TaskRequest taskRequest) {
        Task task = this.taskMapper.toEntity(taskRequest);
        return Optional.of(tasksRepository.save(task));
    }

    public Optional<Task> update(Task task) {
        return Optional.of(tasksRepository.save(task));
    }

    public void delete(Task task) {
        tasksRepository.delete(task);
    }
}
