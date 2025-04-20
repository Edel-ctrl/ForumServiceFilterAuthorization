package telran.java57.forum.posts.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import telran.java57.forum.posts.dao.PostRepository;
import telran.java57.forum.posts.dto.DatePeriodDto;
import telran.java57.forum.posts.dto.NewCommentDto;
import telran.java57.forum.posts.dto.NewPostDto;
import telran.java57.forum.posts.dto.PostDto;
import telran.java57.forum.posts.dto.exceptions.PostNotFoundException;
import telran.java57.forum.posts.model.Comment;
import telran.java57.forum.posts.model.Post;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    final PostRepository postRepository;
    final ModelMapper modelMapper;

    @Override
    public PostDto addNewPost(String author, NewPostDto newPostDto) {
        Post post = new Post(newPostDto.getTitle(), newPostDto.getContent(), author, newPostDto.getTags());
        post = postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto findPostById(String id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        return modelMapper.map(post, PostDto.class);
    }


    @Override
    public PostDto removePost(String id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        postRepository.delete(post);
        return modelMapper.map(post, PostDto.class);
    }


    @Override
    public PostDto updatePost(String id, NewPostDto newPostDto) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        String content = newPostDto.getContent();
        if (content != null) {
            post.setContent(content);
        }
        String title = newPostDto.getTitle();
        if (title != null) {
            post.setTitle(title);
        }
        Set<String> tags = newPostDto.getTags();
        if (tags != null) {
            tags.forEach(post::addTag);
        }
        post = postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public Iterable<PostDto> findPostsByAuthor(String author) {
        return postRepository.findPostsByAuthorIgnoreCase(author)
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

    @Override
    public PostDto addComment(String id, String author, NewCommentDto newCommentDto) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        Comment comment = new Comment(newCommentDto.getMessage(), author);
        post.addComment(comment);
        post = postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public Iterable<PostDto> findPostsByTags(List<String> tags) {
        return postRepository.findPostsByTagsInIgnoreCase(tags)
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

    @Override
    public Iterable<PostDto> findPostsByPeriod(DatePeriodDto datePeriodDto) {
        return postRepository.findPostsByDateCreatedBetween(datePeriodDto.getDateFrom(),datePeriodDto.getDateTo())
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }

    @Override
    public void addLike(String id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        post.addLike();
        postRepository.save(post);
    }

}
