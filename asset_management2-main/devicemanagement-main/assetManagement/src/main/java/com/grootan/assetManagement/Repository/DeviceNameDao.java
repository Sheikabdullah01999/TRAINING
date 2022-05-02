package com.grootan.assetManagement.Repository;

import com.grootan.assetManagement.Model.DeviceName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceNameDao extends JpaRepository<DeviceName,Integer> {
}
