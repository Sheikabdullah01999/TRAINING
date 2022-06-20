package com.grootan.assetManagement.controller.restController;

import com.grootan.assetManagement.exception.GeneralException;
import com.grootan.assetManagement.model.Role;
import com.grootan.assetManagement.response.Response;
import com.grootan.assetManagement.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "201", description = "CREATED",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "404", description = "NO_RECORDS",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "406", description = "NOT_ACCEPTABLE",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "409", description = "CONFLICT",content = @Content(schema = @Schema(implementation = Response.class)))
})
@RestController
public class RoleRestController {
    @Autowired
    RoleService roleService;

    @GetMapping("/v1/roles")
    @Operation(summary = "GetRoles", description = "Returns a List Of Roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.getAllRoles());
    }

    @PostMapping("/v1/role")
    @Operation(summary = "CreateRole", description = "Create a Role")
    public ResponseEntity<Object> saveRole(@RequestBody Role role) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(roleService.saveRoles(role));
        } catch (GeneralException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}