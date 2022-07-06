package org.silentsoft.solarguard.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.silentsoft.solarguard.core.config.oas.expression.Response;
import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.entity.ProductTokenEntity;
import org.silentsoft.solarguard.service.ProductTokenService;
import org.silentsoft.solarguard.vo.ProductTokenPatchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Hidden
@Tag(name = "Product Tokens")
@RestController
@RequestMapping("/api/product-tokens")
public class ProductTokenController {

    @Autowired
    private ProductTokenService productTokenService;

    @PreAuthorize(Authority.Allow.BROWSER_API)
    @GetMapping(path = "/{productTokenId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(schema = @Schema(implementation = ProductTokenEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.PRODUCT_TOKEN_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> getProductToken(@PathVariable("productTokenId") long productTokenId) {
        return ResponseEntity.ok(productTokenService.getProductToken(productTokenId));
    }

    @PreAuthorize(Authority.Allow.BROWSER_API)
    @PatchMapping(path = "/{productTokenId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(schema = @Schema(implementation = ProductTokenEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_HAS_NOT_STAFF_ROLE_IN_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.PRODUCT_TOKEN_IS_NOT_EXISTS),
            @ApiResponse(responseCode = Response.Code.UNPROCESSABLE_ENTITY, description = Response.Description.FAILED_TO_UPDATE_PRODUCT_TOKEN)
    })
    public ResponseEntity<?> patchProductToken(@PathVariable("productTokenId") long productTokenId, @RequestBody ProductTokenPatchVO productToken) {
        return ResponseEntity.ok(productTokenService.patchProductToken(productTokenId, productToken));
    }

    @PreAuthorize(Authority.Allow.BROWSER_API)
    @DeleteMapping("/{productTokenId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.NO_CONTENT),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_HAS_NOT_STAFF_ROLE_IN_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.PRODUCT_TOKEN_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> deleteProductToken(@PathVariable("productTokenId") long productTokenId) {
        productTokenService.deleteProductToken(productTokenId);
        return ResponseEntity.noContent().build();
    }

}
