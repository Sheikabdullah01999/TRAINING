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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
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
}
