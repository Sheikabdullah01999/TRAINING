package com.grootan.assetManagement.request;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DeviceNameRequest {

    private String name;

    private DeviceCategoryRequest deviceCategory;

    public DeviceNameRequest(String name, DeviceCategoryRequest deviceCategory) {
        this.name = name;
        this.deviceCategory = deviceCategory;
    }

    public DeviceCategoryRequest getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(DeviceCategoryRequest deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
