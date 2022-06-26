package org.silentsoft.solarguard.service;

import org.silentsoft.solarguard.entity.PersonalTokenStatisticsEntity;
import org.silentsoft.solarguard.entity.PersonalTokenStatisticsId;
import org.silentsoft.solarguard.repository.PersonalTokenStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;

@Service
public class PersonalTokenStatisticsService {

    @Autowired
    private PersonalTokenStatisticsRepository personalTokenStatisticsRepository;

    public void updatePersonalTokenStatistics(long personalTokenId, int statusCode) {
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTimeMillis);

        PersonalTokenStatisticsId personalTokenStatisticsId = new PersonalTokenStatisticsId(personalTokenId, new Date(currentTimeMillis));
        PersonalTokenStatisticsEntity personalTokenStatisticsEntity = personalTokenStatisticsRepository.findById(personalTokenStatisticsId).orElseGet(() -> {
            PersonalTokenStatisticsEntity newEntity = new PersonalTokenStatisticsEntity();
            newEntity.setId(personalTokenStatisticsId);
            newEntity.setSuccessCount(0L);
            newEntity.setFailureCount(0L);
            newEntity.setCreatedAt(timestamp);
            return newEntity;
        });

        int status = statusCode / 100;
        if (status == 2) {
            personalTokenStatisticsEntity.setSuccessCount(personalTokenStatisticsEntity.getSuccessCount() + 1);
        } else if (status == 4 || status == 5) {
            personalTokenStatisticsEntity.setFailureCount(personalTokenStatisticsEntity.getFailureCount() + 1);
        }
        personalTokenStatisticsEntity.setUpdatedAt(timestamp);

        personalTokenStatisticsRepository.save(personalTokenStatisticsEntity);
    }

}
