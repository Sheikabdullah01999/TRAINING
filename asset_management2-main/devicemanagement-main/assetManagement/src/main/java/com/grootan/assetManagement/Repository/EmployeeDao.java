package com.grootan.assetManagement.Repository;

import com.grootan.assetManagement.Model.Employee;
import com.grootan.assetManagement.Model.EmployeeDevices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface EmployeeDao extends JpaRepository<Employee,String> {

    Employee findByEmail(String email);

    Employee findByEmpDevices(String empDevices);

    Employee findByEmpId(String empId);

    @Query(value = "select * from employee s where s.emp_name ilike :keyword% or s.emp_id ilike :keyword% or s.emp_department ilike :keyword%  or s.assign_role ilike :keyword%  or s.email ilike :keyword%", nativeQuery = true)
    List<Employee> findByKeyword(@Param("keyword") String keyword);

    @Transactional
    @Modifying
    @Query(value="DELETE FROM Employee u where u.empId= :id")
    int deleteByEmpId(@Param("id") String empId);

    @Query("SELECT new com.grootan.assetManagement.Model.EmployeeDevices(e.empId, d.deviceName, d.id, d.devicePurchaseDate, d.category) FROM Employee e join e.devices d")
    List<EmployeeDevices> getUserDevice();

    @Transactional
    @Modifying
    @Query("UPDATE Employee SET empDevices= :empDevices where empId= :id")
    public void updateEmployeeByEmpDevice(@Param("id") String empId,@Param("empDevices") String empDevices);

    @Query(value="SELECT empDevices FROM Employee WHERE empId= :id")
    public String getEmpDevices(@Param("id") String empId);

    @Query(value="SELECT email FROM Employee WHERE empId= :id")
    public String getEmployeeMail(@Param("id") String empId);

}
