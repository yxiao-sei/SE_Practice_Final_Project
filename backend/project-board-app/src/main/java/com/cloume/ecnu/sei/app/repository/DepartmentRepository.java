package com.cloume.ecnu.sei.app.repository;

import com.cloume.ecnu.sei.app.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "departments")
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

}
