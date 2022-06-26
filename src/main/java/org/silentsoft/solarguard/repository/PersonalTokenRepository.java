package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.PersonalTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalTokenRepository extends JpaRepository<PersonalTokenEntity, Long> {

    List<PersonalTokenEntity> findAllByUserId(long userId);

    Optional<PersonalTokenEntity> findByUserIdAndAccessToken(long userId, String accessToken);

}
