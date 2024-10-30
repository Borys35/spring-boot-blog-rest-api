package pl.boryskaczmarek.blog.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
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

    @Test
    void shouldFindPostWhenValidId() {
        ResponseEntity<Post> response = client.get()
                .uri("/api/posts/{id}", 1)
                .retrieve()
                .toEntity(Post.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldThrowExceptionWhenPostNotFound() {
        client.get()
                .uri("api/posts/{id}", 9999)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
                })
                .onStatus(HttpStatusCode::is2xxSuccessful, (req, res) -> {
                    fail("Expected 404 status code, but got a 2xx.");
                })
                .toBodilessEntity();
    }

    @Test
    void shouldCreatePost() {
        Post post = new Post(
            10,
            "New Post",
            4,
            "Content of the post",
            LocalDateTime.now(),
            LocalDateTime.now(),
            null
        );

        ResponseEntity<Void> response = client.post()
                .uri("/api/posts")
                .body(post)
                .retrieve()
                .toBodilessEntity();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Post createdPost = client.get()
                .uri("/api/posts/{id}", post.id())
                .retrieve()
                .body(Post.class);

        assertNotNull(createdPost);
        assertEquals(post.id(), createdPost.id());
        assertEquals(post.title(), createdPost.title());
    }

    @Test
    void shouldNotCreatePostWhenBadRequestBody() {
        Post post = new Post(
                11,
                "",
                4,
                "",
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        ResponseEntity<Void> response = client.post()
                .uri("/api/posts")
                .body(post)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
                })
                .onStatus(HttpStatusCode::is2xxSuccessful, (req, res) -> {
                    fail("Expected 404 status code, but got a 2xx.");
                })
                .toBodilessEntity();
    }

    @Test
    void shouldUpdateEntirePost() {
        Post post = new Post(
                1,
                "Updated Post",
                2,
                "Content of the post",
                LocalDateTime.now(),
                LocalDateTime.now(),
                1
        );

        ResponseEntity<Void> response = client
                .put()
                .uri("/api/posts/{id}", 1)
                .body(post)
                .retrieve()
                .toBodilessEntity();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Post updatedPost = client.get()
                .uri("api/posts/{id}", 1)
                .retrieve()
                .body(Post.class);

        assertNotNull(updatedPost);
        assertEquals(post.id(), updatedPost.id());
        assertEquals(post.title(), updatedPost.title());
        assertEquals(post.content(), updatedPost.content());
    }

    @Test
    void shouldDeletePost() {
        ResponseEntity<Void> response = client.delete()
                .uri("/api/posts/{id}", 1)
                .retrieve()
                .toBodilessEntity();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void shouldReturnValidPostsSize() {
        ResponseEntity<Long> response = client.get()
                .uri("/api/posts/count")
                .retrieve()
                .toEntity(Long.class);

        assertEquals(5, response.getBody());
    }
}