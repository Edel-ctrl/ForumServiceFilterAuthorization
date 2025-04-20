package telran.java57.forum.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import telran.java57.forum.accounting.model.Role;
import telran.java57.forum.posts.dao.PostRepository;
import telran.java57.forum.posts.model.Post;
import telran.java57.forum.security.filter.model.User;

import java.io.IOException;

@Component
@Order(40)
@RequiredArgsConstructor
public class DeletePostFilter implements Filter {
    final PostRepository postRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (checkEndpoint(request.getMethod(), request.getServletPath())) {
            User user = (User) request.getUserPrincipal();
            String[] parts = request.getServletPath().split("/");
            String postId = parts[3]; // /forum/post/{postId}
            Post post = postRepository.findById(postId).orElse(null);

            boolean isModerator = user.getRoles().contains(Role.MODERATOR.name());
            boolean isOwner = post != null && user.getName().equalsIgnoreCase(post.getAuthor());

            if (post == null || (!isOwner && !isModerator)) {
                response.sendError(403, "Permission denied");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean checkEndpoint(String method, String servletPath) {
        return HttpMethod.DELETE.matches(method) && servletPath.matches("/forum/post/\\w+");
    }
}