package pl.boryskaczmarek.blog.author;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class AuthorRestClient {

    private final RestClient restClient;

    public AuthorRestClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
    }

    public List<Author> findAll() {
        return restClient
                .get()
                .uri("/users")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
