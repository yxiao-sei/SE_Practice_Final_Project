package com.cloume.ecnu.sei.app.service;

import com.cloume.ecnu.sei.app.model.CustomUserDetails;

import java.util.List;

public interface ICustomUserDetailsService extends IBaseService<CustomUserDetails> {

    CustomUserDetails findByUsername(String username);

    List<CustomUserDetails> findByType(String teacher);

    List<CustomUserDetails> findByRole(String roleTutor);

    List<CustomUserDetails> findAll();
}
