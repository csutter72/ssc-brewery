package guru.sfg.brewery.repositories.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import guru.sfg.brewery.domain.security.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
    
    public Optional<User> findByUsername(String username);
}
