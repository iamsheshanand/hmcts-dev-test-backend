package uk.gov.hmcts.tasks.dev.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("integration")
public class OpenAPIPublisherTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    void testOpenAPISpecification() throws IOException {
        String openApiPath = new File("./tasks-openapi-specification.yml")
                .toURI()
                .toString();

        OpenAPI openAPI = new OpenAPIV3Parser()
                .readLocation(resourceLoader.getResource(openApiPath).getURI().toString(), null, new ParseOptions())
                .getOpenAPI();

        assertNotNull(openAPI);
    }
}
