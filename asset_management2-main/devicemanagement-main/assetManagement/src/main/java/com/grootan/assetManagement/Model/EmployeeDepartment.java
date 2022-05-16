package com.grootan.assetManagement.Model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "department"))
public class EmployeeDepartment {
    @Id
    private String department;

    public EmployeeDepartment() {

    }
    public EmployeeDepartment(String dep)
    {
        this.department=dep;
    }

}
