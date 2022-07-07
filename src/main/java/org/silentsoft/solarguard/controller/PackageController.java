package org.silentsoft.solarguard.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.silentsoft.solarguard.core.config.oas.expression.Response;
import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.entity.BundleEntity;
import org.silentsoft.solarguard.entity.PackageEntity;
import org.silentsoft.solarguard.service.PackageService;
import org.silentsoft.solarguard.vo.PackagePatchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Packages")
@RestController
@RequestMapping("/api/packages")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @PreAuthorize(Authority.Has.Admin)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = PackageEntity.class)))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_AN_ADMIN)
    })
    public ResponseEntity<?> getPackages() {
        return ResponseEntity.ok(packageService.getPackages());
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @GetMapping(path = "/{packageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(schema = @Schema(implementation = PackageEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.PACKAGE_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> getPackage(@PathVariable("packageId") long packageId) {
        return ResponseEntity.ok(packageService.getPackage(packageId));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @PatchMapping(path = "/{packageId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(schema = @Schema(implementation = PackageEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.PACKAGE_IS_NOT_EXISTS),
            @ApiResponse(responseCode = Response.Code.UNPROCESSABLE_ENTITY, description = Response.Description.FAILED_TO_UPDATE_PACKAGE)
    })
    public ResponseEntity<?> patchPackage(@PathVariable("packageId") long packageId, @RequestBody PackagePatchVO packagePatchVO) {
        return ResponseEntity.ok(packageService.patchPackage(packageId, packagePatchVO));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @DeleteMapping("/{packageId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.NO_CONTENT),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_HAS_NOT_STAFF_ROLE_IN_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.PRODUCT_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> deletePackage(@PathVariable("packageId") long packageId) {
        packageService.deletePackage(packageId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @GetMapping(path = "/{packageId}/bundles", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = BundleEntity.class)))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.PACKAGE_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> getBundles(@PathVariable("packageId") long packageId) {
        return ResponseEntity.ok(packageService.getBundles(packageId));
    }

}
