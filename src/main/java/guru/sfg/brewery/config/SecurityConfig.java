package guru.sfg.brewery.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import guru.sfg.brewery.security.ApiKeyHeaderAuthFilter;
import guru.sfg.brewery.security.CustomPasswordEncoderFactories;
import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.RestParamAuthFilter;
import guru.sfg.brewery.security.RestParamAuthFilter2;
import guru.sfg.brewery.security.google.Google2faFilter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity //(debug = true)
//@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled= true)
@EnableGlobalMethodSecurity(prePostEnabled= true)
public class SecurityConfig {

    private final AuthenticationManagerBuilder authBuilder;
    private final UserDetailsService userDetailsService;
    private final PersistentTokenRepository persistenceTokenRepository;
    private final Google2faFilter google2faFilter;

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    // Global CorsConfig
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowedMethods(Arrays.asList("*"));
        config.setAllowedOrigins(Collections.singletonList("*"));
        // config.setAllowedHeaders(Collections.singletonList("*"));
        // config.setAllowCredentials(true);
        // config.setMaxAge(3600L);
        // config.setExposedHeaders(Arrays.asList("Authorization"));


        final UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        return configSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       
        http
            .cors()
            .and()
            .authorizeRequests(authorize -> {
                authorize
                    .antMatchers(
                        "/", 
                        "/webjars/**",  
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
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
            .addFilterBefore(new ApiKeyHeaderAuthFilter(authBuilder.getObject()), UsernamePasswordAuthenticationFilter.class)    
            .addFilterAfter(google2faFilter, UsernamePasswordAuthenticationFilter.class)
            //.addFilterBefore(restHeaderAuthFilter(authBuilder.getObject()), UsernamePasswordAuthenticationFilter.class)
            //.addFilterBefore(restParamAuthFilter(authBuilder.getObject()), UsernamePasswordAuthenticationFilter.class) 
            //.addFilterBefore(restParamAuthFilter2(authBuilder.getObject()), UsernamePasswordAuthenticationFilter.class) 
            .formLogin(loginConfigurer -> {
                loginConfigurer
                    
                    .loginProcessingUrl("/login")
                    .loginPage("/").permitAll()
                    .successForwardUrl("/")
                    .defaultSuccessUrl("/")
                    .failureUrl("/?error");
            })
            .logout(logoutConfigurer -> {
                logoutConfigurer
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                    .logoutSuccessUrl("/?logout").permitAll();
            })
            .httpBasic()
            .and()
                .rememberMe()
                    .rememberMeCookieName("remember-me2")
                    .rememberMeParameter("remember-me2")
                    .tokenRepository(persistenceTokenRepository)
                    .userDetailsService(userDetailsService);
            // .rememberMe()
            //     .rememberMeCookieName("remember-me2")
            //     .rememberMeParameter("remember-me2")
            //     //.tokenValiditySeconds(60)
            //     .key("sfg-key")
            //     .userDetailsService(userDetailsService);
                

            // h2 consoloe config
            //http.headers().frameOptions().sameOrigin();

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
