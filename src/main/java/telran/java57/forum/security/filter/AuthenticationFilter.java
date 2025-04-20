package telran.java57.forum.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import telran.java57.forum.accounting.dao.UserAccountRepository;
import telran.java57.forum.accounting.model.Role;
import telran.java57.forum.accounting.model.UserAccount;
import telran.java57.forum.security.filter.model.User;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Order(10)
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {
    final UserAccountRepository userAccountRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if(checkEndpoint(request.getMethod(),request.getServletPath())) {
            try {
                String[] credentials = getCredentials(request.getHeader("Authorization"));
                UserAccount userAccount = userAccountRepository.findById(credentials[0]).orElseThrow(RuntimeException::new);
                if (!BCrypt.checkpw(credentials[1], userAccount.getPassword())) {
                    throw new RuntimeException();
                }
                Set<String> roles = userAccount.getRoles().stream().map(Role::name).collect(Collectors.toSet());
                request = new WrappedRequest(request, credentials[0], roles);
            } catch (RuntimeException e) {
                response.sendError(401);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean checkEndpoint(String method, String servletPath) {
        return !(
                (HttpMethod.POST.matches(method) && servletPath.matches("/account/register"))
                || servletPath.matches("/forum/posts/.*")
        );
    }

    private String[] getCredentials(String headerValue) {
        String token = headerValue.split(" ")[1];
        String decode = new String(Base64.getDecoder().decode(token));
        return decode.split(":");
    }

    private class WrappedRequest extends HttpServletRequestWrapper {
        private String login;
        private Set<String> roles;

        public WrappedRequest(HttpServletRequest request, String login,Set<String> roles) {
            super(request);
            this.login = login;
            this.roles = roles;
        }

        @Override
        public Principal getUserPrincipal() {
            return new User(login,roles);
        }
    }
}
