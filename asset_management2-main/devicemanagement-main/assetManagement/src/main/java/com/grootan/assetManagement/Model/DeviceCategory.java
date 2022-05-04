package com.grootan.assetManagement.Model;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "category"))
public class DeviceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
