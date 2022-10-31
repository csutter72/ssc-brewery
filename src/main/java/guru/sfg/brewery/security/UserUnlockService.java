package guru.sfg.brewery.security;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserUnlockService {

    private final UserRepository userRepository;

    @Scheduled(fixedRate = 5000)
    public void unlockAccounts() {
        log.debug("Running unlock accounts service ...");

        List<User> lockedUsers = userRepository
            .findAllByAccountNonLockedAndLastModifiedDateIsBefore(Boolean.FALSE,
                            Timestamp.valueOf(LocalDateTime.now().minusSeconds(30)));
        
        if(lockedUsers.size() > 0) {

            log.debug("Lock accounts found. Unlocking ...");

            lockedUsers.forEach((user) -> {
                user.setAccountNonExpired(true);
            });

            userRepository.saveAll(lockedUsers);
        }
    }
    
}
