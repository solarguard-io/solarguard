package org.silentsoft.solarguard.service;

import org.silentsoft.solarguard.core.config.security.expression.Authority;
import org.silentsoft.solarguard.entity.PersonalTokenEntity;
import org.silentsoft.solarguard.exception.PersonalTokenNotFoundException;
import org.silentsoft.solarguard.repository.PersonalTokenRepository;
import org.silentsoft.solarguard.repository.PersonalTokenStatisticsRepository;
import org.silentsoft.solarguard.util.UserUtil;
import org.silentsoft.solarguard.vo.PersonalTokenPatchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;

@Service
public class PersonalTokenService {

    @Autowired
    private PersonalTokenRepository personalTokenRepository;

    @Autowired
    private PersonalTokenStatisticsRepository personalTokenStatisticsRepository;

    @PreAuthorize(Authority.Allow.BROWSER_API)
    public PersonalTokenEntity getPersonalToken(long personalTokenId) {
        PersonalTokenEntity personalToken = findPersonalToken(personalTokenId);

        UserUtil.checkIdentity(personalToken.getUser().getId());

        return hideAccessToken(personalToken);
    }

    private PersonalTokenEntity findPersonalToken(long personalTokenId) {
        return personalTokenRepository.findById(personalTokenId).orElseThrow(() -> new PersonalTokenNotFoundException(String.format("The personal token '%d' does not exist.", personalTokenId)));
    }

    @PreAuthorize(Authority.Allow.BROWSER_API)
    public PersonalTokenEntity patchPersonalToken(long personalTokenId, PersonalTokenPatchVO personalTokenPatchVO) {
        PersonalTokenEntity personalToken = findPersonalToken(personalTokenId);

        UserUtil.checkIdentity(personalToken.getUser().getId());

        if (StringUtils.hasLength(personalTokenPatchVO.getNote())) {
            personalToken.setNote(personalTokenPatchVO.getNote());
        }
        personalToken.setUpdatedBy(UserUtil.getId());
        personalToken.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        personalToken = personalTokenRepository.save(personalToken);

        return hideAccessToken(personalToken);
    }

    @PreAuthorize(Authority.Allow.BROWSER_API)
    @Transactional
    public void deletePersonalToken(long personalTokenId) {
        PersonalTokenEntity personalToken = findPersonalToken(personalTokenId);

        UserUtil.checkIdentity(personalToken.getUser().getId());

        personalTokenStatisticsRepository.deleteAllByPersonalTokenId(personalTokenId);
        personalTokenRepository.deleteById(personalTokenId);
    }

    private PersonalTokenEntity hideAccessToken(PersonalTokenEntity personalTokenEntity) {
        if (personalTokenEntity != null) {
            personalTokenEntity.setAccessToken(null);
        }
        return personalTokenEntity;
    }

}
