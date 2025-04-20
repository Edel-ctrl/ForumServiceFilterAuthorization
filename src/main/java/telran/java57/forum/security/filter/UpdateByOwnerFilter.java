package telran.java57.forum.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(25)
public class UpdateByOwnerFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (checkEndpoint(request.getMethod(),request.getServletPath())) {
            String login = request.getUserPrincipal().getName();
            String[] parts = request.getServletPath().split("/");
            String owner = parts[parts.length-1];
            if(!login.equalsIgnoreCase(owner)){
                response.sendError(403,"You are not owner");
                return;
            }
        }
        filterChain.doFilter(request,response);
    }

    private boolean checkEndpoint(String method, String servletPath) {
        return (HttpMethod.PUT.matches(method) && servletPath.matches("/account/user/\\w+"))
                || (HttpMethod.PUT.matches(method) && servletPath.matches("/account/password"))
                || (HttpMethod.POST.matches(method) && servletPath.matches("/forum/post/\\w+"))
                || (HttpMethod.PUT.matches(method) && servletPath.matches("/forum/post/\\w+/comment/\\w+"));
    }
//    private boolean checkEndpoint(String method, String servletPath) {
    //  /account/post
//    return (servletPath.matches("(account|forum)/(user|post)/\\w+"));
//    }
}
