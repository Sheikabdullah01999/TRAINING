package com.grootan.assetManagement.Controller.RestController;

import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Model.Role;
import com.grootan.assetManagement.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RoleRestController {
    @Autowired
    RoleService roleService;

    @GetMapping("/role/list/role")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.getAllRoles());
    }

    @PostMapping("/role/save")
    public ResponseEntity<Object> saveRole(@RequestBody Role role) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(roleService.saveRoles(role));
        } catch (GeneralException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}