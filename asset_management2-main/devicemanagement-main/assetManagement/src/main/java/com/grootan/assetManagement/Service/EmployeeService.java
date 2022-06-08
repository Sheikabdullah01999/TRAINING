package com.grootan.assetManagement.Service;

import com.google.gson.Gson;
import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Model.*;
import com.grootan.assetManagement.Repository.*;
import com.grootan.assetManagement.Response;
import com.grootan.assetManagement.request.EmployeeRequest;
import org.aspectj.apache.bcel.classfile.InnerClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import static com.grootan.assetManagement.Model.Constants.*;


@Service
public class EmployeeService {
    @Autowired
    RoleDao roleDao;
    @Autowired
    DeviceDao deviceDao;
    @Autowired
    private EmployeeDao employeeDao;
    @Autowired
    EmployeeDepartmentDao employeeDepartmentDao;
    @Autowired
    HistoryDao historyDao;
    @Autowired
    private CommonService service;
    @Autowired
    private PasswordEncoder passwordEncoder;


    Logger logger = Logger.getLogger("com.grootan.assetManagement.Service");
    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }


    public void saveHistory(Object o,String constant)
    {
        String userName=service.currentUser();
        History saveHistory=new History(userName,constant,new Gson().toJson(o),service.DateAndTime());
        //  historyDao.save(saveHistory);

    }
    //get all departments from the table
    public ResponseEntity getAllEmpDepartments() {
        List<EmployeeDepartment> employeeList=employeeDepartmentDao.findAll();
        if (employeeList.isEmpty())
        {
            return new ResponseEntity(
                    new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()),
                            HttpStatus.NOT_FOUND.getReasonPhrase(), NO_RECORDS),
                    new HttpHeaders(),
                    HttpStatus.NOT_FOUND
            );
        }

        return   new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK.getReasonPhrase(), "department found", employeeList),
                new HttpHeaders(), HttpStatus.OK);
    }

    //list all employees
    public ResponseEntity getAllEmployees()
    {
        List<Employee> employeeList = employeeDao.findAll();
        if (employeeList.isEmpty())
        {
            return new ResponseEntity(
                    new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()),
                            HttpStatus.NOT_FOUND.getReasonPhrase(), NO_RECORDS),
                    new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        return   new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK.getReasonPhrase(), "employee found", employeeList),
                new HttpHeaders(), HttpStatus.OK);
    }

    public Employee saveEmpDetails(EmployeeRequest employeeDetails)
    {

        String devices= employeeDetails.getEmpDevices();

        List<Device> device=new ArrayList<>();

        if(devices!=null)
        {
            List<Integer> id=getDeviceID(devices);
            for(int i=0;i<id.size();i++)
            {
                deviceDao.updateAssignStatus(id.get(i));
            }

            for(Integer deviceId : id)
            {
                device.add(new Device(deviceId));
            }
        }

        EmployeeDepartment employeeDepartment=new EmployeeDepartment(employeeDetails.getEmpDepartment());

        Employee employee = new Employee(employeeDetails.getEmpId(),
                employeeDetails.getEmpName(), employeeDetails.getEmail(),
                passwordEncoder.encode(employeeDetails.getEmpPassword()),
                employeeDepartment,
                employeeDetails.getAssignRole(),
                Arrays.asList(new Role(employeeDetails.getAssignRole())),device,
                employeeDetails.getEmpDepartment());

        return employee;
    }

    public ResponseEntity validate(EmployeeRequest employeeDetails)
    {
        if(employeeDetails.getEmpPassword().isEmpty() || employeeDetails.getEmpDepartment().isEmpty()||
                employeeDetails.getAssignRole().isEmpty()||employeeDetails.getEmpId().isEmpty()||employeeDetails.getEmpName().isEmpty())
        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE),
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),"field should not empty"),
                    new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
        }

        Boolean validEmail=isValid(employeeDetails.getEmail());
        if(!validEmail)
        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE),
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),INCORRECT_EMAIL+employeeDetails.getEmail()),
                    new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
        }

        if(employeeDetails.getEmpPassword().length()<9)
        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE),
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),"password must be more than 8 character"),
                    new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
        }

        if(emailExists(employeeDetails.getEmail()))
        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE),
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),EMP_EMAIL_EXISTS+employeeDetails.getEmail()),
                    new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
        }

        if(employeeIdExists(employeeDetails.getEmpId()))
        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE),
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),EMP_ID_EXISTS+employeeDetails.getEmpId()),
                    new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
        }


        if(String.valueOf(employeeDetails.getEmpDevices()).equals(""))
        {
            return null;
        }

        if(deviceExists(employeeDetails.getEmpDevices()))
        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE),
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),"device un available"),
                    new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
        }

        return null;
    }

    private boolean deviceExists(String empDevices) {
        Boolean deviceExists=true;
        List<Integer> deviceID = getDeviceID(empDevices);
        for (Integer id:deviceID)
        {
            List<String> device=deviceDao.getDeviceByName();
            List<Integer> availableDeviceID = getDeviceID(String.valueOf(device));
            for(Integer availableID:availableDeviceID)
            {
                if(availableID==id)
                {
                    deviceExists=false;
                }
            }

        }
        return deviceExists;
    }


    //save employee details after check all details are  correct
    public ResponseEntity saveEmployee(EmployeeRequest employeeDetails) {
        ResponseEntity validate = validate(employeeDetails);
        if(validate != null){
            return validate;
        }

        Employee employee=saveEmpDetails(employeeDetails);

        employeeDao.save(employee);
        return new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.CREATED.value()), HttpStatus.CREATED.getReasonPhrase(), "successfully saved",employeeDetails),
                new HttpHeaders(), HttpStatus.CREATED);
    }

    //get device by device id
    public List<Integer> getDeviceID(String device)
    {
        List<String> list = null;
        if (device != null)
        {
            list = Arrays.asList(device.split(","));
        }

        List<Integer> deviceId = new ArrayList<>();
        assert list != null;
        if(!list.isEmpty())
        {
            for (String empId : list)
            {

                String temp = "";
                for (int i = 0; i < empId.length(); i++)
                {
                    if (Character.isDigit(empId.charAt(i)))
                    {
                        temp = temp + String.valueOf(empId.charAt(i));
                    }
                }
                if (temp != "")
                {
                    deviceId.add(Integer.parseInt(temp));
                }
            }
        }
        return deviceId;
    }

    // check given email id is valid or not

    //to check  email already exits or not
    private boolean emailExists(final String email)
    {
        return employeeDao.findByEmail(email)!=null;
    }

    //to check employee id  already exits or not
    private boolean employeeIdExists(final String empId)
    {
        return employeeDao.findByEmpId(empId)!=null;
    }


    private boolean departmentExists(final String department)
    {
        return employeeDepartmentDao.findByDepartmentName(department)!=null;
    }

    // list all user with devices
    public ResponseEntity getUserDevices()
    {
        List<EmployeeDevices> userDevices = employeeDao.getUserDevice();
        if(userDevices.isEmpty())
        {
            return new ResponseEntity(
                    new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()),
                            HttpStatus.NOT_FOUND.getReasonPhrase(), NO_RECORDS),
                    new HttpHeaders(),
                    HttpStatus.NOT_FOUND
            );
        }
        return   new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK.getReasonPhrase(), "deleted successful",userDevices),
                new HttpHeaders(),
                HttpStatus.OK);
    }

    //find employee by employee id
    public ResponseEntity findEmployeeById(String id)
    {
        Employee employee = employeeDao.findByEmpId(id);
        if(employee.getEmpId()==null)
        {
            return new ResponseEntity(
                    new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()),
                            HttpStatus.NOT_FOUND.getReasonPhrase(), NO_RECORDS),
                    new HttpHeaders(),
                    HttpStatus.NOT_FOUND
            );
        }
        return   new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK.getReasonPhrase(), "deleted successful",employee),
                new HttpHeaders(),
                HttpStatus.OK);
    }

    //delete employee details


    //search by keyword using like
    public List<Employee> getByKeyword(String keyword){
        return employeeDao.findByKeyword(keyword);
    }

    //current log in user detail validation
    public ResponseEntity currentLoggedInUserValidation(String  id)
    {
        String currentUser=employeeDao.getEmployeeMail(id);
        if(currentUser!=null)
        {
            if(currentUser.equals(service.currentUser()))
            {
                return   new ResponseEntity(
                        new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()), HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(), "can not delete current user"),
                        new HttpHeaders(),
                        HttpStatus.NOT_ACCEPTABLE);
            }
            else if(id.equals("G001"))
            {
                return new ResponseEntity(
                        new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()), HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(), "can not delete default admin account"),
                        new HttpHeaders(),
                        HttpStatus.NOT_ACCEPTABLE);
            }
        }

        return  null;
    }

    //save employee department


    //update employee details
    public ResponseEntity updateEmployee(EmployeeRequest employeeDetails)
    {
        ResponseEntity validate = validate(employeeDetails);
        if(validate != null){
            return validate;
        }

        List<Integer> updatedDeviceList = new ArrayList<>();

        List<Device> device=new ArrayList<>();

        if(employeeDetails.getEmpDevices()!=null)
        {
            updatedDeviceList=getDeviceID(employeeDetails.getEmpDevices());
        }
        List<Integer> existingDevice=deviceDao.deviceId(employeeDetails.getEmpId());

        if(existingDevice!=null)
        {
            updatedDeviceList.addAll(existingDevice);

            for(Integer Id : updatedDeviceList)
            {
                device.add(new Device(Id));
            }
            updateAssignStatus(updatedDeviceList);
        }

        EmployeeDepartment employeeDepartment=new EmployeeDepartment(employeeDetails.getEmpDepartment());

        Employee employee = new Employee(employeeDetails.getEmpId(),
                employeeDetails.getEmpName(), employeeDetails.getEmail(),
                employeeDetails.getEmpPassword(), employeeDepartment,
                employeeDetails.getAssignRole(),
                Arrays.asList(new Role(employeeDetails.getAssignRole())),device,employeeDetails.getEmpDepartment());

        String updatedEmployeeHistory=updatedEmployeeHistory(employeeDetails,updatedDeviceList);
        saveHistory(employeeDetails,USER_UPDATED);
        employeeDao.save(employee);
        return new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.CREATED.value()), HttpStatus.CREATED.getReasonPhrase(), "updated successfully"),
                new HttpHeaders(),
                HttpStatus.CREATED);
    }

    //updated device size
    public void updateAssignStatus(List<Integer> updatedDevice)
    {
        for(int i=0;i<updatedDevice.size();i++)
        {
            deviceDao.updateAssignStatus(updatedDevice.get(i));
        }
    }

    //update employee history
    public String updatedEmployeeHistory(EmployeeRequest employeeDetails,List<Integer> newDeviceSize)
    {
        String employeeId=employeeDetails.getEmpId();
        Employee emp=employeeDao.findByEmpId(employeeId);
        String updatedEmployeeHistory="EMP ID: "+employeeDetails.getEmpId();

        if(employeeDetails.getEmpDevices()!=null&&emp.getEmpDevices()!=null)
        {
            if(!emp.getEmpDevices().equals(employeeDetails.getEmpDevices())&&(newDeviceSize!=null))
            {
                int size=newDeviceSize.size();
                updatedEmployeeHistory=updatedEmployeeHistory+size+",new device added ";
            }
        }
        if(!emp.getEmpName().equalsIgnoreCase(employeeDetails.getEmpName()))
        {
            updatedEmployeeHistory=updatedEmployeeHistory+" ,Employee name changed from "
                    +emp.getEmpName()+" to "
                    +employeeDetails.getEmpName();
        }
        if(!emp.getEmpDepartment().equalsIgnoreCase(employeeDetails.getEmpDepartment()))
        {
            updatedEmployeeHistory=updatedEmployeeHistory+",Employee department changed from "
                    + emp.getEmpDepartment()+" to"+employeeDetails.getEmpDepartment();
        }
        if(!emp.getAssignRole().equalsIgnoreCase(employeeDetails.getAssignRole()))
        {
            updatedEmployeeHistory=updatedEmployeeHistory+",Employee Role changed from "
                    +  emp.getAssignRole()+" to "+employeeDetails.getAssignRole();
        }
        if(!emp.getEmail().equalsIgnoreCase(employeeDetails.getEmail()))
        {
            updatedEmployeeHistory=updatedEmployeeHistory+",Employee Email changed from "
                    +emp.getEmail()+" to "+employeeDetails.getEmail();
        }
        return updatedEmployeeHistory;
    }

    //delete employee devices by employeeId
    public ResponseEntity deleteEmpDevices(int id)
    {
        EmployeeDevices device=employeeDao.getUserDevices(id);
        if (device==null)
        {
            return new ResponseEntity(
                    new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()),
                            HttpStatus.NOT_FOUND.getReasonPhrase(), "no records found"),
                    new HttpHeaders(),
                    HttpStatus.NOT_FOUND
            );
        }

        deviceDao.updateAssignStatusAndDeviceStatus(id);
        employeeDao.deleteEmployeeByEmpDevice(id);
        return   new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK.getReasonPhrase(), "deleted successful"),
                new HttpHeaders(),
                HttpStatus.OK);
    }

    public ResponseEntity deleteEmployeeDepartment(String department)
    {
        EmployeeDepartment empDep= employeeDepartmentDao.findByDepartmentName(department);
        if(empDep==null)
        {
            return new ResponseEntity(
                    new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()),
                            HttpStatus.NOT_FOUND.getReasonPhrase(), "no records found"),
                    new HttpHeaders(),
                    HttpStatus.NOT_FOUND
            );
        }
        employeeDepartmentDao.deleteDepartment(department);
        return   new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK.getReasonPhrase(), "deleted successful"),
                new HttpHeaders(),
                HttpStatus.OK);
    }

    public ResponseEntity deleteEmpDetails(String empID)
    {
        if(employeeDao.findByEmpId(empID)==null)
        {
            return new ResponseEntity(
                    new Response<>(String.valueOf(HttpStatus.NOT_FOUND.value()),
                            HttpStatus.NOT_FOUND.getReasonPhrase(), "no records found"),
                    new HttpHeaders(),
                    HttpStatus.NOT_FOUND
            );
        }
        currentLoggedInUserValidation(empID);

        List<Integer> empDevice=deviceDao.deviceId(empID);
        employeeDao.deleteByEmpId(empID);
        for(Integer dId:empDevice)
        {
            deviceDao.updateAssignStatusAndDeviceStatus(dId);
        }
        // String newDeletedDeviceHistory="employee Id "+empID+"Records deleted ";
        saveHistory(empID,EMP_DELETE);

        return   new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.OK.value()), HttpStatus.OK.getReasonPhrase(), "employee deleted success"),
                new HttpHeaders(),
                HttpStatus.OK);
    }

    public ResponseEntity saveEmpDepartment(EmployeeDepartment employeeDepartment) {

        String lower = employeeDepartment.getDepartment().toLowerCase();
        if(employeeDepartment.getDepartment().isEmpty())
        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.NOT_ACCEPTABLE),
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),"field should not empty"),
                    new HttpHeaders(),HttpStatus.NOT_ACCEPTABLE);
        }

        if(departmentExists(lower))
        {
            return new ResponseEntity<>(new Response<>(String.valueOf(HttpStatus.CONFLICT),
                    HttpStatus.CONFLICT.getReasonPhrase(),"Department Name  Already Exists: "+employeeDepartment.getDepartment()),
                    new HttpHeaders(),HttpStatus.CONFLICT);
        }
        //String newDeviceHistory="New Department Name: "+employeeDepartment.getDepartment();
        saveHistory(employeeDepartment,EMP_DEPARTMENT_ADD);
        employeeDepartment.setDepartment(lower);
        logger.info("saved success");
        employeeDepartmentDao.save(employeeDepartment);
        return new ResponseEntity(
                new Response<>(String.valueOf(HttpStatus.CREATED.value()), HttpStatus.CREATED.getReasonPhrase(), "successfully saved",employeeDepartment),
                new HttpHeaders(),
                HttpStatus.CREATED);
    }

    public EmployeeDevices employeeDevices(int id)
    {
        EmployeeDevices empDevice=employeeDao.getUserDevices(id);
        return empDevice;
    }

}