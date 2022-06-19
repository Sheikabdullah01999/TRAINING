package com.grootan.assetManagement.Controller.RestController;

import com.google.gson.Gson;
import com.grootan.assetManagement.Exception.AlreadyExistsException;
import com.grootan.assetManagement.Exception.FieldEmptyException;
import com.grootan.assetManagement.Exception.ResourceNotFoundException;
import com.grootan.assetManagement.Model.Device;
import com.grootan.assetManagement.Repository.DeviceCategoryDao;
import com.grootan.assetManagement.Repository.DeviceDao;
import com.grootan.assetManagement.Repository.HistoryDao;
import com.grootan.assetManagement.Response;
import com.grootan.assetManagement.Service.DeviceService;
import com.grootan.assetManagement.request.DeviceCategoryRequest;
import com.grootan.assetManagement.request.DeviceNameRequest;
import com.grootan.assetManagement.request.DeviceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.websocket.server.PathParam;
import java.util.List;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "201", description = "CREATED",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "404", description = "NO_RECORDS",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "406", description = "NOT_ACCEPTABLE",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "409", description = "CONFLICT",content = @Content(schema = @Schema(implementation = Response.class)))
})
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

    @GetMapping("/v1/history")
    @Operation(summary = "GetHistories", description = "Returns a List Of Histories")
    public ResponseEntity<Object> history() throws ResourceNotFoundException {
        return deviceService.getHistory();
    }

    @GetMapping("/v1/devices")
    @Operation(summary = "GetDevices", description = "Returns a List Of Devices")
    public ResponseEntity<Object> getAllDeviceList() throws ResourceNotFoundException {
        return deviceService.getAllDevices();
    }

    @GetMapping("/v1/devices/categories")
    @Operation(summary = "GetDeviceCategories", description = "Returns a List Of DeviceCategories")
    public ResponseEntity<Object> getDeviceCategories() throws ResourceNotFoundException {
        return deviceService.getCategory();
    }

    @GetMapping("/v1/devices/deviceNames")
    @Operation(summary = "GetDeviceNames", description = "Returns a List Of DeviceNames")
    public ResponseEntity<Object> getDeviceNames() throws ResourceNotFoundException {
        return deviceService.getDeviceNames();
    }

    @GetMapping("/v1/devices/{deviceId}")
    @Operation(summary = "GetDevice", description = "Returns a Device By Id")
    public ResponseEntity<Object> getDeviceById(@PathParam("deviceId") Integer deviceId) {
        return deviceService.findDeviceById(deviceId);
    }

    @PostMapping("/v1/{deviceId}/device")
    @Operation(summary = "CreateDevice", description = "Create a Device")
    public ResponseEntity<Object> addDevice(@PathVariable(name = "deviceId") Integer deviceId, @RequestBody DeviceRequest device) throws FieldEmptyException, ResourceNotFoundException {
        return deviceService.addDeviceDetails(deviceId,device);
    }

    @PostMapping("/v1/devices/{deviceCategoryId}/category")
    @Operation(summary = "CreateDeviceCategory", description = "Create a DeviceCategory")
    public ResponseEntity<Object> addDeviceCategory(@PathVariable(name = "deviceCategoryId") Long deviceCategoryId, @RequestBody DeviceCategoryRequest deviceCategory) throws FieldEmptyException, AlreadyExistsException {

        return deviceService.saveDeviceCategory(deviceCategoryId,deviceCategory);
    }

    @PostMapping("/v1/devices/{deviceNameId}/deviceName")
    @Operation(summary = "CreateDeviceName", description = "Create a DeviceName")
    public ResponseEntity<Object> addDeviceName(@PathVariable(name = "deviceNameId") Long deviceNameId, @RequestBody DeviceNameRequest deviceName) throws FieldEmptyException, AlreadyExistsException {
        return deviceService.saveDeviceName(deviceNameId,deviceName);

    }

    @PutMapping("/v1/device/{deviceId}")
    @Operation(summary = "EditDevice", description = "update the device")
    public ResponseEntity<Object> updateDevice(@PathVariable(name = "deviceId") Integer deviceId, @RequestBody DeviceRequest device) throws FieldEmptyException,ResourceNotFoundException {
        return deviceService.updateDeviceDetails(deviceId,device);
    }

    @DeleteMapping("/v1/devices/{deviceId}")
    @Operation(summary = "DeleteDevice", description = "delete the device")
    public ResponseEntity<Object> deleteDevice(@PathVariable(name = "deviceId") Integer deviceId) throws ResourceNotFoundException {
        return deviceService.deleteDevice(deviceId);
    }

    @DeleteMapping("/v1/devices/deviceCategories/{deviceCategoryId}")
    @Operation(summary = "DeleteDeviceCategory", description = "delete the deviceCategory")
    public ResponseEntity<Object> deleteDeviceCategory(@PathVariable(name = "deviceCategoryId") Long deviceCategoryId) throws ResourceNotFoundException {
        return deviceService.deleteDeviceCategory(deviceCategoryId);
    }

    @DeleteMapping("/v1/devices/deviceNames/{deviceNameId}")
    @Operation(summary = "DeleteDeviceName", description = "delete the deviceName")
    public ResponseEntity<Object> deleteDeviceName(@PathVariable(name = "deviceNameId") Long deviceNameId) throws ResourceNotFoundException {
        return deviceService.deleteDeviceName(deviceNameId);
    }

    @GetMapping("/v1/devices/search")
    @Operation(summary = "DeviceSearch", description = "search device by keyword")
    public ResponseEntity<Object> search(@RequestParam(name = "keyword") String keyword){
        List<Device> devices = deviceDao.findByKeyword(keyword);
        if (devices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(devices);
    }

    @ResponseBody
    @GetMapping(value = "/v1/devices/deviceCategory/{deviceName}")
    @Operation(summary = "GetDeviceCategory", description = "get deviceCategory by deviceName")
    public String loadNamesByCategory(@PathVariable("deviceName") String name) {
        Gson gson = new Gson();
        long id = deviceService.findByCategoryId(name);
        return gson.toJson(deviceService.findByCategory(id));
    }

    @ResponseBody
    @GetMapping(value="/v1/devices/deviceCategoryIds/{deviceName}")
    @Operation(summary = "GetDeviceCategoryId", description = "get deviceCategory by deviceName")
    public long loadIdsByCategory(@PathVariable("deviceName") String name)
    {
        Gson gson = new Gson();
        return Long.parseLong(gson.toJson(deviceCategoryDao.findByDeviceCategoryId(name)));
    }
}