# HMCTS Dev Test Backend
This will be the backend for the brand new HMCTS case management system. As a potential candidate we are leaving
this in your hands. Please refer to the brief for the complete list of tasks! Complete as much as you can and be
as creative as you want.

You should be able to run `./gradlew build` to start with to ensure it builds successfully. Then from that you
can run the service in IntelliJ (or your IDE of choice) or however you normally would.

There is an example endpoint provided to retrieve an example of a case. You are free to add/remove fields as you
wish.

---

### Steps to run

##### To run with application-local.yml with H2 db
  - `./gradlew bootRun`

This has been done by adding the following entry in `build.gradle` as it uses `application-local.yaml`.

> bootRun {
  systemProperty 'spring.profiles.active', 'local'
}

For Docker it uses `application.yaml` for Postgres docker container.

#### Other Containers

- Tasks API
- Postgres
- Prometheus
- Grafana

##### To run with docker
  - Use `docker-compose up -d -build` (to build and up)
  - After first build use `docker-compose up -d`
  - To take down use `docker-compose down`

Tasks API would be available at http://localhost:4000 *after* the service is up and running.

##### Task has following properties
- Title
- Description
- Status
- Due date

##### Backend REST API has following actions
- Retrieve a task by ID
- Retrieve all tasks
- Update the status of a task
- Delete a task

#### Additional
 - The project uses `springdoc-openapi-starter-webmvc-ui`.
 - This makes OpenAPI Task API definition available at http://localhost:4000/swagger-ui/index.html.
 - Solution uses AOP for logging
 - Caching has also been used with Caffeine
 - Grafana has been added with Prometheus and Metrics to monitor the API with Spring Boot 3.x statistics.
   Grafana would be available at http://localhost:3000 *after* the container services are up and running.


