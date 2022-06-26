package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.LoginTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginTokenRepository extends JpaRepository<LoginTokenEntity, Long> {

    Optional<LoginTokenEntity> findByUser_IdAndAccessToken(Long userId, String accessToken);

    Optional<LoginTokenEntity> findByUser_IdAndRefreshToken(Long userId, String refreshToken);

}
