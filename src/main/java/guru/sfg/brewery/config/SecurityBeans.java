package guru.sfg.brewery.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;

import guru.sfg.brewery.security.google.GoogleCredentialRepository;

@Configuration
public class SecurityBeans {

    @Bean
    public PersistentTokenRepository persitentTokenDatabase(DataSource dataSource) {
        JdbcTokenRepositoryImpl tokenRepository =  new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        tokenRepository.setCreateTableOnStartup(true);

        return tokenRepository;
    }

    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new DefaultAuthenticationEventPublisher(applicationEventPublisher); 
    }

    @Bean
    public GoogleAuthenticator googleAuthenticator(GoogleCredentialRepository googleCredentialRepository) {
        final GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder configBuilder = 
                new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder();
        
        configBuilder
            .setTimeStepSizeInMillis(TimeUnit.MILLISECONDS.convert(Duration.ofMillis(60000)))
            .setWindowSize(10)
            .setNumberOfScratchCodes(0);
        
        final GoogleAuthenticator googleAuthenticator = 
            new GoogleAuthenticator(configBuilder.build());
        googleAuthenticator.setCredentialRepository(googleCredentialRepository);

        return googleAuthenticator;
    }
    
}
