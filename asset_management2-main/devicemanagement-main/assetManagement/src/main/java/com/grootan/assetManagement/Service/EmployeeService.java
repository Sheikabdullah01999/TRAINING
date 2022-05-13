package com.grootan.assetManagement.Service;

import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Model.*;
import com.grootan.assetManagement.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.grootan.assetManagement.Model.Constants.*;
import static com.grootan.assetManagement.Model.Constants.USER_ADD;

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

    //get all departments from the table
    public List<EmployeeDepartment> getAllEmpDepartments() {
        List<EmployeeDepartment> list=employeeDepartmentDao.findAll();
        return list;
    }

    //list all employees
    public List<Employee> getAllEmployees()
    {
        List<Employee> employeeList = employeeDao.findAll();
        if(employeeList.isEmpty())
        {
            throw new GeneralException("RECORD_NOT_FOUND");
        }
        return employeeList;
    }

    public Employee saveEmpDetails(Employee employeeDetails)
    {
        String employeeDevices="";
        String devices= employeeDetails.getEmpDevices();

        List<Device> device=new ArrayList<>();

        if(employeeDetails.getEmpDevices()==null)
        {
            employeeDevices="";
            device=new ArrayList<>();
        }
        else
        {
            List<Integer> id=getDeviceID(devices);
            String deviceList=empDevice(employeeDetails);
            employeeDevices=deviceList;
            for(int i=0;i<id.size();i++)
            {
                deviceDao.updateAssignStatus(id.get(i));
            }

            for(Integer deviceId : id)
            {
                device.add(new Device(deviceId));
            }

        }

        Employee employee = new Employee(employeeDetails.getEmpId(),
                employeeDetails.getEmpName(), employeeDetails.getEmail(),
                passwordEncoder.encode(employeeDetails.getEmpPassword()),
                employeeDetails.getEmpDepartment(),employeeDevices,
                employeeDetails.getAssignRole(),
                Arrays.asList(new Role(employeeDetails.getAssignRole())),device);

        return employee;
    }


    public String empDevice(Employee employee)
    {
        String devices=employee.getEmpDevices();
        List<Integer> list=getDeviceID(devices);
        List<String> deviceList =new ArrayList<>();
        for(int i=0;i<list.size();i++)
        {
            deviceList.add(deviceDao.getDeviceById(list.get(i)));
        }
        String updatedDevice="";
        for (String device:deviceList) {
            if (updatedDevice == "") {
                updatedDevice = updatedDevice + device;
                updatedDevice = device;
            } else {
                updatedDevice = updatedDevice + ";" + device;
            }

        }
        return updatedDevice;
    }

    //save employee details after check all details are  correct
    public Employee saveEmployee(Employee employeeDetails) {
        Employee employee=saveEmpDetails(employeeDetails);
        if(employeeDetails.getAssignRole().equals("ADMIN"))
        {
            String history = NEW_EMP_ID + employeeDetails.getEmpId() + ","
                    + EMP_NAME + employeeDetails.getEmpName();
            History newHistory = new History(service.currentUser(), ADMIN_ADD, history, service.DateAndTime());
            historyDao.save(newHistory);
            return employeeDao.save(employee);
        }
        else {
            String history = NEW_EMP_ID + employeeDetails.getEmpId() + ","
                    + EMP_NAME+ employeeDetails.getEmpName();
            History newHistory = new History(service.currentUser(), USER_ADD, history, service.DateAndTime());
            historyDao.save(newHistory);
            return employeeDao.save(employee);
        }
    }

    //get device by device id
    public List<Integer> getDeviceID(String device) {
        List<String> list = null;
        if (device != null) {

            list = Arrays.asList(device.split(","));
        }

        List<Integer> deviceId = new ArrayList<>();
        if(!list.isEmpty())
        {
            for (String empId : list) {

                String temp = "";
                for (int i = 0; i < empId.length(); i++) {
                    if (Character.isDigit(empId.charAt(i))) {
                        temp = temp + String.valueOf(empId.charAt(i));
                    }
                }
                if (temp != "") {
                    deviceId.add(Integer.parseInt(temp));
                }
            }
        }

        return deviceId;
    }

    // check given email id is valid or not
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

    //to check employee devices exists or not
    private boolean employeeDevicesExists(final String empDevices)
    {
        return employeeDao.findByEmpDevices(empDevices)!=null;
    }

    //to check department name already exits or not
    private boolean departmentExists(final String department)
    {
        return employeeDepartmentDao.findByDepartmentName(department)!=null;
    }


    public Boolean validate(Employee employeeDetails)
    {
        Boolean validEmail=isValid(employeeDetails.getEmail());
        if(!validEmail)
        {
            throw new GeneralException(INCORRECT_EMAIL+employeeDetails.getEmail());
        }

        if(emailExists(employeeDetails.getEmail()))
        {
            throw new GeneralException(EMP_EMAIL_EXISTS+employeeDetails.getEmail());
        }

        if(employeeIdExists(employeeDetails.getEmpId()))
        {
            throw new GeneralException(EMP_ID_EXISTS+employeeDetails.getEmpId());
        }

        if(employeeDevicesExists(employeeDetails.getEmpDevices()))
        {
            throw new GeneralException(DEVICE_ASSIGNED+employeeDetails.getEmpDevices());
        }
        return true;
    }

    // list all user with devices
    public List<EmployeeDevices> getUserDevices()
    {
        List<EmployeeDevices> userDevices = employeeDao.getUserDevice();
        return userDevices;
    }

    //get all device by employeeId
    public String getAllDevicesById(String EmpId)
    {
        return employeeDao.getEmpDevices(EmpId);
    }

    //find employee by employee id
    public Employee findEmployeeById(String id)
    {
        Employee employee = employeeDao.findByEmpId(id);
        if(employee.getEmpId()==null)
        {
            throw new GeneralException("No Records Found");
        }
        return employee;
    }

    //delete employee details
    public void deleteEmpDetails(String id)
    {
        currentLoggedInUserValidation(id);

        Employee employee = employeeDao.findByEmpId(id);
        String s=employee.getEmpDevices();

        List<Integer> deviceId=getDeviceID(s);

        if(employee.getEmpId()==null)
        {
            throw new GeneralException("No Records Found");
        }

        employeeDao.deleteByEmpId(id);

        for(Integer dId:deviceId)
        {
            deviceDao.updateAssignStatusAndDeviceStatus(dId);
        }
        String newDeletedDeviceHistory="employee Id "+id+"Records deleted ";
        String userName=service.currentUser();
        History history=new History(userName,USER_DELETE,newDeletedDeviceHistory,service.DateAndTime());
        historyDao.save(history);

    }

    //search by keyword using like
    public List<Employee> getByKeyword(String keyword){
        return employeeDao.findByKeyword(keyword);
    }

    //current log in user detail validation
    public void currentLoggedInUserValidation(String  id)
    {
        String currentUser=employeeDao.getEmployeeMail(id);
        if(currentUser.equals(service.currentUser()))
        {
            throw new GeneralException("cannot delete current logged in user");
        }
        else if(id.equals("G001"))
        {
            throw new GeneralException("cannot delete default admin account ");
        }
    }

    //save employee department
    public void saveEmpDepartment(EmployeeDepartment employeeDepartment) {
        String lower = employeeDepartment.getDepartment();
        String i = lower.toLowerCase();
        if(departmentExists(i))
        {
            throw new GeneralException("Department Name  Already Exists: "+employeeDepartment.getDepartment());
        }
        String newDeviceHistory="New Department Name: "+employeeDepartment.getDepartment();
        String userName=service.currentUser();
        History history=new History(userName,EMP_DEPARTMENT_ADD,newDeviceHistory,service.DateAndTime());
        historyDao.save(history);
        employeeDepartment.setDepartment(i);
        employeeDepartmentDao.save(employeeDepartment);
    }

    //delete empdevices by employeeId
    public void deleteEmpDevices(int id)
    {
        String employeeDevices = employeeDao.deleteByEmpDevicesId(id);
        String empDevices = employeeDao.getEmpDevices(employeeDevices);
        String devices = deviceDao.getDeviceById(id);
        String [] empDevicesById = empDevices.split(";");
        String newDevice="";
        for(int i=0;i<empDevicesById.length;i++)
        {
            if(devices.equals(empDevicesById[i]))
            {
                newDevice=newDevice+"";
            }
            else
            {
                if(newDevice=="")
                {
                    newDevice=newDevice+empDevicesById[i];
                }
                else
                {
                    newDevice = newDevice + ";" + empDevicesById[i];
                }

            }
        }
        employeeDao.updateEmployeeByEmpDevice(employeeDevices,newDevice);
        deviceDao.updateAssignStatusAndDeviceStatus(id);
        employeeDao.deleteEmployeeByEmpDevice(id);
    }

    //update employee details
    public Employee updateEmployee(Employee employeeDetails)
    {
        List<Integer> newDeviceSize = null;
        String newDevice=employeeDetails.getEmpDevices();
        if(newDevice!=null)
        {
            newDeviceSize=getDeviceID(newDevice);
        }

        String updatedDevice = updateEmployeeDevice(employeeDetails);

        List<Integer> deviceId=getDeviceID(updatedDevice);
        List<Device> device=new ArrayList<>();

        for(Integer Id : deviceId)
        {
            device.add(new Device(Id));
        }


        Employee employee = new Employee(employeeDetails.getEmpId(),
                employeeDetails.getEmpName(), employeeDetails.getEmail(),
                employeeDetails.getEmpPassword(), employeeDetails.getEmpDepartment(), updatedDevice,
                employeeDetails.getAssignRole(),
                Arrays.asList(new Role(employeeDetails.getAssignRole())),device);

        updateAssignStatus(updatedDevice);

        String updatedEmployeeHistory=updatedEmployeeHistory(employeeDetails,newDeviceSize);
        String userName=service.currentUser();
        History history=new History(userName,USER_UPDATED,updatedEmployeeHistory,service.DateAndTime());

        historyDao.save(history);
        return employeeDao.save(employee);
    }

    //updated device size
    public void updateAssignStatus(String updatedDevice)
    {
        List<Integer> deviceId=getDeviceID(updatedDevice);
        for(int i=0;i<deviceId.size();i++)
        {
            deviceDao.updateAssignStatus(deviceId.get(i));
        }
    }

    //update employee history
    public String updatedEmployeeHistory(Employee employeeDetails,List<Integer> newDeviceSize)
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

    //updated device
    public String updateEmployeeDevice(Employee employeeDetails)
    {
        String newDevice=employeeDetails.getEmpDevices();
        String updatedDevice = "";
        String deviceList=deviceDao.getDevice(employeeDetails.getEmpId());

        if(employeeDetails.getEmpDevices()!=null)
        {
            updatedDevice=newDevice+";"+deviceList;
        }
        else
        {
            updatedDevice=updatedDevice+deviceList;
        }

        return updatedDevice;

    }


}
