package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.PersonalTokenStatisticsEntity;
import org.silentsoft.solarguard.entity.PersonalTokenStatisticsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalTokenStatisticsRepository extends JpaRepository<PersonalTokenStatisticsEntity, PersonalTokenStatisticsId> {

    @Modifying
    @Query("delete from personal_token_statistics where id.personalTokenId = :personalTokenId")
    void deleteAllByPersonalTokenId(long personalTokenId);

}
