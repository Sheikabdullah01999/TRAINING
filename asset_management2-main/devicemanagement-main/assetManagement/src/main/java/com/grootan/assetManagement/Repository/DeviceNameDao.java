package com.grootan.assetManagement.Repository;

import com.grootan.assetManagement.Model.Device;
import com.grootan.assetManagement.Model.DeviceName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DeviceNameDao extends JpaRepository<DeviceName,String> {
    @Query("SELECT u from DeviceName u where u.name=?1")
    DeviceName findByDeviceName(String name);
}
