package org.silentsoft.solarguard.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.exception.OrganizationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
public class OrganizationUtilTest {

    @Autowired
    private OrganizationUtil organizationUtil;

    @Test
    public void organizationNotFoundTest() {
        Assertions.assertThrows(OrganizationNotFoundException.class, () -> {
            organizationUtil.checkMemberAuthority(-1);
        });

        Assertions.assertThrows(OrganizationNotFoundException.class, () -> {
            organizationUtil.checkStaffAuthority(-1);
        });
    }

    @Test
    @WithUserDetails
    public void checkMemberAuthorityTest() {
        Assertions.assertDoesNotThrow(() -> {
            organizationUtil.checkMemberAuthority(100);
        });

        Assertions.assertThrows(AccessDeniedException.class, () -> {
            organizationUtil.checkStaffAuthority(100);
        });
    }

    @Test
    @WithUserDetails("admin")
    public void checkStaffAuthorityTest() {
        Assertions.assertDoesNotThrow(() -> {
            organizationUtil.checkMemberAuthority(100);
            organizationUtil.checkStaffAuthority(100);
        });
    }

}
