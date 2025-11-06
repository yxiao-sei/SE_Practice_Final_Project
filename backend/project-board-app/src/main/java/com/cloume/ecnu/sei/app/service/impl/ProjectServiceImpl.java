package com.cloume.ecnu.sei.app.service.impl;

import com.cloume.ecnu.sei.app.model.Project;
import com.cloume.ecnu.sei.app.repository.ProjectRepository;
import com.cloume.ecnu.sei.app.service.IBaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements IBaseService<Project> {

    private final ProjectRepository repository;

    public ProjectServiceImpl(ProjectRepository repository) {
        this.repository = repository;
    }

    @Override
    public Project create() {
        return new Project();
    }

    @Override
    public JpaRepository<Project, Integer> getRepository() {
        return repository;
    }
}
