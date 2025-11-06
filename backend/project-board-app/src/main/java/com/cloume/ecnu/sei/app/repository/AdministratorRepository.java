package com.cloume.ecnu.sei.app.repository;

import com.cloume.ecnu.sei.app.model.Administrator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(path = "administrator")
public interface AdministratorRepository extends JpaRepository<Administrator, Integer> {

    @RestResource(path = "multiple")
    Page<Administrator> findAllByIsRemovedFalseAndDepartmentCode(@Param("departmentCode") String departmentCode,
                                                                 Pageable pageable);

    List<Administrator> findAllByNumber(String number);
}