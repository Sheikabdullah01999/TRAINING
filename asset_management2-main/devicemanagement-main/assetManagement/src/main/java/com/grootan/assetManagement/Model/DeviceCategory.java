package com.grootan.assetManagement.Model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "category"))
public class DeviceCategory {


    @Id
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public DeviceCategory(String category) {
        this.category = category;
    }

    public DeviceCategory()
    {

    }

}
