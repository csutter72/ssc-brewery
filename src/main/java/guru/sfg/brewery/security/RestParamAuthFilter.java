package guru.sfg.brewery.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestParamAuthFilter extends AbstractAuthenticationProcessingFilter {


    
    public RestParamAuthFilter(AuthenticationManager authenticationManager,
                               RequestMatcher requiresAuthenticationRequestMatcher ) {
        super(requiresAuthenticationRequestMatcher, authenticationManager);

    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = getUsername(request);
        String password = getPassword(request);

        if(username == null) {
            username = "";
        }

        if(password == null) {
            password = "";
        }

        if(!StringUtils.hasText(username)) {
            
        }

        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        return getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        SecurityContext context =  SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        SecurityContextHolder.clearContext();

        response.sendError(HttpStatus.UNAUTHORIZED.value(),
                           HttpStatus.UNAUTHORIZED.getReasonPhrase());  
    }

    private String getPassword(HttpServletRequest request) {
        return request.getParameter("Api-Secret");
    }

    private String getUsername(HttpServletRequest request) {
        return request.getParameter("Api-Key");
    }
    
    
}
