package uk.gov.hmcts.tasks.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.tasks.dev.entities.Task;

public interface TasksRepository extends JpaRepository<Task, Long> {
}
