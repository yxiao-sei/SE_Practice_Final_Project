package com.cloume.ecnu.sei.app.repository;

import com.cloume.ecnu.sei.app.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
}
