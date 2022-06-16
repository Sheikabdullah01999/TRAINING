package com.grootan.assetManagement.request;

import com.grootan.assetManagement.Model.DeviceCategory;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DeviceNameRequest {

    private String name;

    private DeviceCategory deviceCategory;

    public DeviceNameRequest(String name, DeviceCategory deviceCategory) {
        this.name = name;
        this.deviceCategory = deviceCategory;
    }

    public DeviceCategory getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(DeviceCategory deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
