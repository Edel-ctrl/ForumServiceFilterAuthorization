package telran.java57.forum.posts.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import telran.java57.forum.posts.model.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public interface PostRepository extends MongoRepository<Post, String> {

    Stream<Post> findPostsByAuthorIgnoreCase(String author);

    Stream<Post> findPostsByTagsInIgnoreCase(List<String> tags);

    Stream<Post> findPostsByDateCreatedBetween(LocalDateTime dateCreated, LocalDateTime dateCreated2);
}
