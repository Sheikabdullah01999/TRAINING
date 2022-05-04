package com.grootan.assetManagement.Service;

import com.grootan.assetManagement.Exception.FieldEmptyException;
import com.grootan.assetManagement.Model.*;
import com.grootan.assetManagement.Repository.*;
import com.grootan.assetManagement.Exception.ResourceNotFoundException;
import com.grootan.assetManagement.Exception.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

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

    //@Secured({ "ROLE_RUN_AS_REPORTER" })
    public Authentication getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String s=authentication.getName();
        System.out.println(s);
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
        adminUser.setEmpName("admin123");
        adminUser.setEmail("admin123@gmail.com");
        adminUser.setEmpPassword(getEncodedPassword("admin@pass"));
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
    public Employee saveEmployee(Employee employeeDetails) {
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

        String devices= employeeDetails.getEmpDevices();
        Employee employee;
        if(devices==null)
        {
            List<Device> devices1=new ArrayList<>();
            employee = new Employee(employeeDetails.getEmpId(),
                    employeeDetails.getEmpName(), employeeDetails.getEmail(),
                    passwordEncoder.encode(employeeDetails.getEmpPassword()),
                    employeeDetails.getEmpDepartment(),
                    "", employeeDetails.getAssignRole(),
                    Arrays.asList(new Role(employeeDetails.getAssignRole())),devices1);
        }
        else
        {
            List<Integer> list=getDeviceID(devices);
            List<String> deviceList =new ArrayList<>();
            for(int i=0;i<list.size();i++)
            {
                deviceList.add(deviceDao.getDeviceById(list.get(i)));
            }
            String updatedDevice="";
            for (String device:deviceList)
            {
                if(updatedDevice=="")
                {
                    updatedDevice=updatedDevice+device;
                    updatedDevice=device;
                }
                else
                {
                    updatedDevice=updatedDevice+";"+device;
                }

            }
            List<Device> device=new ArrayList<>();
            List<Integer> id=new ArrayList<>();
            id = getDeviceID(devices);
            for(Integer deviceId : id)
            {
                device.add(new Device(deviceId));
            }

                    employee = new Employee(employeeDetails.getEmpId(),
                    employeeDetails.getEmpName(), employeeDetails.getEmail(),
                    passwordEncoder.encode(employeeDetails.getEmpPassword()),
                    employeeDetails.getEmpDepartment(),
                    updatedDevice, employeeDetails.getAssignRole(),
                    Arrays.asList(new Role(employeeDetails.getAssignRole())),device);

            for(int i=0;i<id.size();i++)
            {
                deviceDao.updateAssignStatus(id.get(i));
            }
        }

        String history="New employee with Id:"+employeeDetails.getEmpId()+","
                + "  Name: "+employeeDetails.getEmpName()+","
                + "  employee Device Id:"+employeeDetails.getEmpDevices()+" has been registered ";
        LocalDateTime date=LocalDateTime.now();
        DateTimeFormatter time=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatedDate=date.format(time);
        History newHistory=new History(history);
        historyDao.save(newHistory);

        return employeeDao.save(employee);
    }

    public Role saveRoles(Role role) {
        if(emailExists(role.getRoleName()))
        {
            throw new UserAlreadyExistException("role exists: "+role.getRoleName());
        }
        String roleHistory="New role "+role.getRoleName()+"Added ";
        History newRoleStatus=new History(roleHistory);
        historyDao.save(newRoleStatus);
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
      String deviceHistory="New device with name:  "+device.getDeviceName()+","
              +"  device manufacture id:"+device.getManufacturedId()+" has been  added";
      History newDeviceHistory=new History(deviceHistory);
      historyDao.save(newDeviceHistory);
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
      System.out.println(empDevices);
      String empId = deviceDao.getEmpId(id);
      System.out.println(empId);
      String empDevicesById = employeeDao.getEmpDevices(empId);
      System.out.println(empDevicesById);
      if(empDevicesById!=null)
      {
          String [] empDevicesByIds = empDevicesById.split(";");
          for(int j=0;j<empDevicesByIds.length;j++)
          {
              System.out.println(empDevicesByIds[j]);
          }
          String temp="";
          for(int i=0;i<empDevicesByIds.length;i++)
          {
              if(empDevices.equals(empDevicesByIds[i]))
              {
                  temp=temp+"";
              }
              else
              {
                  temp=temp+empDevicesByIds[i];
              }
          }
          employeeDao.updateEmployeeByEmpDevice(empId,temp);
          String deviceDeleteHistory="device id:"+device.getId()+","
                  +"device name: "+device.getDeviceName()+","
                  +"manufactured id: "+device.getManufacturedId()+"  record deleted ";
          History newDeviceDeleteRecord=new History(deviceDeleteHistory);
          historyDao.save(newDeviceDeleteRecord);
          deviceDao.deleteForiegnKey(id);
          deviceDao.deleteById(id);
      }
      else
      {
          String deviceDeleteHistory="device id:"+device.getId()+","
                  +"device name: "+device.getDeviceName()+","
                  +"manufactured id: "+device.getManufacturedId()+"  record deleted ";
          History newDeviceDeleteRecord=new History(deviceDeleteHistory);
          historyDao.save(newDeviceDeleteRecord);
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


        String updatedEmployeeHistory="";

        if(employeeDetails.getEmpDevices()!=null&&emp.getEmpDevices()!=null)
        {
            if(!emp.getEmpDevices().equals(employeeDetails.getEmpDevices())&&(newDeviceSize!=null))
            {
                int size=newDeviceSize.size();
                updatedEmployeeHistory=updatedEmployeeHistory+size+"new device added "
                        + newDevice+","+"for  "+"employee Id: "
                        +employeeDetails.getEmpId();
            }
        }
        if(!emp.getEmpName().equalsIgnoreCase(employeeDetails.getEmpName()))
        {
            updatedEmployeeHistory=updatedEmployeeHistory+"Employee name changed from "
                    +emp.getEmpName()+" to "
                    +employeeDetails.getEmpName()+",";
        }
        if(!emp.getEmpDepartment().equalsIgnoreCase(employeeDetails.getEmpDepartment()))
        {
            updatedEmployeeHistory=updatedEmployeeHistory+"Employee department changed from "
                    + emp.getEmpDepartment()+"to"+employeeDetails.getEmpDepartment()+"for  "
                    +"employee Id: "+employeeDetails.getEmpId();
        }
        if(!emp.getAssignRole().equalsIgnoreCase(employeeDetails.getAssignRole()))
        {
            updatedEmployeeHistory=updatedEmployeeHistory+"Employee Role changed from "
                    +  emp.getAssignRole()+"to "+employeeDetails.getAssignRole()+"for  "
                    +"employee Id: "+employeeDetails.getEmpId();
        }

        History newUpdatedEmployeeHistory=new History(updatedEmployeeHistory);
        historyDao.save(newUpdatedEmployeeHistory);
        return employeeDao.save(employee);

    }

    public void deleteEmpDetails(String id)
    {

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
        LocalDateTime date=LocalDateTime.now();
        DateTimeFormatter time=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatedDate=date.format(time);
        History device=new History(newDeletedDeviceHistory);
        historyDao.save(device);

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
      if(deviceCategoryExists(category.getCategory()))
      {
          throw new UserAlreadyExistException("This category is already exists: "+category.getCategory());
      }
        String newDeviceHistory="New Device category "+category.getCategory()+" added";
        History device=new History(newDeviceHistory);
        historyDao.save(device);
        deviceCategoryDao.save(category);
    }

    public List<DeviceCategory> getCategory() {

      List<DeviceCategory> list=deviceCategoryDao.findAll();
      return list;
    }

    public void saveDeviceName(DeviceName deviceName)
    {
        if(deviceNameExists(deviceName.getName()))
        {
            throw new UserAlreadyExistException("This Product Name is Already Exists: "+deviceName.getName());
        }
        String newDeviceHistory="New Device with name "+deviceName.getName()+" has been added";
        History device=new History(newDeviceHistory);
        historyDao.save(device);
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


    public List<History> getHistory() {
     List<History> historyList=historyDao.findAll();
     System.out.println(historyList);
     System.out.print("/n");
     getCurrentUser();
        return historyList;
    }
}

