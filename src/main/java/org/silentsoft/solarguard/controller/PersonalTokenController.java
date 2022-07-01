package org.silentsoft.solarguard.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.service.PersonalTokenService;
import org.silentsoft.solarguard.vo.PersonalTokenPatchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequestMapping("/api/personal-tokens")
public class PersonalTokenController {

    @Autowired
    private PersonalTokenService personalTokenService;

    @PreAuthorize(Authority.Allow.BROWSER_API)
    @GetMapping(path = "/{personalTokenId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPersonalToken(@PathVariable("personalTokenId") long personalTokenId) {
        return ResponseEntity.ok(personalTokenService.getPersonalToken(personalTokenId));
    }

    @PreAuthorize(Authority.Allow.BROWSER_API)
    @PatchMapping(path = "/{personalTokenId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> patchPersonalToken(@PathVariable("personalTokenId") long personalTokenId, @RequestBody PersonalTokenPatchVO personalToken) {
        return ResponseEntity.ok(personalTokenService.patchPersonalToken(personalTokenId, personalToken));
    }

    @PreAuthorize(Authority.Allow.BROWSER_API)
    @DeleteMapping("/{personalTokenId}")
    public ResponseEntity<?> deletePersonalToken(@PathVariable("personalTokenId") long personalTokenId) {
        personalTokenService.deletePersonalToken(personalTokenId);
        return ResponseEntity.noContent().build();
    }

}
