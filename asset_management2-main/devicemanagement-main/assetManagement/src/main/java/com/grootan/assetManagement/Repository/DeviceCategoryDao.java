package com.grootan.assetManagement.Repository;

import com.grootan.assetManagement.Model.Device;
import com.grootan.assetManagement.Model.DeviceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface DeviceCategoryDao extends JpaRepository<DeviceCategory,Long> {

    @Query("SELECT u from DeviceCategory u where u.category=?1")
    DeviceCategory findByDeviceCategory(String category);
    @Query("SELECT id from DeviceCategory u where u.category=?1")
    long findByDeviceCategoryId(String category);
    @Query(value = "select * from device_category where id=?1",nativeQuery = true)
    public DeviceCategory getByDeviceCategoryId(Long id);
    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "delete FROM device_category_device_name where device_category_id=?1")
    void deleteDeviceCategoryForiegnKey(@Param("id") Long id);
}
