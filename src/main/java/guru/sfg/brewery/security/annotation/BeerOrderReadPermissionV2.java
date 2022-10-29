package guru.sfg.brewery.security.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('order.read') OR " +
"hasAuthority('customer.order.read')")
public @interface BeerOrderReadPermissionV2 {
    
}
