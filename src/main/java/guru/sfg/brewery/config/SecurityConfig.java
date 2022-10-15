package guru.sfg.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
}
