package org.silentsoft.solarguard.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.silentsoft.solarguard.core.config.oas.expression.Response;
import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.entity.ProductEntity;
import org.silentsoft.solarguard.entity.ProductTokenEntity;
import org.silentsoft.solarguard.service.ProductService;
import org.silentsoft.solarguard.vo.ProductPatchVO;
import org.silentsoft.solarguard.vo.ProductTokenPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Products")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PreAuthorize(Authority.Has.Admin)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductEntity.class)))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_AN_ADMIN)
    })
    public ResponseEntity<?> getProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @GetMapping(path = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(schema = @Schema(implementation = ProductEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.PRODUCT_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> getProduct(@PathVariable("productId") long productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @PatchMapping(path = "/{productId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(schema = @Schema(implementation = ProductEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_HAS_NOT_STAFF_ROLE_IN_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.PRODUCT_IS_NOT_EXISTS),
            @ApiResponse(responseCode = Response.Code.UNPROCESSABLE_ENTITY, description = Response.Description.FAILED_TO_UPDATE_PRODUCT)
    })
    public ResponseEntity<?> patchProduct(@PathVariable("productId") long productId, @RequestBody ProductPatchVO product) {
        return ResponseEntity.ok(productService.patchProduct(productId, product));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @DeleteMapping("/{productId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.NO_CONTENT),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_HAS_NOT_STAFF_ROLE_IN_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.PRODUCT_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> deleteProduct(@PathVariable("productId") long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @Hidden
    @PreAuthorize(Authority.Allow.BROWSER_API)
    @GetMapping(path = "/{productId}/tokens", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductTokenEntity.class)))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_A_MEMBER_OF_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.PRODUCT_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> getTokens(@PathVariable("productId") long productId) {
        return ResponseEntity.ok(productService.getTokens(productId));
    }

    @Hidden
    @PreAuthorize(Authority.Allow.BROWSER_API)
    @PostMapping(path = "/{productId}/tokens", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.CREATED, content = @Content(schema = @Schema(implementation = ProductTokenEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_HAS_NOT_STAFF_ROLE_IN_ORGANIZATION),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.PRODUCT_IS_NOT_EXISTS),
            @ApiResponse(responseCode = Response.Code.UNPROCESSABLE_ENTITY, description = Response.Description.FAILED_TO_CREATE_PRODUCT_TOKEN)
    })
    public ResponseEntity<?> createToken(@PathVariable("productId") long productId, @RequestBody ProductTokenPostVO productToken) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createToken(productId, productToken));
    }

}
