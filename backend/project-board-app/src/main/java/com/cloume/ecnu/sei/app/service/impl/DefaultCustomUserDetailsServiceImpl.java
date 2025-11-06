package com.cloume.ecnu.sei.app.service.impl;

import com.cloume.ecnu.sei.app.model.CustomUserDetails;
import com.cloume.ecnu.sei.app.repository.DefaultCustomUserDetailsRepository;
import com.cloume.ecnu.sei.app.service.ICustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public JpaRepository<CustomUserDetails, Integer> getRepository() {
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
}
