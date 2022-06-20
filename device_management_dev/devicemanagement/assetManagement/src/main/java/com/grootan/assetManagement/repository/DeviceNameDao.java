package com.grootan.assetManagement.repository;


import com.grootan.assetManagement.model.DeviceName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DeviceNameDao extends JpaRepository<DeviceName,String> {
    @Query("SELECT u from DeviceName u where u.name=?1")
    DeviceName findByDeviceName(String name);
    @Query(value = "select name from device_name where device_category_id=?1",nativeQuery = true)
    public List<String> getDeviceNames(long id);
    @Query(value="select id from device_category where category = ?1",nativeQuery = true)
    public Integer getDeviceCategoryId(String name);
}