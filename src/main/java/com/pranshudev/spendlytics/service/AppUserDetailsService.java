package com.pranshudev.spendlytics.service;

import com.pranshudev.spendlytics.entity.ProfileEntity;
import com.pranshudev.spendlytics.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService  implements UserDetailsService {
    private final ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ProfileEntity existingProfileEntity = profileRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Profile not found with email"+ email));
        return  User.builder()
                .username( existingProfileEntity.getEmail())
                .password(existingProfileEntity.getPassword())
                .authorities(Collections.emptyList())
                .build();
//                existingProfileEntity.getRoles()
//                        .stream()
//                        .map(role -> new SimpleGrantedAuthority(role.getName()))
//                        .collect(Collectors.toList())
//

    }
}
