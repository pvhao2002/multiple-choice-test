package com.app.quizzservice.security;

import com.app.quizzservice.model.User;
import com.app.quizzservice.utils.Constants;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPrincipal extends User implements OAuth2User, UserDetails {
    Collection<? extends GrantedAuthority> authorities;
    Map<String, Object> attributes;

    public UserPrincipal(
            long userId,
            String email,
            String password,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(userId, email, password);
        this.authorities = authorities;
    }

    public UserPrincipal(ResultSet rs) throws SQLException {
        super(rs);
    }

    public static UserPrincipal create(User user) {

        return new UserPrincipal(
                user.getUserId(),
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(Constants.ROLE_USER))
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return StringUtils.capitalize("%s %s".formatted(firstName, lastName));
    }
}
