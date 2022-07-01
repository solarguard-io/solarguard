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
import org.silentsoft.solarguard.entity.UserEntity;
import org.silentsoft.solarguard.service.UserService;
import org.silentsoft.solarguard.vo.PersonalTokenPostVO;
import org.silentsoft.solarguard.vo.UserPatchVO;
import org.silentsoft.solarguard.vo.UserPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserEntity.class)))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.PRODUCT_API_IS_NOT_ALLOWED)
    })
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(schema = @Schema(implementation = UserEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.PRODUCT_API_IS_NOT_ALLOWED),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.USER_IS_NOT_EXISTS)
    })
    public ResponseEntity<?> getUser(@PathVariable("userId") long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PreAuthorize(Authority.Has.Admin)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.CREATED, content = @Content(schema = @Schema(implementation = UserEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_AN_ADMIN),
            @ApiResponse(responseCode = Response.Code.UNPROCESSABLE_ENTITY, description = Response.Description.FAILED_TO_CREATE_USER)
    })
    public ResponseEntity<?> createUser(@RequestBody UserPostVO user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
    }

    @PreAuthorize(Authority.Deny.PRODUCT_API)
    @PatchMapping(path = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.OK, content = @Content(schema = @Schema(implementation = UserEntity.class))),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.PRODUCT_API_IS_NOT_ALLOWED),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.USER_IS_NOT_EXISTS),
            @ApiResponse(responseCode = Response.Code.UNPROCESSABLE_ENTITY, description = Response.Description.FAILED_TO_EDIT_USER)
    })
    public ResponseEntity<?> patchUser(@PathVariable("userId") long userId, @RequestBody UserPatchVO user) {
        return ResponseEntity.ok(userService.patchUser(userId, user));
    }

    @PreAuthorize(Authority.Has.Admin)
    @DeleteMapping("/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Response.Code.NO_CONTENT),
            @ApiResponse(responseCode = Response.Code.FORBIDDEN, description = Response.Description.USER_IS_NOT_AN_ADMIN),
            @ApiResponse(responseCode = Response.Code.NOT_FOUND, description = Response.Description.USER_IS_NOT_EXISTS),
            @ApiResponse(responseCode = Response.Code.UNPROCESSABLE_ENTITY, description = Response.Description.FAILED_TO_DELETE_USER)
    })
    public ResponseEntity<?> deleteUser(@PathVariable("userId") long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Hidden
    @PreAuthorize(Authority.Allow.BROWSER_API)
    @GetMapping(path = "/{userId}/tokens", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPersonalTokens(@PathVariable("userId") long userId) {
        return ResponseEntity.ok(userService.getPersonalTokens(userId));
    }

    @Hidden
    @PreAuthorize(Authority.Allow.BROWSER_API)
    @PostMapping(path = "/{userId}/tokens", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPersonalToken(@PathVariable("userId") long userId, @RequestBody PersonalTokenPostVO personalToken) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createPersonalToken(userId, personalToken));
    }

}
