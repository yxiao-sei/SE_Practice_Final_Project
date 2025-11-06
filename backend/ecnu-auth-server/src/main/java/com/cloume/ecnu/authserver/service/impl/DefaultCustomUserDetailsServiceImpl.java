package com.cloume.ecnu.authserver.service.impl;

import com.cloume.ecnu.authserver.model.CustomUserDetails;
import com.cloume.ecnu.authserver.repository.DefaultCustomUserDetailsRepository;
import com.cloume.ecnu.authserver.service.ICustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultCustomUserDetailsServiceImpl implements ICustomUserDetailsService {

    @Autowired
    private DefaultCustomUserDetailsRepository customUserDetailsRepository;

    @Override
    public CustomUserDetails create() {
        return new CustomUserDetails();
    }

    @Override
    public JpaRepository<CustomUserDetails, String> getRepository() {
        return customUserDetailsRepository;
    }

    @Override
    public CustomUserDetails findByUsername(String username) {
        return customUserDetailsRepository.findTopByUsernameAndIsRemovedFalse(username);
    }

    @Override
    public List<CustomUserDetails> findByType(String userType) {
        return customUserDetailsRepository.findAllByUserTypeAndIsRemovedFalse(userType);
    }

    @Override
    public List<CustomUserDetails> findByRole(String role) {
        return customUserDetailsRepository.findAllByRolesLikeAndIsRemoved("%" + role + "%", false);
    }

    @Override
    public List<CustomUserDetails> findAll() {
        return customUserDetailsRepository.findAllByIsRemovedFalse();
    }

    @Override
    public List<CustomUserDetails> searchByRealName(String keyword, Integer limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        if (limit == null || limit <= 0) {
            return customUserDetailsRepository.findTop10ByRealNameContainingAndIsRemovedFalse(keyword.trim());
        }
        Pageable pageable = PageRequest.of(0, limit);
        return customUserDetailsRepository.findByRealNameContainingAndIsRemovedFalse(keyword.trim(), pageable);
    }
}
