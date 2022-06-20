package com.grootan.assetManagement.repository;

import com.grootan.assetManagement.model.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDao extends CrudRepository<Role,String> {
    @Query("SELECT u FROM Role u where u.roleName=?1")
    Role findByName(@Param("roleName") String roleName);
}
