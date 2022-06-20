package com.grootan.assetManagement.validation;

import com.grootan.assetManagement.exception.FieldEmptyException;
import com.grootan.assetManagement.exception.GeneralException;
import com.grootan.assetManagement.exception.ResourceNotFoundException;
import com.grootan.assetManagement.model.Device;
import com.grootan.assetManagement.model.Employee;
import com.grootan.assetManagement.model.EmployeeDepartment;
import com.grootan.assetManagement.model.Role;
import com.grootan.assetManagement.repository.DeviceDao;
import com.grootan.assetManagement.repository.EmployeeDao;
import com.grootan.assetManagement.repository.EmployeeDepartmentDao;
import com.grootan.assetManagement.repository.RoleDao;
import com.grootan.assetManagement.request.EmployeeRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.grootan.assetManagement.model.Constants.*;

public class EmployeeValidation {

        @Autowired
        private EmployeeDao employeeDao;
        @Autowired
        private EmployeeDepartmentDao employeeDepartmentDao;

        @Autowired
        private RoleDao roleDao;

        @Autowired
        private DeviceDao deviceDao;




}
