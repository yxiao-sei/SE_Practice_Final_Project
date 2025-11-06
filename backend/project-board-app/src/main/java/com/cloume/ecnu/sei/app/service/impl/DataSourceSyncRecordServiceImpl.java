package com.cloume.ecnu.sei.app.service.impl;

import com.cloume.ecnu.sei.app.model.DataSourceSyncRecord;
import com.cloume.ecnu.sei.app.repository.DataSourceSyncRecordRepository;
import com.cloume.ecnu.sei.app.service.IDataSourceSyncRecordService;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;

@Service
public class DataSourceSyncRecordServiceImpl implements IDataSourceSyncRecordService {
    @Autowired
    private DataSourceSyncRecordRepository dataSourceSyncRecordRepository;

    @Override
    public DataSourceSyncRecord create() {
        return new DataSourceSyncRecord();
    }

    @Override
    public JpaRepository<DataSourceSyncRecord, Integer> getRepository() {
        return dataSourceSyncRecordRepository;
    }

    @Override
    public Page<DataSourceSyncRecord> list(List<String> username, String outerDataSourceId, String type, String dataSourceTypeName, String createdTime, int page, int size) {
        //        //排序
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createdTime");
        Sort sort = Sort.by(order);
        //分页
        Pageable pageable = PageRequest.of(page, size, sort);
        /**
         * root：是我们要查询的类型
         * query：添加查询条件
         * cb：构建Predicate
         */
        Specification<DataSourceSyncRecord> specification = (Specification<DataSourceSyncRecord>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.and(criteriaBuilder.equal(root.get("isRemoved"), false));
            if (StringUtils.isNotEmpty(outerDataSourceId)) {
                predicate.getExpressions().add(criteriaBuilder.like(root.get("outerDataSource"), outerDataSourceId));
            }
            if (CollectionUtils.isNotEmpty(username)) {
                predicate.getExpressions().add(criteriaBuilder.in(root.get("creator")).value(username));
            }
            if (StringUtils.isNotEmpty(type)) {
                predicate.getExpressions().add(criteriaBuilder.like(root.get("dataSourceType"), type));
            }
            if (StringUtils.isNotEmpty(dataSourceTypeName)) {
                predicate.getExpressions().add(criteriaBuilder.like(root.get("dataSourceTypeName"), "%" + dataSourceTypeName + "%"));
            }
            if (StringUtils.isNotEmpty(createdTime) && !createdTime.equals(",")) {
                List<String> createdTimes = Arrays.asList(createdTime.split(","));
                predicate.getExpressions().add(criteriaBuilder.ge(root.get("createdTime"), Long.valueOf(createdTimes.get(0))));
                predicate.getExpressions().add(criteriaBuilder.le(root.get("createdTime"), Long.valueOf(createdTimes.get(1))));
            }
            return predicate;
        };
        return dataSourceSyncRecordRepository.findAll(specification, pageable);
    }
}
