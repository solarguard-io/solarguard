package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.PersonalTokenStatisticsEntity;
import org.silentsoft.solarguard.entity.PersonalTokenStatisticsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalTokenStatisticsRepository extends JpaRepository<PersonalTokenStatisticsEntity, PersonalTokenStatisticsId> {

    void deleteAllById_PersonalTokenId(long personalTokenId);

}
