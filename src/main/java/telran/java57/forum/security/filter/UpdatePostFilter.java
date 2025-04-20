package telran.java57.forum.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import telran.java57.forum.posts.dao.PostRepository;
import telran.java57.forum.posts.model.Post;

import java.io.IOException;

@Component
@Order(30)
@RequiredArgsConstructor
public class UpdatePostFilter implements Filter {
    final PostRepository postRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (checkEndpoint(request.getMethod(), request.getServletPath())) {
            String login = request.getUserPrincipal().getName();
            String[] parts = request.getServletPath().split("/");

            // postId всегда в позиции parts[3], независимо от типа запроса
            String postId = parts[3];

            Post post = postRepository.findById(postId).orElse(null);
            if (post == null || !login.equalsIgnoreCase(post.getAuthor())) {
                response.sendError(403, "Permission denied");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean checkEndpoint(String method, String servletPath) {
        return HttpMethod.PUT.matches(method) &&
                (servletPath.matches("/forum/post/\\w+") ||
                        servletPath.matches("/forum/post/\\w+/comment/\\w+"));
    }
}