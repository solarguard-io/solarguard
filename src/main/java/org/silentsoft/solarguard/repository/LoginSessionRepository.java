package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.LoginSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginSessionRepository extends JpaRepository<LoginSessionEntity, Long> {

}
