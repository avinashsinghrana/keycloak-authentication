package com.asr.auth.service;

import com.asr.auth.domain.entity.AppUser;
import com.asr.auth.exception.BusinessException;
import com.asr.auth.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository userRepository;

    public AppUser saveUser(AppUser user) {
        return userRepository.save(user);
    }

    public Optional<AppUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public AppUser findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    public AppUser findByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    public void checkDuplicates(String email, String mobile) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email already registered");
        }
        if (mobile != null && userRepository.existsByMobile(mobile)) {
            throw new BusinessException("Mobile number already registered");
        }
    }
}
