package com.grootan.assetManagement.Model;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeDevices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String empId;
    private Integer deviceId;
    private String deviceName;
    private Date devicePurchaseDate;
    private String category;

    public EmployeeDevices(String empId, String deviceName, Integer id, Date devicePurchaseDate, String category) {
        this.empId = empId;
        this.deviceName = deviceName;
        this.deviceId = id;
        this.devicePurchaseDate = devicePurchaseDate;
        this.category = category;
    }
}