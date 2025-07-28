package uk.gov.hmcts.tasks.dev;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.tasks.dev.entities.Status;
import uk.gov.hmcts.tasks.dev.models.TaskRequest;
import uk.gov.hmcts.tasks.dev.models.TaskResponse;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("smoke")
class TaskControllerSmokeTest {

    protected static final String CONTENT_TYPE_VALUE = "application/json";

    public static final String TASKS_ID = "/tasks/{id}";
    public static final String TASKS = "/tasks";

    @Value("${TEST_URL:http://localhost:4000}")
    private String testUrl;

    private static final String taskRequest = """
                                              {
                                                "title": "Test Task",
                                                "description": "Test Description",
                                                "status":"TODO",
                                                "dueDate":"2025-07-31"
                                              }
                                              """;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private TaskResponse taskResponse;

    private final ObjectMapper objectMapper = JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .build();

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();

    }

    @Test
    @DisplayName("Smoke test for Task Controller")
    void smokeTestForTaskController() {

        Response response = given()
            .contentType(ContentType.JSON)
            .body(taskRequest)
            .when()
            .get(TASKS)
            .then()
            .statusCode(200)
            .extract().response();

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertNotNull(response.getBody().asString());
    }

    @Test
    @DisplayName("Smoke test for create task")
    void smokeTestForCreateTask() {

        Response response = given()
            .contentType(ContentType.JSON)
            .body(taskRequest)
            .when()
            .post(TASKS)
            .then()
            .statusCode(201)
            .extract().response();

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
        Assertions.assertNotNull(response.jsonPath().getLong("id"));

        Assertions.assertEquals("Test Task", response.jsonPath().getString("title"));
    }

    @Test
    @DisplayName("Smoke test for get task by id returning 404")
    void smokeTestForGetTaskById404() {
        testRestTemplate.delete(TASKS_ID, 1L);
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get(TASKS_ID, 1L)
            .then()
            .statusCode(404)
            .extract().response();

        Assertions.assertEquals(404, response.getStatusCode());
    }

    @Test
    @DisplayName("Smoke test for get task by Id 2XX response")
    void smokeTestForGetTaskById2XX() {

        TaskRequest taskRequestToGetTaskById = new TaskRequest();
        taskRequestToGetTaskById.setTitle("Test Task");
        taskRequestToGetTaskById.setDescription("Description");
        taskRequestToGetTaskById.setStatus(Status.COMPLETED);
        taskRequestToGetTaskById.setDueDate(LocalDate.now());

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
            testUrl + TASKS,
            taskRequestToGetTaskById,
            String.class
        );

        try {
            taskResponse = objectMapper.readValue(responseEntity.getBody(), TaskResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get(TASKS_ID, 1L)
            .then()
            .statusCode(200)
            .extract().response();

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(taskResponse.getTitle(), response.jsonPath().getString("title"));

        testRestTemplate.delete(TASKS_ID, taskResponse.getId());
    }

    @Test
    @DisplayName("Smoke test for update task status")
    void smokeTestForUpdateTaskStatus() {

        TaskRequest taskRequestToGetTaskById = new TaskRequest();
        taskRequestToGetTaskById.setTitle("Test task");
        taskRequestToGetTaskById.setDescription("Description");
        taskRequestToGetTaskById.setStatus(Status.TODO);
        taskRequestToGetTaskById.setDueDate(LocalDate.now());

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
            testUrl + TASKS,
            taskRequestToGetTaskById,
            String.class
        );

        try {
            taskResponse = objectMapper.readValue(responseEntity.getBody(), TaskResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TaskRequest taskRequestToUpdateStatus = new TaskRequest();
        taskRequestToUpdateStatus.setStatus(Status.IN_PROGRESS);

        Response response = given()
            .contentType(ContentType.JSON)
            .body(taskRequestToUpdateStatus)
            .when()
            .patch(TASKS_ID, taskResponse.getId())
            .then()
            .statusCode(200)
            .extract().response();

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("IN_PROGRESS", response.jsonPath().getString("status"));

        testRestTemplate.delete(TASKS_ID, taskResponse.getId());
    }

    @Test
    @DisplayName("Smoke test for update task status ResourceNotFound")
    void smokeTestForUpdateTaskStatusResourceNotFound() {
        TaskRequest taskRequestToUpdateStatus = new TaskRequest();
        taskRequestToUpdateStatus.setStatus(Status.IN_PROGRESS);

        Long nonExistingTaskId = 100L;

        Response response = given()
            .contentType(ContentType.JSON)
            .body(taskRequestToUpdateStatus)
            .when()
            .patch(TASKS_ID, nonExistingTaskId)
            .then()
            .statusCode(404)
            .extract().response();

        Assertions.assertEquals(404, response.getStatusCode());

    }

    @Test
    void smokeTestForDeleteTaskById() {

    }
}
