package pl.boryskaczmarek.blog.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

// In this test we only use Jdbc part of the application
// so we import only slice of the app by using @DataJdbcTest

@Testcontainers
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    PostRepository repository;

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @BeforeEach
    void setUp() {
        List<Post> posts = List.of(
                new Post(0,
                    "Example Title",
                    1,
                    "This is content",
                    LocalDateTime.now().minusDays(1),
                    LocalDateTime.now(),
                    null),
                new Post(1,
                    "Example Title 2",
                    1,
                    "This is content 2",
                    LocalDateTime.now().minusDays(2),
                    LocalDateTime.now(),
                    null));

        repository.saveAll(posts);
    }

    @Test
    void shouldReturnPostsByAuthorId() {
        List<Post> posts = repository.findAllByAuthorId(1);
        assertThat(posts.size()).isEqualTo(2);
    }
}