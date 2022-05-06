package com.grootan.assetManagement.Model;


import javax.persistence.*;

@Entity
@Table(name="deviceName",uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class DeviceName {

    @Id
    private String name;

    @ManyToOne()
    private DeviceCategory deviceCategory;

    public DeviceName()
    {

    }

    public DeviceName(String name, DeviceCategory deviceCategory) {
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
