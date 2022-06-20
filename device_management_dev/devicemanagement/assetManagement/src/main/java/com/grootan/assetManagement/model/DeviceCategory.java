package com.grootan.assetManagement.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "category"))
public class DeviceCategory {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String category;

    public DeviceCategory(String category) {
        this.category = category;
    }

    public DeviceCategory(long id, String category) {
        this.id = id;
        this.category = category;
    }

    public DeviceCategory()
    {

    }

}