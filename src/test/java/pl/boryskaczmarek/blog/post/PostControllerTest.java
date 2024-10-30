package pl.boryskaczmarek.blog.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// This test handles entire application so we import everything
// by @SpringBootTest annotation

// @Testcontainers looks for @Container annotations in the class.
// For example, we use PostgreSQLContainer

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostControllerTest {

    @LocalServerPort
    int serverPort;

    RestClient client;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @BeforeEach
    void setUp() {
        client = RestClient.create("http://localhost:" + serverPort);
    }

    @Test
    void shouldFindAllPosts() {
        List<Post> posts = client.get()
                .uri("/api/posts")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        assertEquals(5, posts.size());
    }
}