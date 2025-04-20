package telran.java57.forum.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import telran.java57.forum.accounting.model.Role;
import telran.java57.forum.security.filter.model.User;

import java.io.IOException;

@Component
@Order(40)
public class DeleteUserFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (checkEndpoint(request.getMethod(),request.getServletPath())) {
            User user = (User) request.getUserPrincipal();
            String[] parts = request.getServletPath().split("/");
            String owner = parts[parts.length-1];
            if(!(user.getName().equalsIgnoreCase(owner) || user.getRoles().contains(Role.ADMINISTRATOR.name()))){
                response.sendError(403,"Permission denied");
                return;
            }
        }
        filterChain.doFilter(request,response);
    }

    private boolean checkEndpoint(String method, String servletPath) {
        return (HttpMethod.DELETE.matches(method) && servletPath.matches("/account/user/\\w+"));

    }
}
