package guru.sfg.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import guru.sfg.brewery.domain.security.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

}
