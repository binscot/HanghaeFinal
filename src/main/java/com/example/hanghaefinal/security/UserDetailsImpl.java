package com.example.hanghaefinal.security;

import com.example.hanghaefinal.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override //계정의 만료여부 리턴 스프링시큐리티의 기능들
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override //계정의 잠금여부를 리턴
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override //계정의 비번이 만료되었는지 리턴
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override //사용가능한계정인지 리턴
    public boolean isEnabled() {
        return true;
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }
}
