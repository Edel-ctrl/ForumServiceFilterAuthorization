package telran.java57.forum.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    String user;
    String message;
    LocalDateTime dateCreated;
    Integer likes;
}
