package guru.sfg.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import guru.sfg.brewery.security.ApiKeyHeaderAuthFilter;
import guru.sfg.brewery.security.CustomPasswordEncoderFactories;
import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.RestParamAuthFilter;
import guru.sfg.brewery.security.RestParamAuthFilter2;

@Configuration
@EnableWebSecurity(debug = true)
//@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled= true)
@EnableGlobalMethodSecurity(prePostEnabled= true)
public class SecurityConfig {

    private AuthenticationManagerBuilder authBuilder;
    
    public SecurityConfig(AuthenticationManagerBuilder authBuilder) {
        this.authBuilder = authBuilder;
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

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
                    .antMatchers("/h2-console/**").permitAll()    
                    //.antMatchers("/beers*").permitAll()
                    //.antMatchers("/beers/find").hasAnyRole("CUSTOMER", "ADMIN", "USER")

                    //.antMatchers(HttpMethod.GET, "/api/v1/beer*").hasAnyRole("CUSTOMER", "ADMIN", "USER")

                    //.mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").hasAnyRole("CUSTOMER", "ADMIN", "USER")

                    //.mvcMatchers(HttpMethod.DELETE, "/api/v1/beer/**").hasRole("ADMIN")
                    

                    //.mvcMatchers(HttpMethod.GET, "/brewery/api/v1/breweries").hasAnyRole("CUSTOMER", "ADMIN")
                    //.mvcMatchers( "/brewery/breweries").hasAnyRole("CUSTOMER", "ADMIN")

                    .anyRequest().authenticated();  
            })
            .csrf()
                .ignoringAntMatchers("/h2-comnsole/**", "/api/**")
                //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            .addFilterBefore(new ApiKeyHeaderAuthFilter(authBuilder.getObject()), UsernamePasswordAuthenticationFilter.class)    
            //.addFilterBefore(restHeaderAuthFilter(authBuilder.getObject()), UsernamePasswordAuthenticationFilter.class)
            //.addFilterBefore(restParamAuthFilter(authBuilder.getObject()), UsernamePasswordAuthenticationFilter.class) 
            //.addFilterBefore(restParamAuthFilter2(authBuilder.getObject()), UsernamePasswordAuthenticationFilter.class) 
            .formLogin()
            .and()
            .httpBasic();

            // h2 consoloe config
            http.headers().frameOptions().sameOrigin();

        return http.build();
    }

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
        final RestHeaderAuthFilter filter = new RestHeaderAuthFilter(
                                new AntPathRequestMatcher("/api/**"),
                                authenticationManager);
        return filter;
    }

    public RestHeaderAuthFilter restParamAuthFilter2(AuthenticationManager authenticationManager) {
        final RestHeaderAuthFilter filter = new RestParamAuthFilter2(
                                new AntPathRequestMatcher("/api/**"),
                                authenticationManager);
        return filter;
    }

    public RestParamAuthFilter restParamAuthFilter(AuthenticationManager authenticationManager) {
        final RestParamAuthFilter filter = new RestParamAuthFilter(authenticationManager, 
                                                                  new AntPathRequestMatcher("/api/**"));
        return filter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return CustomPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    // @Bean
    // public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

    //     UserDetails admin = User.withUsername("spring")
    //                             .password(passwordEncoder.encode("pwd"))
    //                             .roles("ADMIN")
    //                             .build();
        
    //     UserDetails user = User.withUsername("user")
    //                             .password(passwordEncoder.encode("password"))
    //                             .roles("USER")
    //                             .build();
        
    //     UserDetails customer = User.withUsername("scott")
    //                                 .password(passwordEncoder.encode("tiger"))
    //                                 .roles("CUSTOMER")
    //                                 .build();

    //     return new InMemoryUserDetailsManager(admin, user, customer);
    // }

}
