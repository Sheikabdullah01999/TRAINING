package com.grootan.assetManagement.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="deviceName",uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class DeviceName {
    private Integer id;
    @Id
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "deviceName")
    @JsonIgnore
    private Set<DeviceCategory> category = new HashSet<>();
}
