package com.grootan.assetManagement.service;

import com.google.gson.Gson;
import com.grootan.assetManagement.exception.GeneralException;
import com.grootan.assetManagement.model.History;
import com.grootan.assetManagement.model.Role;
import com.grootan.assetManagement.repository.HistoryDao;
import com.grootan.assetManagement.repository.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.grootan.assetManagement.model.Constants.*;

@Service
public class RoleService {
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private CommonService service;

    @Autowired
    private HistoryDao historyDao;

    //to check role name exists or not
    private boolean roleExists(final String roleName){
        return roleDao.findByName(roleName)!=null;
    }

    //get all roles
    public List<Role> getAllRoles()
    {
        List<Role> roles = (List<Role>) roleDao.findAll();
        if(roles.isEmpty())
        {
            throw new GeneralException("NO_RECORDS_FOUND");
        }
        return roles;
    }

    public Role saveRoles(Role role) {
        if(role.getRoleName()=="")
        {
            throw new GeneralException("Enter the roleName");
        }
        if(role.getRoleDescription()=="")
        {
            throw new GeneralException("Enter the roleDescription");
        }
        if(roleExists(role.getRoleName()))
        {
            throw new GeneralException(R0LE_EXISTS+role.getRoleName());
        }
        String roleHistory=NEW_ROLE+role.getRoleName();
        History history=new History(service.currentUser(),ROLE_ADD,new Gson().toJson(role),service.DateAndTime());
        System.out.println(role);
        historyDao.save(history);
        return roleDao.save(role);
    }
}