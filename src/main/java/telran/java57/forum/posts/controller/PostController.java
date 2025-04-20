package telran.java57.forum.posts.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telran.java57.forum.posts.dto.DatePeriodDto;
import telran.java57.forum.posts.dto.NewCommentDto;
import telran.java57.forum.posts.dto.NewPostDto;
import telran.java57.forum.posts.dto.PostDto;
import telran.java57.forum.posts.service.PostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forum")
public class PostController {
    final PostService postService;

    @PostMapping("/post/{author}")
    public PostDto addNewPost(@PathVariable String author, @RequestBody NewPostDto newPostDto) {
        return postService.addNewPost(author, newPostDto);
    }

    @GetMapping("/post/{id}")
    public PostDto findPostById(@PathVariable String id) {
        return postService.findPostById(id);
    }

    @DeleteMapping("/post/{id}")
    public PostDto removePost(@PathVariable String id) {
        return postService.removePost(id);
    }

    @PutMapping("/post/{id}")
    public PostDto updatePost(@PathVariable String id, @RequestBody NewPostDto newPostDto) {
        return postService.updatePost(id, newPostDto);
    }

    @GetMapping("posts/author/{author}")
    public Iterable<PostDto> findPostsByAuthor(@PathVariable String author) {
        return postService.findPostsByAuthor(author);
    }

    @PutMapping("post/{id}/comment/{author}")
    public PostDto addComment(@PathVariable String id, @PathVariable String author,
                              @RequestBody NewCommentDto newCommentDto) {
        return postService.addComment(id, author,newCommentDto);
    }

    @PostMapping("posts/tags")
    public Iterable<PostDto> findPostsByTags(@RequestBody List<String> tags) {
        return postService.findPostsByTags(tags);
    }

    @PostMapping("posts/period")
    public Iterable<PostDto> findPostsByPeriod(@RequestBody DatePeriodDto datePeriodDto) {
        return postService.findPostsByPeriod(datePeriodDto);
    }

    @PutMapping("post/{id}/like")
    public void addLike(@PathVariable String id) {
        postService.addLike(id);
    }
}
