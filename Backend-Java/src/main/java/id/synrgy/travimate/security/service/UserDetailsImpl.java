package id.synrgy.travimate.security.service;

import id.synrgy.travimate.model.Users;
import id.synrgy.travimate.security.jwt.AuthTokenFilter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails{

    private String username;
    private String password;
    private List<GrantedAuthority> authorities;

    public UserDetailsImpl(String username, String password, List<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetails build(Users users) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        Object roles = AuthTokenFilter.getRoles();
        if (roles != null && roles instanceof ArrayList) {
            ArrayList<String> rolesList = (ArrayList<String>) roles;

            for (String role : rolesList) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        return new UserDetailsImpl(users.getUsername(), users.getPassword(), authorities);
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
        return username;
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
}
