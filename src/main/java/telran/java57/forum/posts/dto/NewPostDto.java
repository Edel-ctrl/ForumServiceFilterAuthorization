package telran.java57.forum.posts.dto;

import lombok.Getter;

import java.util.Set;

@Getter
public class NewPostDto {
    String title;
    String content;
    Set<String> tags;
}

