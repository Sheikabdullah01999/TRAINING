package com.grootan.assetManagement.Model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="deviceName",uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class DeviceName {

    @Id
    private long id;
    private String name;

    public DeviceName(String name) {
        this.name = name;
    }

    @ManyToOne()
    @JoinTable(name = "deviceCategory_deviceName",
            joinColumns = @JoinColumn(name = "deviceName_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "deviceCategory_id",referencedColumnName = "id"))
    private DeviceCategory deviceCategory;

    public DeviceName()
    {

    }
    public DeviceName(String name, DeviceCategory deviceCategory) {
        this.name = name;
        this.deviceCategory = deviceCategory;
    }
}
