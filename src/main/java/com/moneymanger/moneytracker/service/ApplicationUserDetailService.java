package com.moneymanger.moneytracker.service;

import com.moneymanger.moneytracker.entity.ProfileEntity;
import com.moneymanger.moneytracker.reposistory.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ApplicationUserDetailService  implements UserDetailsService {
    private  final ProfileRepository profileRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ProfileEntity profileEntity= profileRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return User.builder()
                .username(profileEntity.getEmail())
                .password(profileEntity.getPassword())
                .authorities(Collections.emptySet()).build();
    }
}
