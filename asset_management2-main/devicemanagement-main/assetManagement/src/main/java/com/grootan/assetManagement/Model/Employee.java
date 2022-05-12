package com.grootan.assetManagement.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Component
@Table(name = "employee",uniqueConstraints = @UniqueConstraint(columnNames = {"email", "empId"}))
public class Employee {
    private String email;
    @Id
    private String empId;
    private String empName;
    private String empPassword;
    private String empDepartment;
    private String empDevices;
    private String assignRole;


    public Employee()
    {

    }


    public Employee(String empId, String empName, String email, String empPassword, String empDepartment, String empDevices, String assignRole, Collection<Role> role, List<Device> devices) {
        this.empId = empId;
        this.empName = empName;
        this.email = email;
        this.empPassword = empPassword;
        this.empDepartment = empDepartment;
        this.empDevices = empDevices;
        this.assignRole = assignRole;
        this.role = role;
        this.devices = devices;
    }

    public Employee(String empId, String empName, String email, String empPassword, String empDepartment,  String assignRole,Collection<Role> role ,List<Device> devices) {
        this.email = email;
        this.empId = empId;
        this.empName = empName;
        this.empPassword = empPassword;
        this.empDepartment = empDepartment;
        this.assignRole = assignRole;
        this.role = role;
        this.devices = devices;
    }
    @ManyToMany
    @JoinTable(name = "auth_user_role",
            joinColumns = {
                    @JoinColumn(name = "email")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "roleName")
            }
    )

    private Collection<Role> role;
    @OneToMany()
    private List<Device> devices;
}
