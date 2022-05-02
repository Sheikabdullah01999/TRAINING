package com.grootan.assetManagement.Repository;

import com.grootan.assetManagement.Model.DeviceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceCategoryDao extends JpaRepository<DeviceCategory,Integer> {
}
