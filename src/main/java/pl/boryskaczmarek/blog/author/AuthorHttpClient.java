package pl.boryskaczmarek.blog.author;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface AuthorHttpClient {

    @GetExchange("/users")
    List<Author> findAll();

    @GetExchange("/users/{id}")
    Author findById(@PathVariable int id);
}
