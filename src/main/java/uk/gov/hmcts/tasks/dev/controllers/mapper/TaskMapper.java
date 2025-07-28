package uk.gov.hmcts.tasks.dev.controllers.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.tasks.dev.controllers.entities.Task;
import uk.gov.hmcts.tasks.dev.controllers.models.TaskRequest;
import uk.gov.hmcts.tasks.dev.controllers.models.TaskResponse;

@Mapper(componentModel = "spring")
@Component
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    Task toEntity(TaskRequest taskRequest);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "dueDate", source = "dueDate")
    TaskResponse toResponse(Task task);
}
