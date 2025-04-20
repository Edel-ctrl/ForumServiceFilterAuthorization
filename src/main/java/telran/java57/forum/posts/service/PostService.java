package telran.java57.forum.posts.service;

import telran.java57.forum.posts.dto.DatePeriodDto;
import telran.java57.forum.posts.dto.NewCommentDto;
import telran.java57.forum.posts.dto.NewPostDto;
import telran.java57.forum.posts.dto.PostDto;

import java.util.List;

public interface PostService {

    PostDto addNewPost(String author, NewPostDto newPostDto);

    PostDto findPostById(String id);

    PostDto removePost(String id);

    PostDto updatePost(String id, NewPostDto newPostDto);

    Iterable<PostDto> findPostsByAuthor(String author);

    PostDto addComment(String id, String author, NewCommentDto newCommentDto);

    Iterable<PostDto> findPostsByTags(List<String> tags);

    Iterable<PostDto> findPostsByPeriod(DatePeriodDto datePeriodDto);

    void addLike(String id);
}
