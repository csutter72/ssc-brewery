package guru.sfg.brewery.bootstrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Order(value=2)
@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultSecurityLoader implements CommandLineRunner {

    private final UserRepository userRepo;
    private final AuthorityRepository authorityRepo;
    private final PasswordEncoder encoder;

    private enum RoleKeys {
        CUSTOMER,
        USER,
        ADMIN
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {

        Map<RoleKeys, Authority> authorities = loadAuthorites();
        loadUser(authorities);
    }

    private Map<RoleKeys, Authority> loadAuthorites() {

        Map<RoleKeys, Authority> authorities = getAuthorities();

        if(authorityRepo.count() == 0) {

            authorityRepo.saveAllAndFlush(authorities.values());
             
        }

        return authorities;
    }

    private void loadUser(final Map<RoleKeys, Authority> authorities) {

        if(userRepo.count() == 0 && authorities != null && authorities.size() > 0) {

            final List<User> users = getUsers(authorities);

            userRepo.saveAllAndFlush(users);

            log.debug("Authorities loaded: " + authorityRepo.count());
            log.debug("Users loaded: " + userRepo.count());
        }
    }

    private List<User> getUsers(final Map<RoleKeys, Authority> authorities) {
       
        final List<User> users = new ArrayList<>();

        final User spring = User.builder()
            .username("spring")
            .password(encoder.encode("pwd"))
            .authority(authorities.get(RoleKeys.ADMIN))
            .build();

        final User user = User.builder()
            .username("user")
            .password(encoder.encode("password"))
            .authority(authorities.get(RoleKeys.USER))
            .build();
            
        final User scott = User.builder()
            .username("scott")
            .password(encoder.encode("tiger"))
            .authority(authorities.get(RoleKeys.CUSTOMER))
            .build();    

        users.add(spring);
        users.add(user);
        users.add(scott);

        return users;
    }

    private Map<RoleKeys, Authority> getAuthorities() {
        final Map<RoleKeys, Authority> authorities = new HashMap<>();

        final Authority admin = Authority.builder().role("ROLE_ADMIN").build();
        final Authority user = Authority.builder().role("ROLE_USER").build();
        final Authority customer = Authority.builder().role("ROLE_CUSTOMER").build();

        authorities.put(RoleKeys.CUSTOMER, customer);
        authorities.put(RoleKeys.ADMIN,admin);
        authorities.put(RoleKeys.USER,user);

        return authorities;
    }


    
}
