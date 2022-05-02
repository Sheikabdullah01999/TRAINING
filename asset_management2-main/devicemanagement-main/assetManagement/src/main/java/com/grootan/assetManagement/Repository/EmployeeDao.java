package com.grootan.assetManagement.Repository;

import com.grootan.assetManagement.Model.Employee;
import com.grootan.assetManagement.Model.Response;
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

    @Query(value = "select * from employee s where s.emp_name like %:keyword% or s.emp_id like %:keyword% or s.emp_department like %:keyword% or s.assign_role like %:keyword%", nativeQuery = true)
    List<Employee> findByKeyword(@Param("keyword") String keyword);

    @Transactional
    @Modifying
    @Query(value="DELETE FROM Employee u where u.empId= :id")
    int deleteByEmpId(@Param("id") String empId);

    @Query("SELECT new com.grootan.assetManagement.Model.Response(e.empId, d.deviceName, d.id, d.devicePurchaseDate, d.category) FROM Employee e join e.devices d")
    List<Response> getUserDevice();

    @Transactional
    @Modifying
    @Query("UPDATE Employee SET empDevices= :empDevices where empId= :id")
    public void updateEmployeeByEmpDevice(@Param("id") String empId,@Param("empDevices") String empDevices);

    @Query(value="SELECT empDevices FROM Employee WHERE empId= :id")
    public String getEmpDevices(@Param("id") String empId);
}
