package com.grootan.assetManagement.Model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "category"))
public class DeviceCategory {


    @Id
    private String category;

    public DeviceCategory()
    {

    }
}
