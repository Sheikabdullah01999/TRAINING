package com.grootan.assetManagement.Service;

import com.grootan.assetManagement.Model.*;
import com.grootan.assetManagement.Repository.*;
import com.grootan.assetManagement.Exception.ResourceNotFoundException;
import com.grootan.assetManagement.Exception.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

import static com.grootan.assetManagement.Model.Constants.*;

@Service
public class AdminService {
    String UserName;
    @Autowired
    private AdminDao adminDao;

    @Autowired
    private Employee employee;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private DeviceCategoryDao deviceCategoryDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    HistoryDao historyDao;

    @Autowired
    private DeviceNameDao deviceNameDao;

    @Autowired
    private EmployeeDepartmentDao employeeDepartmentDao;

    public Authentication getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

  public void initRoleAndUser() {

        Role adminRole = new Role();
        adminRole.setRoleName("ADMIN");
        adminRole.setRoleDescription("Admin role");
        roleDao.save(adminRole);

        Role userRole = new Role();
        userRole.setRoleName("USER");
        userRole.setRoleDescription("Default role for newly created record");
        roleDao.save(userRole);

        Employee adminUser = new Employee();
        adminUser.setEmpId("G001");
        adminUser.setEmpName("grootan");
        adminUser.setEmail("grootan@gmail.com");
        adminUser.setEmpPassword(getEncodedPassword("gr00tan"));
        adminUser.setEmpDepartment("admin");
        adminUser.setEmpDevices("");
        adminUser.setAssignRole(adminRole.getRoleName());
        Collection<Role> adminRoles = new ArrayList<>();
        adminRoles.add(adminRole);
        adminUser.setRole(adminRoles);
        adminDao.save(adminUser);
    }

    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }
    private boolean emailExists(final String email)
    {
        return employeeDao.findByEmail(email)!=null;
    }
    private boolean employeeIdExists(final String empId)
    {
        return employeeDao.findByEmpId(empId)!=null;
    }
    private boolean deviceIdExists(final String id) { return deviceDao.findByDeviceId(id)!=null; }
    private boolean deviceNameExists(final String name){ return deviceNameDao.findByDeviceName(name)!=null; }
    private boolean employeeDevicesExists(final String empDevices)
    {
        return employeeDao.findByEmpDevices(empDevices)!=null;
    }

    private boolean deviceCategoryExists(final String category)
    {
        return deviceCategoryDao.findByDeviceCategory(category)!=null;
    }
    private boolean departmentExists(final String department)
    {
        return employeeDepartmentDao.findByDepartmentName(department)!=null;
    }
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

    public String currentTimeAndDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        return dateFormat.format(new Date()).toString();
    }

    public String currentUser()
    {
        Authentication authentication=getCurrentUser();
       return  authentication.getName();
    }

    public Boolean validate(Employee employeeDetails)
    {
        Boolean validEmail=isValid(employeeDetails.getEmail());
        if(!validEmail)
        {
            throw new UserAlreadyExistException("Incorrect email: "+employeeDetails.getEmail());
        }

        if(emailExists(employeeDetails.getEmail()))
        {
            throw new UserAlreadyExistException("There is an account with that email address: "+employeeDetails.getEmail());
        }

        if(employeeIdExists(employeeDetails.getEmpId()))
        {
            throw new UserAlreadyExistException("There is an account with that EmployeeId: "+employeeDetails.getEmpId());
        }

        if(employeeDevicesExists(employeeDetails.getEmpDevices()))
        {
            throw new UserAlreadyExistException("These devices are already assigned: "+employeeDetails.getEmpDevices());
        }
        return true;
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


    public Employee saveEmployee(Employee employeeDetails) {

        Employee employee=saveEmpDetails(employeeDetails);


        String history="New employee  Id:"+employeeDetails.getEmpId()+","
                + "  Name: "+employeeDetails.getEmpName();
        History newHistory=new History(currentUser(),ADD,history,currentTimeAndDate());
        historyDao.save(newHistory);

        return employeeDao.save(employee);
    }

    public Role saveRoles(Role role) {
        if(emailExists(role.getRoleName()))
        {
            throw new UserAlreadyExistException("role exists: "+role.getRoleName());
        }
        String roleHistory="New role "+role.getRoleName();
        History history=new History(currentUser(),ADD,roleHistory,currentTimeAndDate());
        historyDao.save(history);
        return roleDao.save(role);
    }
    public List<Employee> getAllEmployees()
    {
        return employeeDao.findAll();
    }

    public List<Role> getAllRoles()
    {
        return (List<Role>) roleDao.findAll();
    }


    public List<Device> getDevice(String device)
    {
        List<Device> list=new ArrayList<>();
        list= deviceDao.getDeviceByCategory(device);
        if(list.isEmpty())
        {
            throw new ResourceNotFoundException("NO_RECORDS FOUND");
        }

        return list;

    }

    public void addDeviceDetails(Device device) {

      if(deviceIdExists(device.getManufacturedId()))
      {
          throw new UserAlreadyExistException("There is an product with that ManufactureId: "+device.getManufacturedId());
      }
      String deviceHistory="New device  name:  "+device.getDeviceName()+","
              +"  device manufacture id:"+device.getManufacturedId();
        History history=new History(currentUser(),ADD,deviceHistory,currentTimeAndDate());
      historyDao.save(history);
      deviceDao.save(device);
    }

    public void updateDeviceDetails(Device device) {

        String deviceHistory="New device  name:  "+device.getDeviceName()+","
                +"  device manufacture id:"+device.getManufacturedId();

        String userName=currentUser();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        History history=new History(currentUser(),UPDATED,deviceHistory,currentTimeAndDate());
        historyDao.save(history);
        deviceDao.save(device);
    }

    public List<String> getAllDevices()
    {
        return (List<String>) deviceDao.getDevice();
    }

    public List<String> getAllDevicesByName()
    {
        return (List<String>) deviceDao.getDeviceByName();
    }

    public List<Response> getUserDevices()
    {
        return (List<Response>) employeeDao.getUserDevice();
    }

    public List<Device> getDamagedDevice(String device, String status) {
        List<Device> list=new ArrayList<>();
        list= deviceDao.getDamagedDevice(device,status);
        if(list.isEmpty())
        {
            throw new ResourceNotFoundException("NO_RECORDS FOUND");
        }

        return list;

    }

    public void deleteDeviceDetails(Integer id) {

      Device device=deviceDao.findById(id).orElseThrow(()-> new ResourceNotFoundException("no record found") );
      String empDevices = device.getId()+","+device.getDeviceName()+","+device.getCategory();
      String empId = deviceDao.getEmpId(id);
      String empDevicesById = employeeDao.getEmpDevices(empId);

      if(empDevicesById!=null)
      {
          String [] empDevicesByIds = empDevicesById.split(";");

          String newDevice="";
          for(int i=0;i<empDevicesByIds.length;i++)
          {
              if(empDevices.equals(empDevicesByIds[i]))
              {
                  newDevice=newDevice+"";
              }
              else
              {
                  newDevice=newDevice+empDevicesByIds[i];
              }
          }
          employeeDao.updateEmployeeByEmpDevice(empId,newDevice);
          String deviceDeleteHistory="device id:"+device.getId()+","
                  +"device name: "+device.getDeviceName();
          History history=new History(currentUser(),DELETE,deviceDeleteHistory,currentTimeAndDate());


          deviceDao.deleteForiegnKey(id);
          deviceDao.deleteById(id);
          historyDao.save(history);
      }
      else
      {
          String deviceDeleteHistory="device id:"+device.getId()+","
                  +"device name: "+device.getDeviceName();

          String userName=currentUser();
          DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
          String dateString = dateFormat.format(new Date()).toString();
          History history=new History(userName,DELETE,deviceDeleteHistory,dateString);
          historyDao.save(history);
          deviceDao.deleteForiegnKey(id);
          deviceDao.deleteById(id);
      }

    }

    public Optional<Device> findDeviceById(int id)
    {
        Optional<Device> device = deviceDao.findById(id);
        if(device.isEmpty())
        {
            throw new ResourceNotFoundException("No Records Found");
        }
        return device;
    }

    public Employee findEmployeeById(String id)
    {
        Employee employee = employeeDao.findByEmpId(id);
        if(employee.getEmpId()==null)
        {
            throw new ResourceNotFoundException("No Records Found");
        }
        return employee;
    }

    public Employee updateEmployee(Employee employeeDetails)
    {
        List<Integer> newDeviceSize = null;
        String newDevice=employeeDetails.getEmpDevices();
        if(newDevice!=null)
        {
          newDeviceSize=getDeviceID(newDevice);
        }

        String updatedDevice = "";
        String employeeId=employeeDetails.getEmpId();
        String deviceList=deviceDao.getDevice(employeeId);

        if(employeeDetails.getEmpDevices()!=null)
        {
            updatedDevice=newDevice+";"+deviceList;
        }
        else
        {
            updatedDevice=updatedDevice+deviceList;
        }

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

        for(int i=0;i<deviceId.size();i++)
        {
            deviceDao.updateAssignStatus(deviceId.get(i));
        }
        Employee emp=employeeDao.findByEmpId(employeeId);


        String updatedEmployeeHistory="EMP ID:"+employeeDetails.getEmpId();

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
                    + emp.getEmpDepartment()+"to"+employeeDetails.getEmpDepartment();
        }
        if(!emp.getAssignRole().equalsIgnoreCase(employeeDetails.getAssignRole()))
        {
            updatedEmployeeHistory=updatedEmployeeHistory+",Employee Role changed from "
                    +  emp.getAssignRole()+"to "+employeeDetails.getAssignRole();
        }

        String userName=currentUser();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        History history=new History(userName,UPDATED,updatedEmployeeHistory,dateString);
        historyDao.save(history);
        return employeeDao.save(employee);
    }

    public void currentLoggedInUserValidation(String  id)
    {
        String currentUser=employeeDao.getEmployeeMail(id);
        if(currentUser.equals(currentUser()))
        {
            throw new UserAlreadyExistException("cannot delete current logged in user");
        }
        else if(id.equals("G001"))
        {
            throw new UserAlreadyExistException("cannot delete default admin account ");
        }
    }

    public void deleteEmpDetails(String id)
    {
        currentLoggedInUserValidation(id);

        Employee employee = employeeDao.findByEmpId(id);
        String s=employee.getEmpDevices();

        List<Integer> deviceId=getDeviceID(s);

        if(employee.getEmpId()==null)
        {
            throw new ResourceNotFoundException("No Records Found");
        }

        employeeDao.deleteByEmpId(id);

        for(Integer dId:deviceId)
        {
            deviceDao.updateAssignStatusAndDeviceStatus(dId);
        }
        String newDeletedDeviceHistory="employee Id "+id+"Records deleted ";
        String userName=currentUser();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        History history=new History(userName,DELETE,newDeletedDeviceHistory,dateString);
        historyDao.save(history);

    }

    public Employee getEmployeeById()
    {
       Employee employee = employeeDao.findByEmail(UserName);
       return employee;
    }

    public List<Employee> getByKeyword(String keyword){
        return employeeDao.findByKeyword(keyword);
    }


    public void saveDeviceCategory(DeviceCategory category) {
        String lower = category.getCategory();
        String i = lower.toLowerCase();
      if(deviceCategoryExists(i))
      {
          throw new UserAlreadyExistException("This category is already exists: "+category.getCategory());
      }
        String newDeviceHistory="New Device category: "+category.getCategory();
        LocalDateTime now=LocalDateTime.now();

        String userName=currentUser();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        History history=new History(userName,ADD,newDeviceHistory,dateString);
        historyDao.save(history);
        category.setCategory(i);
        deviceCategoryDao.save(category);
    }

    public List<DeviceCategory> getCategory() {

      List<DeviceCategory> list=deviceCategoryDao.findAll();
      return list;
    }

    public void saveDeviceName(DeviceName deviceName)
    {
        String lower = deviceName.getName();
        String i = lower.toLowerCase();
        if(deviceNameExists(i))
        {
            throw new UserAlreadyExistException("This Product Name is Already Exists: "+deviceName.getName());
        }
        String newDeviceHistory="New Device  Name: "+deviceName.getName();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        String userName=currentUser();
        History history=new History(userName,ADD,newDeviceHistory,dateString);
        historyDao.save(history);
        deviceName.setName(i);
        deviceNameDao.save(deviceName);
    }

    public List<DeviceName> getName()
    {
        List<DeviceName> list=deviceNameDao.findAll();
        return list;
    }

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


    //get history from table
    public List<History> getHistory() {
        List<History> historyList = historyDao.findAll();
        return historyList;
    }

    //save employee department
    public void saveEmpDepartment(EmployeeDepartment employeeDepartment) {
        String lower = employeeDepartment.getDepartment();
        String i = lower.toLowerCase();
        if(departmentExists(i))
        {
            throw new UserAlreadyExistException("Department Name  Already Exists: "+employeeDepartment.getDepartment());
        }

        String newDeviceHistory="New Department Name: "+employeeDepartment.getDepartment();
        String userName=currentUser();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        History history=new History(userName,ADD,newDeviceHistory,dateString);
        historyDao.save(history);
        employeeDepartment.setDepartment(i);
        employeeDepartmentDao.save(employeeDepartment);
    }
    //get all departments from the table
    public List<EmployeeDepartment> getAllEmpDepartments() {
        List<EmployeeDepartment> list=employeeDepartmentDao.findAll();
        return list;
    }
}

