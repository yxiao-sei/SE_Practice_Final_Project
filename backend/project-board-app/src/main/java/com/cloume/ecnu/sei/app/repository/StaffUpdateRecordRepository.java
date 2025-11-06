package com.cloume.ecnu.sei.app.repository;

import com.cloume.ecnu.sei.app.model.UpdateRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "staff-update-record")
public interface StaffUpdateRecordRepository extends JpaRepository<UpdateRecord, Integer> {


    @RestResource(path = "multiple")
    Page<UpdateRecord> findAllByIsRemovedFalseAndModelResourceOwner(@Param("modelResourceOwner") String modelResourceOwner,
                                                                    Pageable pageable);
}
