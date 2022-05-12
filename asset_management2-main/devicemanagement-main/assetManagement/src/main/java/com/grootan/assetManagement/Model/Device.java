package com.grootan.assetManagement.Model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;


@Entity(name = "Device")
@Getter
@Setter
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
        public Device()
        {

        }

}
