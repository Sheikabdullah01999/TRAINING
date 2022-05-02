package com.grootan.assetManagement.Model;

import javax.persistence.*;
import java.sql.Date;


@Entity(name = "Device")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "manufacturedId"))
public class Device {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Integer id;

        @Column(name="manufacturedId")

        private String manufacturedId;



    @Column(name = "category")
        private String category;

        @Column(name = "deviceName")
        private String deviceName;

        @Column(name = "devicePurchaseDate")
        private Date devicePurchaseDate;

        @Column(name = "assign_status")
        private String assignStatus;

        @Column(name = "deviceStatus")
        private String deviceStatus;

//        @ManyToOne(fetch = FetchType.LAZY)
//        private Employee employee;

    public Device(Integer id) {
        this.id = id;
    }

    public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public Date getDevicePurchaseDate() {
            return devicePurchaseDate;
        }

        public void setDevicePurchaseDate(Date devicePurchaseDate) {
            this.devicePurchaseDate = devicePurchaseDate;
        }

    public String getAssignStatus() {
        return assignStatus;
    }

    public void setAssignStatus(String assignStatus) {
        this.assignStatus = assignStatus;
    }

        public String getDeviceStatus() {
            return deviceStatus;
        }

        public void setDeviceStatus(String deviceStatus) {
            this.deviceStatus = deviceStatus;
        }

    public String getManufacturedId() {
        return manufacturedId;
    }

    public void setManufacturedId(String manufacturedId) {
        this.manufacturedId = manufacturedId;
    }

        public Device()
        {

        }

}
