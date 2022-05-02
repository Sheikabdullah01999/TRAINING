package com.grootan.assetManagement.Configuration;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<String> username;
        if (principal instanceof UserDetails) {
           username = Optional.ofNullable(((UserDetails) principal).getUsername());
        } else {
          username = Optional.ofNullable(principal.toString());
        }

        return username;
    }
}
