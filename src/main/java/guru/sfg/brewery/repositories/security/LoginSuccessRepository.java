package guru.sfg.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import guru.sfg.brewery.domain.security.LoginSuccess;

@Repository
public interface LoginSuccessRepository extends JpaRepository<LoginSuccess, Integer>{
    
}
