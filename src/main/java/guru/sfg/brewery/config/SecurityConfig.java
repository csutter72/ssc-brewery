package guru.sfg.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorize -> {
                authorize
                    .antMatchers(
                        "/", 
                        "/webjars/**", 
                        "/login", 
                        "/resources/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/beers/find", "/beers*").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/v1/beer", "/api/v1/beer/**").permitAll()
                    .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll()
                    .anyRequest().authenticated();  
            })
            .formLogin()
            .and()
            .httpBasic();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails admin = User.withUsername("spring")
                                .password(passwordEncoder().encode("pwd"))
                                .roles("ADMIN")
                                .build();
        
        UserDetails user = User.withUsername("user")
                                .password(passwordEncoder().encode("password"))
                                .roles("USER")
                                .build();
        
        UserDetails customer = User.withUsername("Scott")
                                    .password(passwordEncoder().encode("tiger"))
                                    .roles("CUSTOMER")
                                    .build();

        return new InMemoryUserDetailsManager(admin, user, customer);
    }
}
