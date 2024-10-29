package pl.boryskaczmarek.blog.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class PostJsonLoader implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(PostJsonLoader.class);
    private final ObjectMapper mapper;
    private final PostRepository postRepository;

    public PostJsonLoader(ObjectMapper mapper, PostRepository jdbcClientPostRepository) {
        this.mapper = mapper;
        this.postRepository = jdbcClientPostRepository;
    }

    @Override
    public void run(String... args) {
        if (postRepository.count() == 0) {
            try (InputStream is = getClass().getResourceAsStream("/data/posts.json")) {
                Posts allPosts = mapper.readValue(is, Posts.class);
                log.info("Post table is empty. Loading {} dummy posts.", allPosts.posts().size());
                postRepository.saveAll(allPosts.posts());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("Post table is not empty. There is no need to load new data.");
        }
    }
}
