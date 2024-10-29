package pl.boryskaczmarek.blog.post;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface PostRepository extends ListCrudRepository<Post, Integer> {

    List<Post> findAllByAuthorId(int id);

}
