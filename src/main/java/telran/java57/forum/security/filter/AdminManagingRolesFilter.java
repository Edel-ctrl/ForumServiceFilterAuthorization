package telran.java57.forum.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import telran.java57.forum.accounting.dao.UserAccountRepository;
import telran.java57.forum.accounting.model.Role;
import telran.java57.forum.accounting.model.UserAccount;

import java.io.IOException;

@Component
@Order(20) // Order of the filter in the chain (1-100)
@RequiredArgsConstructor
public class AdminManagingRolesFilter implements Filter {
    final UserAccountRepository userAccountRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if ( checkEndpoint(request.getMethod(), request.getServletPath()) ) {
            String principal = request.getUserPrincipal().getName();
//            UserAccount userAccount = userAccountRepository.findById(principal).get();
            UserAccount userAccount = userAccountRepository.findById(principal)
                    .orElseThrow(() -> new RuntimeException("User not found" + principal));
            if ( ! userAccount.getRoles().contains(Role.ADMINISTRATOR) ) {
                response.sendError(403, "You are not allowed to access this resource");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean checkEndpoint(String method, String servletPath) {
        return servletPath.matches("/account/user/\\w+/role/\\w+");
    }
}
