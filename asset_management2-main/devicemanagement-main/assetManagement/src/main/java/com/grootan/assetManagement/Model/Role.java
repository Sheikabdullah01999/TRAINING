package com.grootan.assetManagement.Model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Getter
@Setter
@Table(name = "role",uniqueConstraints = @UniqueConstraint(columnNames = {"roleName"}))
public class Role {
    @Id
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
