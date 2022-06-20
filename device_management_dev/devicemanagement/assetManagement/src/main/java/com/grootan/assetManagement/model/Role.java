package com.grootan.assetManagement.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "role",uniqueConstraints = @UniqueConstraint(columnNames = {"id","roleName"}))
public class Role
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String roleName;
    private String roleDescription;
    public Role(String roleName) {
        super();
        this.roleName = roleName;
    }
    public Role()
    {

    }
}