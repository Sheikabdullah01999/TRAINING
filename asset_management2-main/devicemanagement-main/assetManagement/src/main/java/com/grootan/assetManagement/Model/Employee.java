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
    private String assignRole;

    @Transient
    private String empDevices;



    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = {
                    @JoinColumn(name = "email")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "roleName")
            }
    )
    private Collection<Role> role;
    //@org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Device> devices;

    public Employee()
    {

    }

    public Employee(String empId, String empName, String email, String empPassword, String empDepartment, String assignRole, Collection<Role> role, List<Device> devices)
    {
        this.empId = empId;
        this.empName = empName;
        this.email = email;
        this.empPassword = empPassword;
        this.empDepartment = empDepartment;
        this.assignRole = assignRole;
        this.role = role;
        this.devices = devices;
    }
}
