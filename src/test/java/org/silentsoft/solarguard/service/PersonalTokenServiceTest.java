package org.silentsoft.solarguard.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.silentsoft.solarguard.context.support.WithBrowser;
import org.silentsoft.solarguard.entity.PersonalTokenEntity;
import org.silentsoft.solarguard.exception.PersonalTokenNotFoundException;
import org.silentsoft.solarguard.util.UserUtil;
import org.silentsoft.solarguard.vo.PersonalTokenPatchVO;
import org.silentsoft.solarguard.vo.PersonalTokenPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
public class PersonalTokenServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PersonalTokenService personalTokenService;

    @Test
    @WithBrowser
    public void dataIntegrityTest() {
        long personalTokenId = userService.createPersonalToken(UserUtil.getId(), PersonalTokenPostVO.builder().note("dataIntegrityTest1").build()).getId();

        PersonalTokenEntity personalToken = personalTokenService.getPersonalToken(personalTokenId);
        Assertions.assertEquals(UserUtil.getId(), personalToken.getUser().getId());
        Assertions.assertEquals("dataIntegrityTest1", personalToken.getNote());

        personalToken = personalTokenService.patchPersonalToken(personalTokenId, PersonalTokenPatchVO.builder().note("dataIntegrityTest2").build());
        Assertions.assertEquals("dataIntegrityTest2", personalToken.getNote());

        List<PersonalTokenEntity> personalTokens = userService.getPersonalTokens(UserUtil.getId());
        Assertions.assertFalse(personalTokens.isEmpty());

        personalTokenService.deletePersonalToken(personalTokenId);

        Assertions.assertThrows(PersonalTokenNotFoundException.class, () -> {
            personalTokenService.getPersonalToken(personalTokenId);
        });
    }

}
