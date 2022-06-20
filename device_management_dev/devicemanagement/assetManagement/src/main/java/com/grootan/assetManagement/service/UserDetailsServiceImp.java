package com.grootan.assetManagement.service;

import com.grootan.assetManagement.model.Employee;
import com.grootan.assetManagement.model.Role;
import com.grootan.assetManagement.repository.EmployeeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
    String UserName="";
    @Autowired
    EmployeeDao employeeDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserName = username;
        Employee employee = employeeDao.findByEmail(username);
        if(employee == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(employee.getEmail(), employee.getEmpPassword(), mapRolesToAuthorities(employee.getRole()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
    }
}
