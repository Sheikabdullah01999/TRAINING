package com.grootan.assetManagement.Repository;

import com.grootan.assetManagement.Model.Device;
import com.grootan.assetManagement.Model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;


@Repository
public interface DeviceDao extends JpaRepository<Device,Integer>
{
    @Query("SELECT u FROM Device u where u.manufacturedId=?1")
    Device findByDeviceId(@Param("id") String id);
    @Query("SELECT u from Device u Where u.category =?1")
    public List<Device> getDeviceByCategory(@Param("deviceName") String deviceName);

    @Query("SELECT u from Device u Where u.category =?1 and u.deviceStatus=?2")
    public List<Device> getDamagedDevice(@Param("deviceName") String deviceName,@Param("deviceStatus") String status);

    @Query("SELECT DISTINCT category from Device")
    public List<String> getDevice();

    @Query("SELECT DISTINCT id,deviceName,category from Device where assignStatus='unassigned' and deviceStatus='new'")
    public List<String> getDeviceByName();

    @Transactional
    @Modifying
    @Query("UPDATE  Device u set u.assignStatus = 'assigned' , u.deviceStatus = 'working' where id=?1")
    public void updateAssignStatus(@Param("id") Integer id);

    @Transactional
    @Modifying
    @Query("UPDATE  Device u set u.assignStatus = 'unassigned' , u.deviceStatus = 'new' where id=?1")
    public void updateAssignStatusAndDeviceStatus(@Param("id") Integer id);

    @Query("Select  empDevices from Employee u where u.empId = :id")
    public String getDevice(@Param("id") String id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "delete  FROM employee_devices where devices_id=?1")
    void deleteForiegnKey(@Param("id") Integer id);

    @Query("SELECT DISTINCT id,deviceName,category from Device")
    public Device getEmpDevice();

    @Query("SELECT DISTINCT id,deviceName,category from Device where id=?1")
    public String getDeviceById(@Param("id") Integer id);

    @Query("SELECT id,deviceName,category from Device where id=?1")
    public Device getDeviceId(@Param("id") Integer id);

    @Query(value="SELECT employee_emp_id FROM employee_devices where devices_id=?1",nativeQuery = true)
    public String getEmpId(@Param("id") Integer id);
}
