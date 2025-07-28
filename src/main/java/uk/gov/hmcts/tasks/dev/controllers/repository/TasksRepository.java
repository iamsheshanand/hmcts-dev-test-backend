package uk.gov.hmcts.tasks.dev.controllers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.tasks.dev.controllers.entities.Task;

public interface TasksRepository extends JpaRepository<Task, Long> {
}
