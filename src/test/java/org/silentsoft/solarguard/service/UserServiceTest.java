package org.silentsoft.solarguard.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithBrowser;
import org.silentsoft.solarguard.context.support.WithProduct;
import org.silentsoft.solarguard.entity.PersonalTokenEntity;
import org.silentsoft.solarguard.entity.UserEntity;
import org.silentsoft.solarguard.entity.UserRole;
import org.silentsoft.solarguard.exception.UserNotFoundException;
import org.silentsoft.solarguard.repository.UserRepository;
import org.silentsoft.solarguard.util.JwtTokenUtil;
import org.silentsoft.solarguard.vo.PersonalTokenPostVO;
import org.silentsoft.solarguard.vo.UserPatchVO;
import org.silentsoft.solarguard.vo.UserPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("dev")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    @WithUserDetails
    public void createUserWithUserAuthorityTest() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            userService.createUser(new UserPostVO());
        });
    }

    @Test
    @WithUserDetails
    public void patchUserWithUserAuthorityTest() {
        // not me
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            userService.patchUser(1, new UserPatchVO());
        });

        // for me
        Assertions.assertDoesNotThrow(() -> {
            userService.patchUser(2, new UserPatchVO());
        });
    }

    @Test
    @WithUserDetails
    public void deleteUserWithUserAuthorityTest() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            userService.deleteUser(1);
        });
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            userService.deleteUser(2);
        });
    }

    @Test
    @WithBrowser
    public void createPersonalTokenWithUserAuthorityTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // empty note
            userService.createPersonalToken(2, PersonalTokenPostVO.builder().build());
        });

        Assertions.assertDoesNotThrow(() -> {
            PersonalTokenEntity personalToken = userService.createPersonalToken(2, PersonalTokenPostVO.builder().note("TestToken1").build());
            Assertions.assertEquals(2, personalToken.getUser().getId());
            Assertions.assertTrue(jwtTokenUtil.notExpiredToken(personalToken.getAccessToken()));
            Assertions.assertEquals("TestToken1", personalToken.getNote());
            Assertions.assertNull(personalToken.getExpiredAt());
        });

        Assertions.assertDoesNotThrow(() -> {
            PersonalTokenEntity personalToken = userService.createPersonalToken(2, PersonalTokenPostVO.builder().note("TestToken2").expiredAt(LocalDate.now().plusDays(1)).build());
            Assertions.assertEquals(LocalDate.now().plusDays(1), LocalDate.ofEpochDay(personalToken.getExpiredAt().toLocalDateTime().toLocalDate().toEpochDay()));
            Assertions.assertTrue(jwtTokenUtil.notExpiredToken(personalToken.getAccessToken()));
        });

        Assertions.assertDoesNotThrow(() -> {
            PersonalTokenEntity personalToken = userService.createPersonalToken(2, PersonalTokenPostVO.builder().note("TestToken3").expiredAt(LocalDate.now().minusDays(1)).build());
            Assertions.assertEquals(LocalDate.now().minusDays(1), LocalDate.ofEpochDay(personalToken.getExpiredAt().toLocalDateTime().toLocalDate().toEpochDay()));
            Assertions.assertTrue(jwtTokenUtil.expiredToken(personalToken.getAccessToken()));
        });

        Assertions.assertThrows(AccessDeniedException.class, () -> {
            // not mine
            userService.createPersonalToken(1, PersonalTokenPostVO.builder().note("TestToken4").build());
        });
    }

    @Test
    @WithUserDetails("admin")
    public void dataIntegrityTest() {
        UserEntity user = userService.createUser(UserPostVO.builder().username("dataIntegrityTest").password("HelloWorld").build());
        final long userId = user.getId();
        Assertions.assertEquals("dataIntegrityTest", user.getUsername());
        Assertions.assertNull(user.getPassword());
        Assertions.assertNull(user.getEmail());
        Assertions.assertEquals(UserRole.USER, user.getRole());
        Assertions.assertEquals(1, user.getCreatedBy());
        Assertions.assertEquals(1, user.getUpdatedBy());

        user = userService.patchUser(user.getId(), UserPatchVO.builder()
                .username("dataIntegrityTest2")
                .password("HelloWorld2")
                .email("dataIntegrityTest2@solarguard.io")
                .role(UserRole.ADMIN)
                .build());
        Assertions.assertEquals("dataIntegrityTest2", user.getUsername());
        Assertions.assertNull(user.getPassword());
        Assertions.assertEquals("dataIntegrityTest2@solarguard.io", user.getEmail());
        Assertions.assertEquals(UserRole.ADMIN, user.getRole());
        Assertions.assertEquals(1, user.getUpdatedBy());

        userService.deleteUser(user.getId());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(userId);
        });

        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        Assertions.assertTrue(optionalUser.isPresent());
        Assertions.assertTrue(optionalUser.get().getIsDeleted());
    }

    @Test
    @WithProduct(200)
    public void createUserWithProductAuthorityTest() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            userService.createUser(new UserPostVO());
        });
    }

    @Test
    @WithProduct(200)
    public void patchUserWithProductAuthorityTest() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            userService.patchUser(2, new UserPatchVO());
        });
    }

    @Test
    @WithProduct(200)
    public void deleteUserWithProductAuthorityTest() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            userService.deleteUser(2);
        });
    }

}
