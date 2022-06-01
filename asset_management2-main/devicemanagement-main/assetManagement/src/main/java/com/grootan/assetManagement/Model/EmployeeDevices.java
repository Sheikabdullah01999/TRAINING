package com.grootan.assetManagement.Model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeDevices {
    private String empId;
    private Integer id;
    private String deviceName;
    private Date devicePurchaseDate;
    private String category;

    public EmployeeDevices(String empId, String deviceName, Integer id, Date devicePurchaseDate, String category) {
        this.empId = empId;
        this.deviceName = deviceName;
        this.id = id;
        this.devicePurchaseDate = devicePurchaseDate;
        this.category = category;
    }
}
