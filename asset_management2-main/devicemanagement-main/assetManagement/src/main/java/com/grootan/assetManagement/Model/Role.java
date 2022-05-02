package com.grootan.assetManagement.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "auth_role",uniqueConstraints = @UniqueConstraint(columnNames = {"roleName"}))
public class Role {
    @Id
    private String roleName;
    private String roleDescription;

    public Role(Long id,String roleName, String roleDescription)
    {

    }
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public Role(String roleName) {
        super();
        this.roleName = roleName;
    }
    public Role()
    {

    }
}
