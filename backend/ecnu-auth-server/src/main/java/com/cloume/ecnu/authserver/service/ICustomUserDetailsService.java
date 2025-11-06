package com.cloume.ecnu.authserver.service;

import com.cloume.ecnu.authserver.model.CustomUserDetails;

import java.util.List;

public interface ICustomUserDetailsService extends IBaseService<CustomUserDetails> {

    CustomUserDetails findByUsername(String username);

    List<CustomUserDetails> findByType(String teacher);

    List<CustomUserDetails> findByRole(String roleTutor);

    List<CustomUserDetails> findAll();

    List<CustomUserDetails> searchByRealName(String keyword, Integer limit);
}
