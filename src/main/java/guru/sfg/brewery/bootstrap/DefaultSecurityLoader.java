package guru.sfg.brewery.bootstrap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Component
@Order(1)
public class DefaultSecurityLoader implements CommandLineRunner {

    private final UserRepository userRepo;
    private final AuthorityRepository authorityRepo;
    private final PasswordEncoder encoder;

    private enum RoleKeys {
        CUSTOMER,
        USER,
        ADMIN
    }

    private enum AuthorityKeys {
        BEER_CREATE,
        BEER_UPDATE,
        BEER_READ,
        BEER_DELETE,
        BREWERY_CREATE,
        BREWERY_UPDATE,
        BREWERY_READ,
        BREWERY_DELETE,
        CUSTOMER_CREATE,
        CUSTOMER_UPDATE,
        CUSTOMER_READ,
        CUSTOMER_DELETE,
        ORDER_CREATE,
        ORDER_UPDATE,
        ORDER_READ,
        ORDER_DELETE,
        CUSTOMER_ORDER_CREATE,
        CUSTOMER_ORDER_UPDATE,
        CUSTOMER_ORDER_READ,
        CUSTOMER_ORDER_DELETE,
        ORDER_PICK_UP,
        CUSTOMER_ORDER_PICK_UP
    }

    @Override
    public void run(String... args) throws Exception {

        Map<AuthorityKeys, Authority> authorities = getAuthorities();
        Map<RoleKeys, Role> roles = getRoles(authorities);
        loadUser(roles);
    }

    private void loadUser(final Map<RoleKeys, Role> roles) {

        if(userRepo.count() == 0 && roles != null && roles.size() > 0) {

            final List<User> users = getUsers(roles);

            userRepo.saveAllAndFlush(users);

            log.debug("Authorities loaded: " + authorityRepo.count());
            log.debug("Users loaded: " + userRepo.count());
        }
    }

    private List<User> getUsers(final Map<RoleKeys, Role> roles) {
       
        final List<User> users = new ArrayList<>();

        final User spring = User.builder()
            .username("spring")
            .password(encoder.encode("pwd"))
            .role(roles.get(RoleKeys.ADMIN))
            .build();

        final User user = User.builder()
            .username("user")
            .password(encoder.encode("password"))
            .role(roles.get(RoleKeys.USER))
            .build();
            
        final User scott = User.builder()
            .username("scott")
            .password(encoder.encode("tiger"))
            .role(roles.get(RoleKeys.CUSTOMER))
            .build();    

        users.add(spring);
        users.add(user);
        users.add(scott);

        return users;
    }
    
    private Map<AuthorityKeys, Authority> getAuthorities() {
        final Map<AuthorityKeys, Authority> authorities = new HashMap<>();


        final Authority beer_create = Authority.builder().permission("beer.create").build();
        final Authority beer_update = Authority.builder().permission("beer.update").build();
        final Authority beer_read = Authority.builder().permission("beer.read").build();
        final Authority beer_delete = Authority.builder().permission("beer.delete").build();

        final Authority brewery_create = Authority.builder().permission("brewery.create").build();
        final Authority brewery_update = Authority.builder().permission("brewery.update").build();
        final Authority brewery_read = Authority.builder().permission("brewery.read").build();
        final Authority brewery_delete = Authority.builder().permission("brewery.delete").build();

        final Authority customer_create = Authority.builder().permission("customer.create").build();
        final Authority customer_update = Authority.builder().permission("customer.update").build();
        final Authority customer_read = Authority.builder().permission("customer.read").build();
        final Authority customer_delete = Authority.builder().permission("customer.delete").build();

        final Authority order_create = Authority.builder().permission("order.create").build();
        final Authority order_update = Authority.builder().permission("order.update").build();
        final Authority order_read = Authority.builder().permission("order.read").build();
        final Authority order_delete = Authority.builder().permission("order.delete").build();

        final Authority customer_order_create = Authority.builder().permission("customer.order.create").build();
        final Authority customer_order_update = Authority.builder().permission("customer.order.update").build();
        final Authority customer_order_read = Authority.builder().permission("customer.order.read").build();
        final Authority customer_order_delete = Authority.builder().permission("customer.order.delete").build();

        final Authority order_pick_up = Authority.builder().permission("order.pickup").build();
        final Authority customer_order_pick_up = Authority.builder().permission("customer.order.pickup").build();


        authorities.put(AuthorityKeys.BEER_CREATE, beer_create);
        authorities.put(AuthorityKeys.BEER_UPDATE, beer_update);
        authorities.put(AuthorityKeys.BEER_READ, beer_read);
        authorities.put(AuthorityKeys.BEER_DELETE, beer_delete);

        authorities.put(AuthorityKeys.BREWERY_CREATE, brewery_create);
        authorities.put(AuthorityKeys.BREWERY_UPDATE, brewery_update);
        authorities.put(AuthorityKeys.BREWERY_READ, brewery_read);
        authorities.put(AuthorityKeys.BREWERY_DELETE, brewery_delete);

        authorities.put(AuthorityKeys.CUSTOMER_CREATE, customer_create);
        authorities.put(AuthorityKeys.CUSTOMER_UPDATE, customer_update);
        authorities.put(AuthorityKeys.CUSTOMER_READ, customer_read);
        authorities.put(AuthorityKeys.CUSTOMER_DELETE, customer_delete);

        authorities.put(AuthorityKeys.ORDER_CREATE, order_create);
        authorities.put(AuthorityKeys.ORDER_UPDATE, order_update);
        authorities.put(AuthorityKeys.ORDER_READ, order_read);
        authorities.put(AuthorityKeys.ORDER_DELETE, order_delete);

        authorities.put(AuthorityKeys.CUSTOMER_ORDER_CREATE, customer_order_create);
        authorities.put(AuthorityKeys.CUSTOMER_ORDER_UPDATE, customer_order_update);
        authorities.put(AuthorityKeys.CUSTOMER_ORDER_READ, customer_order_read);
        authorities.put(AuthorityKeys.CUSTOMER_ORDER_DELETE, customer_order_delete);

        authorities.put(AuthorityKeys.ORDER_PICK_UP, order_pick_up);
        authorities.put(AuthorityKeys.CUSTOMER_ORDER_PICK_UP, customer_order_pick_up);

        return authorities;
    }

    private Map<RoleKeys, Role> getRoles(final Map<AuthorityKeys, Authority> authorities) {
        final Map<RoleKeys, Role> roles = new HashMap<>();

        if(authorities != null && !authorities.isEmpty()) {

            final Role admin = Role.builder().name("ROLE_ADMIN").build();
            final Role user = Role.builder().name("ROLE_USER").build();
            final Role customer = Role.builder().name("ROLE_CUSTOMER").build();

            admin.setAuthorities(Set.of(authorities.get(AuthorityKeys.BEER_CREATE), 
                                        authorities.get(AuthorityKeys.BEER_UPDATE), 
                                        authorities.get(AuthorityKeys.BEER_READ), 
                                        authorities.get(AuthorityKeys.BEER_DELETE),
                                        authorities.get(AuthorityKeys.BREWERY_CREATE),
                                        authorities.get(AuthorityKeys.BREWERY_UPDATE),
                                        authorities.get(AuthorityKeys.BREWERY_READ),
                                        authorities.get(AuthorityKeys.BREWERY_DELETE),
                                        authorities.get(AuthorityKeys.CUSTOMER_CREATE),
                                        authorities.get(AuthorityKeys.CUSTOMER_UPDATE),
                                        authorities.get(AuthorityKeys.CUSTOMER_READ),
                                        authorities.get(AuthorityKeys.CUSTOMER_DELETE),
                                        authorities.get(AuthorityKeys.ORDER_CREATE),
                                        authorities.get(AuthorityKeys.ORDER_UPDATE),
                                        authorities.get(AuthorityKeys.ORDER_READ),
                                        authorities.get(AuthorityKeys.ORDER_DELETE),
                                        authorities.get(AuthorityKeys.ORDER_PICK_UP)));

            customer.setAuthorities(Set.of(authorities.get(AuthorityKeys.BEER_READ),
                                           authorities.get(AuthorityKeys.BREWERY_READ),
                                           authorities.get(AuthorityKeys.CUSTOMER_READ),
                                           authorities.get(AuthorityKeys.CUSTOMER_ORDER_CREATE),
                                           authorities.get(AuthorityKeys.CUSTOMER_ORDER_UPDATE),
                                           authorities.get(AuthorityKeys.CUSTOMER_ORDER_READ),
                                           authorities.get(AuthorityKeys.CUSTOMER_ORDER_DELETE),
                                           authorities.get(AuthorityKeys.CUSTOMER_ORDER_PICK_UP)));

            user.setAuthorities(Set.of(authorities.get(AuthorityKeys.BEER_READ)));

            roles.put(RoleKeys.CUSTOMER, customer);
            roles.put(RoleKeys.ADMIN,admin);
            roles.put(RoleKeys.USER,user);
        }

        return roles;
    }


    
}
