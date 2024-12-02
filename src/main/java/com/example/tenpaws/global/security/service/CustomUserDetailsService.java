package com.example.tenpaws.global.security.service;

import com.example.tenpaws.domain.admin.entity.Admin;
import com.example.tenpaws.domain.admin.repository.AdminRepository;
import com.example.tenpaws.domain.shelter.entity.Shelter;
import com.example.tenpaws.domain.shelter.repository.ShelterRepository;
import com.example.tenpaws.domain.user.entity.User;
import com.example.tenpaws.domain.user.repositoty.UserRepository;
import com.example.tenpaws.global.security.dto.AdminUserDetails;
import com.example.tenpaws.global.security.dto.NormalUserDetails;
import com.example.tenpaws.global.security.dto.ShelterUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ShelterRepository shelterRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return new NormalUserDetails(user);
        }

        Shelter shelter = shelterRepository.findByEmail(email).orElse(null);
        if (shelter != null) {
            return new ShelterUserDetails(shelter);
        }

        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            return new AdminUserDetails(admin);
        }

        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
    }

    public Map<String, Object> getInfosByEmail(String email) throws UsernameNotFoundException {
        Map<String, Object> map = new HashMap<>();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            map.put("id", user.getId());
            map.put("role", user.getUserRole());
            map.put("username", user.getUsername());
            return map;
        }
        Shelter shelter = shelterRepository.findByEmail(email).orElse(null);
        if (shelter != null) {
            map.put("id", shelter.getId());
            map.put("role", shelter.getUserRole());
            map.put("username", shelter.getShelterName());
            return map;
        }
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            map.put("id", admin.getId());
            map.put("role", admin.getUserRole());
            map.put("username", admin.getUsername());
            return map;
        }
        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
    }
}
