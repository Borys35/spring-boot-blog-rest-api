package pl.boryskaczmarek.blog.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcClientPostRepository {
    private final ArrayList<Post> posts = new ArrayList<Post>();

    private static final Logger log = LoggerFactory.getLogger(JdbcClientPostRepository.class);
    private final JdbcClient jdbcClient;

    public JdbcClientPostRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    List<Post> findAllPosts() {
        return jdbcClient.sql("SELECT * FROM Post").query(Post.class).list();
    }

    Optional<Post> findPostById(int id) {
        return jdbcClient.sql("SELECT * FROM Post WHERE id = :id").param("id", id).query(Post.class).optional();
    }

    void createPost(Post post) {
        int updated = jdbcClient
                .sql("INSERT INTO Post (id, title, author_id, content, created_at, updated_at) VALUES (?,?,?,?,?,?)")
                .params(List.of(post.id(),post.title(),post.authorId(),post.content(),post.createdAt(),post.updatedAt()))
                .update();

        Assert.state(updated == 1, "Failed to create post " + post.title());
    }

    void updatePost(int id, Post post) {
        int updated = jdbcClient
                .sql("UPDATE Post SET title=?, author_id=?, content=?, created_at=?, updated_at=? WHERE id=?")
                .params(List.of(post.title(),post.authorId(),post.content(),post.createdAt(),id))
                .update();

        Assert.state(updated == 1, "Failed to update post " + post.title());
    }

    void deletePost(int id) {
        int updated = jdbcClient.sql("DELETE FROM Post WHERE id = :id").param("id", id).update();

        Assert.state(updated == 1, "Failed to delete post " + id);
    }

    int getCount() {
        return jdbcClient.sql("SELECT id FROM Post").query().listOfRows().size();
    }

    void createAll(List<Post> posts) {
        posts.forEach(this::createPost);
    }
}
