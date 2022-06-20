package com.grootan.assetManagement.repository;

import com.grootan.assetManagement.model.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminDao extends CrudRepository<Employee,String> {
}
