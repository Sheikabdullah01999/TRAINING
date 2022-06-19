package com.grootan.assetManagement.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Component
@Table(name = "employee",uniqueConstraints = @UniqueConstraint(columnNames = {"email","empId"}))
public class Employee
{
    @Id
    private String empId;
    private String email;

    private String empName;
    private String empPassword;
    private String assignRole;
    private String empDepartment;

    public Employee(String empId, String email, String empName, String empPassword, String assignRole, String empDepartment, EmployeeDepartment department, Set<Role> role, List<Device> devices) {
        this.empId = empId;
        this.email = email;
        this.empName = empName;
        this.empPassword = empPassword;
        this.assignRole = assignRole;
        this.empDepartment = empDepartment;
        this.department = department;
        this.role = role;
        this.devices = devices;
    }

    @OneToOne(fetch=FetchType.LAZY,cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    private EmployeeDepartment department;


    @Transient
    private String empDevices;
    @JoinColumn(insertable = false,updatable = false)
    @ManyToMany(fetch = FetchType.EAGER,cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH
    })
    @JoinTable(name = "user_role",
            joinColumns = {
                    @JoinColumn(name = "emp_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "role_id")
            }
    )
    private Set<Role> role;
    @JoinColumn(insertable = true,updatable = true)
    @OneToMany(fetch = FetchType.EAGER,cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH})
    private List<Device> devices;

    public Employee()
    {

    }

}