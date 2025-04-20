package telran.java57.forum.posts.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"user","dateCreated"})
public class Comment {
    @Setter
    String user;
    @Setter
    String message;
    LocalDateTime dateCreated = LocalDateTime.now();
    Integer likes = 0;

    public Comment(String message, String user) {
        this.message = message;
        this.user = user;
    }

    public void addLike(){
        likes++;
    }
}
