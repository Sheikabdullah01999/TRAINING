package com.grootan.assetManagement.Model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "category"))
public class DeviceCategory {

    private Integer id;

    @Id
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "DeviceName_DeviceCategory",
            joinColumns = { @JoinColumn(name = "device_category") },
            inverseJoinColumns = { @JoinColumn(name = "device_name") })
    private Set<DeviceName> deviceName = new HashSet<>();
}
