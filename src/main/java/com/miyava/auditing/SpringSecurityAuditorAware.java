package com.miyava.auditing;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SpringSecurityAuditorAware
    implements AuditorAware<String> {

    public String getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ( authentication == null ) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if ( principal instanceof UserDetails ) {
            return ( (UserDetails) principal ).getUsername();
        }

        return principal.toString();
    }
}
