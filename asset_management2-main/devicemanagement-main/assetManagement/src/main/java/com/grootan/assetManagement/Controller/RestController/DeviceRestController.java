package com.grootan.assetManagement.Controller.RestController;

import com.grootan.assetManagement.Exception.AlreadyExistsException;
import com.grootan.assetManagement.Exception.FieldEmptyException;
import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Exception.ResourceNotFoundException;
import com.grootan.assetManagement.Model.Device;
import com.grootan.assetManagement.Model.DeviceCategory;
import com.grootan.assetManagement.Model.DeviceName;
import com.grootan.assetManagement.Model.History;
import com.grootan.assetManagement.Repository.DeviceCategoryDao;
import com.grootan.assetManagement.Repository.DeviceDao;
import com.grootan.assetManagement.Repository.HistoryDao;
import com.grootan.assetManagement.Service.DeviceService;
import com.grootan.assetManagement.request.DeviceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

import static com.grootan.assetManagement.Model.Constants.NO_RECORDS;


@RestController
public class DeviceRestController {
    @Autowired
    DeviceService deviceService;

    @Autowired
    HistoryDao historyDao;

    @Autowired
    DeviceCategoryDao deviceCategoryDao;

    @Autowired
    DeviceDao deviceDao;

    @PostMapping("/device/add/add")
    public ResponseEntity addDeviceDetails(@RequestBody Device device) throws FieldEmptyException {
        return  deviceService.addDeviceDetails(device);
    }

    @GetMapping("/device/list/device")
    public ResponseEntity<List<Device>> getAllDeviceList()
    {
        List<Device> devices = deviceDao.findAll();
        if(devices.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(devices);
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity getDeviceById(@PathParam("deviceId") Integer deviceId)
    {
        return deviceService.findDeviceById(deviceId);
    }

    @PutMapping("/device/update/device")
    public ResponseEntity updateDevice(@RequestBody Device device) throws FieldEmptyException {
        return deviceService.updateDeviceDetails(device);
    }

    @DeleteMapping("/device/delete/{id}")
    public ResponseEntity deleteDevice(@PathVariable(name="id") Integer id) throws ResourceNotFoundException {
        return deviceService.deleteDeviceDetails(id);
    }

    @PostMapping("/device/add/category/device")
    public ResponseEntity addDeviceCategory(@RequestBody DeviceCategory deviceCategory) throws FieldEmptyException, AlreadyExistsException {

        return deviceService.saveDeviceCategory(deviceCategory);

    }

    @PostMapping("/device/name/add")
    public ResponseEntity addDeviceName(@RequestBody DeviceName deviceName) throws FieldEmptyException, AlreadyExistsException {
        return  deviceService.saveDeviceName(deviceName);

    }

    @GetMapping("/get/history")
    public ResponseEntity history() throws ResourceNotFoundException {
        return deviceService.getHistory();
    }

    @GetMapping("/device/get/category/all")
    public ResponseEntity getDeviceCategory() throws ResourceNotFoundException {
        return deviceService.getCategory();
    }

    @DeleteMapping("/device/category/delete/{id}")
    public ResponseEntity deleteDeviceCategory(@PathVariable(name="id") String  category) throws ResourceNotFoundException {

        return deviceService.deleteDeviceCategory(category);
    }



}