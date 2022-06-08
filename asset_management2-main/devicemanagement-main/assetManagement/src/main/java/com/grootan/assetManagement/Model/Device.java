package com.grootan.assetManagement.Model;

import jdk.jfr.Timestamp;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.time.Instant;


@Entity(name = "Device")
@Getter
@Setter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "manufacturedId"))
public class Device {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
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

        public Device(String manufacturedId, String category, String deviceName, Date devicePurchaseDate, String assignStatus, String deviceStatus) {
                this.manufacturedId = manufacturedId;
                this.category = category;
                this.deviceName = deviceName;
                this.devicePurchaseDate = devicePurchaseDate;
                this.assignStatus = assignStatus;
                this.deviceStatus = deviceStatus;
        }

        public Device(Integer id) {
                this.id = id;
        }
        public Device()
        {

        }

}