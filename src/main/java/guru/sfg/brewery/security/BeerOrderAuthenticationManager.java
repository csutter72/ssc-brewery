package guru.sfg.brewery.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BeerOrderAuthenticationManager {
    
    public boolean customerIdMatches(Authentication authentication, UUID customerId) throws AuthenticationException {

        final User authenticatedUser = (User)authentication.getPrincipal();

        log.debug("Auth User Customer Id: " + authenticatedUser.getCustomer().getId() + " Requested Customer Id: " + customerId);

        return authenticatedUser.getCustomer().getId().equals(customerId);
    }
    
}
