package org.silentsoft.solarguard.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.silentsoft.solarguard.core.config.oas.expression.Response;
import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.entity.OrganizationEntity;
import org.silentsoft.solarguard.entity.OrganizationMemberEntity;
import org.silentsoft.solarguard.entity.PackageEntity;
import org.silentsoft.solarguard.entity.ProductEntity;
import org.silentsoft.solarguard.service.OrganizationService;
import org.silentsoft.solarguard.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Organizations")
@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @PreAuthorize(Authority.Has.Admin)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrganizationEntity.class)))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_AN_ADMIN)
    })
    public ResponseEntity<?> getOrganizations() {
        return ResponseEntity.ok(organizationService.getOrganizations());
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @GetMapping(path = "/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(schema = @Schema(implementation = OrganizationEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.ORGANIZATION_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> getOrganization(@PathVariable(value = "organizationId") long organizationId) {
        return ResponseEntity.ok(organizationService.getOrganization(organizationId));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.CREATED, content = @Content(schema = @Schema(implementation = OrganizationEntity.class))),
            @ApiResponse(responseCode = Response.Code.UNPROCESSABLE_ENTITY, description = Response.Description.FAILED_TO_CREATE_ORGANIZATION)
    })
    public ResponseEntity<?> createOrganization(@RequestBody OrganizationPostVO organization) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationService.createOrganization(organization));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @PatchMapping(path = "/{organizationId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(schema = @Schema(implementation = OrganizationEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_HAS_NOT_STAFF_ROLE_IN_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.ORGANIZATION_IS_NOT_EXISTS),
            @ApiResponse(responseCode = Response.Code.UNPROCESSABLE_ENTITY, description = Response.Description.FAILED_TO_UPDATE_ORGANIZATION)
    })
    public ResponseEntity<?> patchOrganization(@PathVariable(value = "organizationId") long organizationId, @RequestBody OrganizationPatchVO organization) {
        return ResponseEntity.ok(organizationService.patchOrganization(organizationId, organization));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @DeleteMapping("/{organizationId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.NO_CONTENT),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_HAS_NOT_STAFF_ROLE_IN_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.ORGANIZATION_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> deleteOrganization(@PathVariable(value = "organizationId") long organizationId) {
        organizationService.deleteOrganization(organizationId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @GetMapping(path = "/{organizationId}/members", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrganizationMemberEntity.class)))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.ORGANIZATION_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> getMembers(@PathVariable(value = "organizationId") long organizationId) {
        return ResponseEntity.ok(organizationService.getMembers(organizationId));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @PostMapping(path = "/{organizationId}/members", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.CREATED, content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrganizationMemberPostVO.class)))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_HAS_NOT_STAFF_ROLE_IN_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.ORGANIZATION_IS_NOT_EXISTS),
            @ApiResponse(responseCode = Response.Code.UNPROCESSABLE_ENTITY, description = Response.Description.FAILED_TO_ADD_MEMBER_TO_ORGANIZATION)
    })
    public ResponseEntity<?> addMembers(@PathVariable long organizationId, @RequestBody OrganizationMemberPostVO organizationMemberPostVO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationService.addMembers(organizationId, organizationMemberPostVO));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @DeleteMapping(path = "/{organizationId}/members", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.NO_CONTENT),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_HAS_NOT_STAFF_ROLE_IN_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.ORGANIZATION_IS_NOT_EXISTS),
            @ApiResponse(responseCode = Response.Code.UNPROCESSABLE_ENTITY, description = Response.Description.FAILED_TO_REMOVE_MEMBER_FROM_ORGANIZATION)
    })
    public ResponseEntity<?> removeMembers(@PathVariable(value = "organizationId") long organizationId, @RequestBody OrganizationMemberDeleteVO organizationMemberDeleteVO) {
        organizationService.removeMembers(organizationId, organizationMemberDeleteVO);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @GetMapping(path = "/{organizationId}/products", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductEntity.class)))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.ORGANIZATION_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> getProducts(@PathVariable(value = "organizationId") long organizationId) {
        return ResponseEntity.ok(organizationService.getProducts(organizationId));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @PostMapping(path = "/{organizationId}/products", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.CREATED, content = @Content(schema = @Schema(implementation = ProductEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_HAS_NOT_STAFF_ROLE_IN_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.ORGANIZATION_IS_NOT_EXISTS),
            @ApiResponse(responseCode = Response.Code.UNPROCESSABLE_ENTITY, description = Response.Description.FAILED_TO_ADD_PRODUCT_TO_ORGANIZATION)
    })
    public ResponseEntity<?> addProduct(@PathVariable long organizationId, @RequestBody ProductPostVO productPostVO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationService.addProduct(organizationId, productPostVO));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @GetMapping(path = "/{organizationId}/packages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = PackageEntity.class)))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.ORGANIZATION_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> getPackages(@PathVariable(value = "organizationId") long organizationId) {
        return ResponseEntity.ok(organizationService.getPackages(organizationId));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @PostMapping(path = "/{organizationId}/packages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.CREATED, content = @Content(schema = @Schema(implementation = PackageEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_HAS_NOT_STAFF_ROLE_IN_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.ORGANIZATION_IS_NOT_EXISTS),
            @ApiResponse(responseCode = Response.Code.UNPROCESSABLE_ENTITY, description = Response.Description.FAILED_TO_ADD_PACKAGE_TO_ORGANIZATION)
    })
    public ResponseEntity<?> addPackage(@PathVariable long organizationId, @RequestBody PackagePostVO packagePostVO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationService.addPackage(organizationId, packagePostVO));
    }

}
