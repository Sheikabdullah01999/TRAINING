package com.grootan.assetManagement.Repository;

import com.grootan.assetManagement.Model.EmployeeDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface EmployeeDepartmentDao extends JpaRepository<EmployeeDepartment, Integer> {
    @Query("SELECT u from EmployeeDepartment u where u.department=?1")
    EmployeeDepartment findByDepartmentName(String department);

    @Transactional
    @Modifying
    @Query(value= "delete from employee_department where department=:dep",nativeQuery = true)
    public void deleteDepartment(@Param("dep") String dep);
}
