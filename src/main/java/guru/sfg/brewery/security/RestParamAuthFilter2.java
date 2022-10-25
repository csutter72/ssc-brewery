package guru.sfg.brewery.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class RestParamAuthFilter2 extends RestHeaderAuthFilter {

    public RestParamAuthFilter2(RequestMatcher requiresAuthenticationRequestMatcher,
            AuthenticationManager authenticationManager) {
        super(requiresAuthenticationRequestMatcher, authenticationManager);

    }

    @Override
    protected String getPassword(HttpServletRequest request) {
        return request.getParameter("Api-Secret");
    }

    @Override
    protected String getUsername(HttpServletRequest request) {
        return request.getParameter("Api-Key");
    }

    
    
}
