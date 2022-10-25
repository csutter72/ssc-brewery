package guru.sfg.brewery.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
public class RestHeaderAuthFilter extends AbstractAuthenticationProcessingFilter {
    
    public RestHeaderAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher,
            AuthenticationManager authenticationManager) {
        super(requiresAuthenticationRequestMatcher, authenticationManager);

    }
        
    // @Override
	// public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
	// 		throws IOException, ServletException {

    //     final HttpServletRequest request = (HttpServletRequest) req;
    //     final HttpServletResponse response = (HttpServletResponse) res;

    //     if(log.isDebugEnabled()) {
    //         log.debug("Request is to process authentication");
    //     }

    //     try {
    //         final Authentication authenticationResult = attemptAuthentication(request, response);

    //         if(authenticationResult != null) {
    //             successfulAuthentication(request, response, chain, authenticationResult);
    //         } else {
    //             chain.doFilter(request, response);
    //         }
    //     } catch (AuthenticationException ex) {
    //         log.error("Authentication failed", ex);
    //         unsuccessfulAuthentication(request, response, ex);
    //     }
	// }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null) {
            return auth;
        }
        
        String userName = getUsername(request);
        String password = getPassword(request);

        if(userName == null) {
            userName = "";
        }

        if(password == null) {
            password = "";
        }

        if(log.isDebugEnabled()) {
            log.debug("Authentication user: " + userName);
        }

        final UsernamePasswordAuthenticationToken token = 
                new UsernamePasswordAuthenticationToken(userName, password);

        if(StringUtils.hasText(userName)) {
            auth = getAuthenticationManager().authenticate(token);
            if(log.isDebugEnabled()) {
                log.debug("isUserAuthenticated: " + auth.isAuthenticated());
            }
            return auth;
        } else {
            throw new BadCredentialsException("Username is empty!");
            //return null;
        }
    }

    protected String getPassword(HttpServletRequest request) {
        return request.getHeader("Api-Secret");
    }

    protected String getUsername(HttpServletRequest request) {
        return request.getHeader("Api-Key");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                Authentication authResult) throws IOException, ServletException {

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);

    }

    // @Override
    // protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
    //         AuthenticationException failed) throws IOException, ServletException {
    //     SecurityContextHolder.clearContext();

    //     log.trace("Failed to process authentication request", failed);
    //     log.trace("Cleared SecurityContextHolder");

    //     response.sendError(HttpStatus.UNAUTHORIZED.value(),
    //                        HttpStatus.UNAUTHORIZED.getReasonPhrase());
    // }
}
