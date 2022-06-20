package com.grootan.assetManagement.repository;

import com.grootan.assetManagement.model.Employee;
import com.grootan.assetManagement.model.EmployeeDevices;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface EmployeeDao extends CrudRepository<Employee,String> {

    Employee findByEmail(String email);


    Employee findByEmpId(String empId);

    @Query(value = "select * from employee s where s.emp_name ilike :keyword% or s.emp_id ilike :keyword% or s.emp_department ilike :keyword%  or s.assign_role ilike :keyword%  or s.email ilike :keyword%", nativeQuery = true)
    List<Employee> findByKeyword(@Param("keyword") String keyword);

    @Transactional
    @Modifying
    @Query(value="DELETE FROM Employee u where u.empId= :id")
    int deleteByEmpId(@Param("id") String empId);

    @Query("SELECT new com.grootan.assetManagement.model.EmployeeDevices(e.empId, d.deviceName, d.deviceId, d.devicePurchaseDate, d.category) FROM Employee e join e.devices d")
    List<EmployeeDevices> getUserDevice();

    @Query("SELECT new com.grootan.assetManagement.model.EmployeeDevices(e.empId, d.deviceName, d.deviceId, d.devicePurchaseDate, d.category) FROM Employee e join e.devices d where d.id=?1")
    EmployeeDevices getUserDevices(int id);

    @Query(value = "select employee_emp_id from employee_devices where devices_device_id= :id",nativeQuery = true)
    String deleteByEmpDevicesId(@Param("id") int id);

    @Query(value = "select devices_device_id from employee_devices where employee_emp_id= :id",nativeQuery = true)
    int deviceId(@Param("id") String id);

    @Transactional
    @Modifying
    @Query(value = "delete from employee_devices where devices_device_id=:id",nativeQuery = true)
    public void deleteEmployeeByEmpDevice(@Param("id") int id);

    @Query(value="SELECT email FROM Employee WHERE empId= :id")
    public String getEmployeeMail(@Param("id") String empId);

    @Query(value = "select * from employee where emp_id=?1",nativeQuery = true)
    public Employee employee(String id);
//    @Transactional
//    @Modifying
//    @Query(value= "delete from employee_department where department=:dep",nativeQuery = true)
//    public void deleteDepartment(@Param("id") String dep);

}
