package guru.sfg.brewery.security.google;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.security.servlet.StaticResourceRequest;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import guru.sfg.brewery.domain.security.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Google2faFilter extends GenericFilterBean {

    private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();
    private final Google2faFailureHandler google2faFailureHandler = new Google2faFailureHandler();
    private final RequestMatcher urlIs2fa = new AntPathRequestMatcher("/user/verify2fa");
    private final RequestMatcher urlIsResource = new AntPathRequestMatcher("/resources/**");
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        StaticResourceRequest.StaticResourceRequestMatcher staticMatcher = PathRequest.toStaticResources().atCommonLocations();

        if (urlIs2fa.matches(request) || urlIsResource.matches(request) || staticMatcher.matches(request)) {
            chain.doFilter(request, response);
            return;
        }

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && !authenticationTrustResolver.isAnonymous(auth)) {
            log.debug("Processing 2FA Filter");
            if(auth.getPrincipal() != null && auth.getPrincipal() instanceof User) {
                final User user = (User)auth.getPrincipal();

                if(user.getUseGoogle2fa() && user.getGoogle2faRequired()) {
                    log.debug("2FA Required");

                    google2faFailureHandler.onAuthenticationFailure(request, response, null);
                    return;
                }
            }

        }

        chain.doFilter(request, response);
        
    }
    
}
