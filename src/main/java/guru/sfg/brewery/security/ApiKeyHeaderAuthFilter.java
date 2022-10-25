package guru.sfg.brewery.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiKeyHeaderAuthFilter extends OncePerRequestFilter {

    private AuthenticationManager authenticationManager;

    public ApiKeyHeaderAuthFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.isAuthenticated()) {
            filterChain.doFilter(request, response);
        }
 
        String username =  getUsername(request);
        String password = getPassword(request);

        if(!StringUtils.hasText(username)) {
            username = "";
        }
        if(!StringUtils.hasText(password)) {
            username = "";
        }

        if(!StringUtils.hasText(username)) {
            filterChain.doFilter(request, response);
            return;
        }

        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        
        try {
            auth = authenticationManager.authenticate(token);
            successfulAuthentication(auth);
            filterChain.doFilter(request, response);     

        } catch(AuthenticationException ex) {
            log.error("Authentication failed", ex);
            unsuccessfulAuthentication(response, ex);
        }
        
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        
        //return request.getServletPath().equals("/api/"); 
        return request.getMethod().equals(HttpMethod.GET.name());
    }

    private String getPassword(HttpServletRequest request) {
        return request.getHeader("Api-Secret");
    }

    private String getUsername(HttpServletRequest request) {
        return request.getHeader("Api-Key");
    }
    
    private void successfulAuthentication(Authentication authentication) {
       
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private void unsuccessfulAuthentication(HttpServletResponse response,
                                            AuthenticationException ex) throws IOException {

        SecurityContextHolder.clearContext();

        response.sendError(HttpStatus.UNAUTHORIZED.value(), 
                           HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }
}
