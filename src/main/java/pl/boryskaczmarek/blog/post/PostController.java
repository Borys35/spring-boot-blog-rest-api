package pl.boryskaczmarek.blog.post;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping()
    List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    Post findPostById(@PathVariable int id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        return post.get();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    void createPost(@Valid @RequestBody Post post) {
        postRepository.save(post);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    void updatePost(@PathVariable int id, @RequestBody Post post) {
        Post found = postRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        Post updated = new Post(
                post.id(),
                post.title() != null ? post.title() : found.title(),
                post.authorId() != null ? post.authorId() : found.authorId(),
                post.content() != null ? post.content() : found.content(),
                found.createdAt(),
                LocalDateTime.now(),
                found.version()
        );
        postRepository.save(updated);
        postRepository.save(post);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void deletePost(@PathVariable int id) {
        postRepository.deleteById(id);
    }

    @GetMapping("/count")
    long getCount() {
        return postRepository.count();
    }

    @GetMapping("/author/{id}")
    List<Post> findPostsByAuthor(@PathVariable int id) {
        return postRepository.findAllByAuthorId(id);
    }
}
