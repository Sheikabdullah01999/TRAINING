package com.grootan.assetManagement.Service;

import com.grootan.assetManagement.Model.Role;
import com.grootan.assetManagement.Repository.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleDao roleDao;

    public Role createNewRole(Role role) {
        return roleDao.save(role);
    }
}
