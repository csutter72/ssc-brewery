package guru.sfg.brewery.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("Getting user info via JPA for username: " + username);

        guru.sfg.brewery.domain.security.User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                return new UsernameNotFoundException("Username: " + username + "not found.");
            });

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
            user.getPassword(),
            user.getEnabled(),
            user.getAccountNonExpired(),
            user.getCredentialsNonExpired(),
            user.getAccountNonLocked(),
            convertToSpringAuthorities(user.getAuthorities()));
    }

    private Collection<? extends GrantedAuthority> convertToSpringAuthorities(Set<Authority> authorities) {
        final HashSet<GrantedAuthority> grantedAuthorities = new HashSet<>();

        if(authorities != null && authorities.size() > 0) {
            for (Authority authority : authorities) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authority.getPermission()));
            };
        } 

        return grantedAuthorities;
    }
    
}