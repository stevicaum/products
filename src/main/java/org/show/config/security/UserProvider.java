package org.show.config.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class UserProvider {

    public static final UserDetails USER_DETAILS = User.withUsername("user").password("user").roles("USER").build();
    public static final UserDetails ADMIN_DETAILS = User.withUsername("admin").password("admin").roles("ADMIN", "USER").build();

    public static Optional<UserDetails> find(String username) {
        if (username.equals("user")) {
            return Optional.of(USER_DETAILS);

        } else if (username.equals("admin")) {
            return Optional.of(ADMIN_DETAILS);

        }
        return Optional.empty();
    }
}