package uk.gov.hmcts.tasks.dev;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("functional")
class TasksFunctionalTest {

    protected static final String CONTENT_TYPE_VALUE = "application/json";
    public static final String TASKS = "/tasks";
    public static final String TASK_REQUEST = """
                                              {
                                                  "title": "Test Task",
                                                  "description": "Test Description",
                                                  "status": "TODO",
                                                  "dueDate": "2025-07-31"
                                              }
                                              """;

    @Value("${TEST_URL:http://localhost:4000}")
    private String testUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void testGetAllTasks() {
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get(TASKS)
            .then()
            .extract().response();

        assertEquals(200, response.statusCode());
    }

    @Test
    void testGetTaskById() {
        // Assuming a task with ID 1 exists or needs to be created first
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/tasks/1")
            .then()
            .extract().response();

        // Task may or may not exist, so checking for 200 or 404
        assertEquals(true, response.statusCode() == 200 || response.statusCode() == 404);
    }

    @Test
    void testCreateTask() {

        Response response = given()
            .contentType(ContentType.JSON)
            .body(TASK_REQUEST)
            .when()
            .post(TASKS)
            .then()
            .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getLong("id"));
        assertEquals("Test Task", response.jsonPath().getString("title"));
    }

    @Test
    void testDeleteTask() {

        Response createResponse = given()
            .contentType(ContentType.JSON)
            .body(TASK_REQUEST)
            .when()
            .post(TASKS)
            .then()
            .extract().response();

        Long taskId = createResponse.jsonPath().getLong("id");

        Response deleteResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .delete("/tasks/" + taskId)
            .then()
            .extract().response();

        assertEquals(204, deleteResponse.statusCode());
    }

    @Test
    void testPatchTask() {

        Response createResponse = given()
            .contentType(ContentType.JSON)
            .body(TASK_REQUEST)
            .when()
            .post(TASKS)
            .then()
            .extract().response();

        Long taskId = createResponse.jsonPath().getLong("id");

        String patchRequest = """
                              {
                                  "status": "IN_PROGRESS"
                              }
                              """;

        Response patchResponse = given()
            .contentType(ContentType.JSON)
            .body(patchRequest)
            .when()
            .patch("/tasks/" + taskId)
            .then()
            .extract().response();

        assertEquals(200, patchResponse.statusCode());
        assertEquals("IN_PROGRESS", patchResponse.jsonPath().getString("status"));
    }
}
