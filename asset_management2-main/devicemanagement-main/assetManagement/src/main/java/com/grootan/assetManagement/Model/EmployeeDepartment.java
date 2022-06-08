package com.grootan.assetManagement.Model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"id","department"}))
public class EmployeeDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String department;

    public EmployeeDepartment() {

    }
    public EmployeeDepartment(String dep)
    {
        this.department=dep;
    }

}