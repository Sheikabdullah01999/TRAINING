package com.grootan.assetManagement.Repository;

import com.grootan.assetManagement.Model.EmployeeDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.criteria.CriteriaBuilder;

public interface EmployeeDepartmentDao extends JpaRepository<EmployeeDepartment, Integer> {
}
