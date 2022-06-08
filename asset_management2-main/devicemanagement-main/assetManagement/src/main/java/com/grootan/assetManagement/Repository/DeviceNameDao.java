package com.grootan.assetManagement.Repository;

import com.grootan.assetManagement.Model.Device;
import com.grootan.assetManagement.Model.DeviceName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface DeviceNameDao extends JpaRepository<DeviceName,String> {
    @Query("SELECT u from DeviceName u where u.name=?1")
    DeviceName findByDeviceName(String name);
    @Query(value = "select name from device_name where device_category_id=?1",nativeQuery = true)
    public List<String> getDeviceNames(long id);
    @Query(value="select id from device_category where category = ?1",nativeQuery = true)
    public int getDeviceCategoryId(String name);
}