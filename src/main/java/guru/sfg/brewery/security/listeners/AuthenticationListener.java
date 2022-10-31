package guru.sfg.brewery.security.listeners;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.LoginSuccess;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.LoginFailureRepository;
import guru.sfg.brewery.repositories.security.LoginSuccessRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationListener {

    private final LoginSuccessRepository loginSuccessRepository;
    private final LoginFailureRepository loginFailureRepository;
    private final UserRepository userRepository;
    
    
    @EventListener
    public void listen(AuthenticationSuccessEvent event) {

        if(event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            final LoginSuccess.LoginSuccessBuilder builder = LoginSuccess.builder();

            final Authentication auth = (UsernamePasswordAuthenticationToken) event.getSource();
        
            if(auth.getPrincipal() instanceof User) {
                final User user = (User)auth.getPrincipal();
                builder.user(user);

                log.debug("User name logged in: " + user.getUsername());
            }

            if(auth.getDetails() instanceof WebAuthenticationDetails) {
                final WebAuthenticationDetails details = (WebAuthenticationDetails)auth.getDetails();

                builder.sourceIp(details.getRemoteAddress());
                log.debug("Remote address is: " + details.getRemoteAddress());
            }

            LoginSuccess loginSuccess  = loginSuccessRepository.saveAndFlush(builder.build());
            log.debug("LoginSucess saved. Id: " + loginSuccess.getId());
        }
        //log.debug("User logged in Okay: " + event.getAuthentication().getName());
    }

    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        if(event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            final LoginFailure.LoginFailureBuilder builder = LoginFailure.builder();

            final Authentication auth = (UsernamePasswordAuthenticationToken) event.getSource();
            builder.username(auth.getName());

            userRepository.findByUsername(auth.getName()).ifPresent((user) -> {
                builder.user(user);
            });

            if(auth.getDetails() instanceof WebAuthenticationDetails) {
                final WebAuthenticationDetails details = (WebAuthenticationDetails)auth.getDetails();

                builder.sourceIp(details.getRemoteAddress());
                log.debug("Remote address is: " + details.getRemoteAddress());
            }

            LoginFailure loginFailure  = loginFailureRepository.save(builder.build());
            log.debug("LoginFailure saved. Id: " + loginFailure.getId());

            if(loginFailure.getUser() != null) {
                lockUserAccount(loginFailure.getUser() );
            }
        }
    }

    private void lockUserAccount(User user) {
        final List<LoginFailure> failures = 
            loginFailureRepository.findAllByUserAndCreatedDateIsAfter(user, 
                                                                      Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        
        if(failures.size() > 3) {
            user.setAccountNonLocked(false);
            userRepository.save(user);
        }
    }

}
