package org.silentsoft.solarguard.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.silentsoft.solarguard.core.config.oas.expression.Response;
import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.entity.DeviceEntity;
import org.silentsoft.solarguard.entity.LicenseEntity;
import org.silentsoft.solarguard.exception.NotFoundException;
import org.silentsoft.solarguard.exception.PackageNotFoundException;
import org.silentsoft.solarguard.service.LicenseService;
import org.silentsoft.solarguard.vo.DevicePatchVO;
import org.silentsoft.solarguard.vo.DevicePostVO;
import org.silentsoft.solarguard.vo.LicensePatchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Licenses")
@RestController
@RequestMapping("/api/licenses")
public class LicenseController {

    @Autowired
    private LicenseService licenseService;

    @PreAuthorize(Authority.Allow.PRODUCT_API)
    @PostMapping(path = "/{key}/devices", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.CREATED, content = @Content(schema = @Schema(implementation = DeviceEntity.class))),
            @ApiResponse(responseCode = Response.Code.BAD_REQUEST, description = Response.Description.FAILED_TO_CHECK_OR_ACTIVATE_LICENSE_KEY_OR_DEVICE_IS_BANNED),
            @ApiResponse(responseCode = Response.Code.UNAUTHORIZED, description = Response.Description.PRODUCT_CODE_OR_TOKEN_IS_INVALID),
            @ApiResponse(responseCode = Response.Code.PAYMENT_REQUIRED, description = Response.Description.LICENSE_IS_EXPIRED_OR_REVOKED_OR_LIMITED),
            @ApiResponse(responseCode = Response.Code.PRECONDITION_REQUIRED, description = Response.Description.NO_LICENSE_PACKAGE_FOUND_CORRESPONDING_TO_THE_PRODUCT),
    })
    public ResponseEntity<?> addDevice(@PathVariable String key, @RequestBody DevicePostVO devicePostVO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(licenseService.addDevice(key, devicePostVO));
        } catch (PackageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).build();
        } catch (NotFoundException | IllegalArgumentException | AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PreAuthorize(Authority.Allow.PRODUCT_API)
    @PatchMapping(path = "/{key}/devices/{deviceCode}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(schema = @Schema(implementation = DeviceEntity.class))),
            @ApiResponse(responseCode = Response.Code.BAD_REQUEST, description = Response.Description.FAILED_TO_CHECK_OR_ACTIVATE_LICENSE_KEY_OR_DEVICE_IS_BANNED),
            @ApiResponse(responseCode = Response.Code.UNAUTHORIZED, description = Response.Description.PRODUCT_CODE_OR_TOKEN_IS_INVALID),
            @ApiResponse(responseCode = Response.Code.PAYMENT_REQUIRED, description = Response.Description.LICENSE_IS_EXPIRED_OR_REVOKED_OR_LIMITED),
            @ApiResponse(responseCode = Response.Code.PRECONDITION_REQUIRED, description = Response.Description.NO_LICENSE_PACKAGE_FOUND_CORRESPONDING_TO_THE_PRODUCT),
    })
    public ResponseEntity<?> patchDevice(@PathVariable String key, @PathVariable String deviceCode, @RequestBody DevicePatchVO devicePatchVO) {
        try {
            return ResponseEntity.ok(licenseService.patchDevice(key, deviceCode, devicePatchVO));
        } catch (PackageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).build();
        } catch (NotFoundException | IllegalArgumentException | AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PreAuthorize(Authority.Allow.PRODUCT_API)
    @DeleteMapping(path = "/{key}/devices/{deviceCode}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.NO_CONTENT),
            @ApiResponse(responseCode = Response.Code.BAD_REQUEST, description = Response.Description.FAILED_TO_CHECK_OR_ACTIVATE_LICENSE_KEY_OR_DEVICE_IS_BANNED),
            @ApiResponse(responseCode = Response.Code.UNAUTHORIZED, description = Response.Description.PRODUCT_CODE_OR_TOKEN_IS_INVALID),
            @ApiResponse(responseCode = Response.Code.PAYMENT_REQUIRED, description = Response.Description.LICENSE_IS_EXPIRED_OR_REVOKED_OR_LIMITED),
            @ApiResponse(responseCode = Response.Code.PRECONDITION_REQUIRED, description = Response.Description.NO_LICENSE_PACKAGE_FOUND_CORRESPONDING_TO_THE_PRODUCT),
    })
    public ResponseEntity<?> deleteDevice(@PathVariable String key, @PathVariable String deviceCode) {
        try {
            licenseService.deleteDevice(key, deviceCode);
            return ResponseEntity.noContent().build();
        } catch (PackageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).build();
        } catch (NotFoundException | IllegalArgumentException | AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @GetMapping(path = "/{licenseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(schema = @Schema(implementation = LicenseEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.LICENSE_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> getLicense(@PathVariable("licenseId") long licenseId) {
        return ResponseEntity.ok(licenseService.getLicense(licenseId));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @PatchMapping(path = "/{licenseId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(schema = @Schema(implementation = LicenseEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.LICENSE_IS_NOT_EXISTS),
            @ApiResponse(responseCode = Response.Code.UNPROCESSABLE_ENTITY, description = Response.Description.FAILED_TO_UPDATE_LICENSE)
    })
    public ResponseEntity<?> patchLicense(@PathVariable("licenseId") long licenseId, @RequestBody LicensePatchVO licensePatchVO) {
        return ResponseEntity.ok(licenseService.patchLicense(licenseId, licensePatchVO));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @DeleteMapping(path = "/{licenseId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.NO_CONTENT),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.LICENSE_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> deleteLicense(@PathVariable("licenseId") long licenseId) {
        licenseService.deleteLicense(licenseId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @GetMapping(path = "/{licenseId}/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = DeviceEntity.class)))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.LICENSE_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> getDevices(@PathVariable("licenseId") long licenseId) {
        return ResponseEntity.ok(licenseService.getDevices(licenseId));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @GetMapping(path = "/{licenseId}/devices/{deviceCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(schema = @Schema(implementation = DeviceEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.LICENSE_OR_DEVICE_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> getDevice(@PathVariable("licenseId") long licenseId, @PathVariable("deviceCode") String deviceCode) {
        return ResponseEntity.ok(licenseService.getDevice(licenseId, deviceCode));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @DeleteMapping(path = "/{licenseId}/devices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.NO_CONTENT),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.LICENSE_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> deleteDevices(@PathVariable("licenseId") long licenseId) {
        licenseService.deleteDevices(licenseId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @PutMapping(path = "/{licenseId}/devices/{deviceCode}/ban")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.NO_CONTENT),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.LICENSE_OR_DEVICE_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> banDevice(@PathVariable("licenseId") long licenseId, @PathVariable("deviceCode") String deviceCode) {
        licenseService.banDevice(licenseId, deviceCode);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @DeleteMapping(path = "/{licenseId}/devices/{deviceCode}/ban")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.NO_CONTENT),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.LICENSE_OR_DEVICE_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> unbanDevice(@PathVariable("licenseId") long licenseId, @PathVariable("deviceCode") String deviceCode) {
        licenseService.unbanDevice(licenseId, deviceCode);
        return ResponseEntity.noContent().build();
    }

}
