package org.silentsoft.solarguard.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
public class UserUtilTest {

    @Test
    public void nullPointerExceptionTest() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            UserUtil.getId();
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            UserUtil.getEntity();
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            UserUtil.isAdmin();
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            UserUtil.isNotAdmin();
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            UserUtil.checkAdminAuthority();
        });
    }

    @Test
    @WithUserDetails
    public void withUserTest() {
        Assertions.assertEquals(2, UserUtil.getId());
        Assertions.assertEquals(2, UserUtil.getEntity().getId());
        Assertions.assertNull(UserUtil.getEntity().getPassword());
        Assertions.assertTrue(UserUtil.isNotAdmin());
        Assertions.assertFalse(UserUtil.isAdmin());
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            UserUtil.checkAdminAuthority();
        });
    }

    @Test
    @WithUserDetails("admin")
    public void withAdminTest() {
        Assertions.assertEquals(1, UserUtil.getId());
        Assertions.assertEquals(1, UserUtil.getEntity().getId());
        Assertions.assertNull(UserUtil.getEntity().getPassword());
        Assertions.assertFalse(UserUtil.isNotAdmin());
        Assertions.assertTrue(UserUtil.isAdmin());
        Assertions.assertDoesNotThrow(() -> {
            UserUtil.checkAdminAuthority();
        });
    }

}
