package com.grootan.assetManagement.Repository;

import com.grootan.assetManagement.Model.DeviceName;
import com.grootan.assetManagement.Model.EmployeeDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.criteria.CriteriaBuilder;

public interface EmployeeDepartmentDao extends JpaRepository<EmployeeDepartment, Integer> {
    @Query("SELECT u from EmployeeDepartment u where u.department=?1")
    EmployeeDepartment findByDepartmentName(String department);
}
