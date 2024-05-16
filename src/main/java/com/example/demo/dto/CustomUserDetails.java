package com.example.demo.dto;

import com.example.demo.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final UserEntity userEntity;

    public CustomUserDetails(UserEntity userEntity) {

        this.userEntity = userEntity;
    }


    // roll 값 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                // userEntity 객체에서 role값만 뽑아서 반환
                return userEntity.getRole();
            }
        });

        return collection;
    }

    // password 반환
    @Override
    public String getPassword() {

        return userEntity.getPassword();
    }

    // username 반환
    @Override
    public String getUsername() {

        return userEntity.getUsername();
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    // 계정 막힘 여부 반환
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