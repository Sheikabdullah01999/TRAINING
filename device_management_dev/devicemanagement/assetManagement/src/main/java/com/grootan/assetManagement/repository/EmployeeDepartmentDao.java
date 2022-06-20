package com.grootan.assetManagement.repository;

import com.grootan.assetManagement.model.EmployeeDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface EmployeeDepartmentDao extends JpaRepository<EmployeeDepartment, Long> {
    @Query("SELECT u from EmployeeDepartment u where u.department=?1")
    EmployeeDepartment findByDepartmentName(String department);

    @Query(value = "select * from employee_department where department=:department",nativeQuery = true)
    EmployeeDepartment department(@Param("department") String department);

    @Transactional
    @Modifying
    @Query(value= "delete from employee_department where department=?1",nativeQuery = true)
    public void deleteDepartment(String dep);
}
