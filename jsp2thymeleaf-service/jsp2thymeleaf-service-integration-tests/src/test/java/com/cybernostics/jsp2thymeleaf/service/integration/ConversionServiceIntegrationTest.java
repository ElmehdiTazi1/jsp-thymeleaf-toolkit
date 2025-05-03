package com.cybernostics.jsp2thymeleaf.service.integration;

import com.cybernostics.jsp2thymeleaf.service.web.JSP2ThymeleafServiceApplication;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(
    classes = JSP2ThymeleafServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ConversionServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1/conversion";
    }

    @Test
    void whenConvertingValidJspFile_thenReturnsSuccessfulConversion() throws Exception {
        File testFile = ResourceUtils.getFile("classpath:test-files/simple.jsp");

        given()
            .multiPart("file", testFile)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .post("/file")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo("COMPLETED"))
            .body("errors", empty())
            .body("conversionId", notNullValue())
            .body("outputPath", notNullValue());
    }

    @Test
    void whenConvertingInvalidFile_thenReturnsError() {
        given()
            .multiPart("file", "invalid.txt", "Invalid content".getBytes())
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .post("/file")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("status", equalTo("FAILED"))
            .body("errors", not(empty()));
    }

    @Test
    void whenCheckingConversionStatus_thenReturnsCorrectStatus() {
        // First create a conversion
        String conversionId = given()
            .multiPart("file", "test.jsp", "<html><body>Test</body></html>".getBytes())
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .post("/file")
        .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .path("conversionId");

        // Then check its status
        given()
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .get("/status/{id}", conversionId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body(anyOf(
                equalTo("PENDING"),
                equalTo("IN_PROGRESS"),
                equalTo("COMPLETED")
            ));
    }
}
