package com.grootan.assetManagement.repository;

import com.grootan.assetManagement.model.DeviceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceCategoryDao extends JpaRepository<DeviceCategory,String> {

    @Query("SELECT u from DeviceCategory u where u.category=?1")
    DeviceCategory findByDeviceCategory(String category);
    @Query("SELECT id from DeviceCategory u where u.category=?1")
    long findByDeviceCategoryId(String category);
}
