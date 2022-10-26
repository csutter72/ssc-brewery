package guru.sfg.brewery.security.params;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('brewery.update')")
public @interface BreweryUpdatePermission {
    
}
