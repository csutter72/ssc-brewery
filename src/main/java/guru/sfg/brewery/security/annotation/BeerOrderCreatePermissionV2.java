package guru.sfg.brewery.security.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('order.create') OR " +
    "hasAuthority('customer.order.create')")
public @interface BeerOrderCreatePermissionV2 {
    
}
