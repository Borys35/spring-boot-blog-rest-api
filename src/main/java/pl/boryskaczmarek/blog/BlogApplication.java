package pl.boryskaczmarek.blog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import pl.boryskaczmarek.blog.author.Author;
import pl.boryskaczmarek.blog.author.AuthorHttpClient;
import pl.boryskaczmarek.blog.author.AuthorRestClient;

import java.util.List;

@SpringBootApplication
public class BlogApplication {

	private static final Logger log = LoggerFactory.getLogger(BlogApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
		log.info("Application started");
	}

	@Bean
	AuthorHttpClient authorHttpClient() {
		RestClient restClient = RestClient.create("https://jsonplaceholder.typicode.com");
		HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
		return factory.createClient(AuthorHttpClient.class);
	}

	@Bean
	CommandLineRunner runner(AuthorHttpClient client) {
		return args -> {
			List<Author> authors = client.findAll();
			authors.forEach(System.out::println);
		};
	}
}
