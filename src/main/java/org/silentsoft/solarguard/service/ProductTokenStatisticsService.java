package org.silentsoft.solarguard.service;

import org.silentsoft.solarguard.entity.ProductTokenStatisticsEntity;
import org.silentsoft.solarguard.entity.ProductTokenStatisticsId;
import org.silentsoft.solarguard.repository.ProductTokenStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;

@Service
public class ProductTokenStatisticsService {

    @Autowired
    private ProductTokenStatisticsRepository productTokenStatisticsRepository;

    public void updateProductTokenStatistics(long productTokenId, int statusCode) {
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTimeMillis);

        ProductTokenStatisticsId productTokenStatisticsId = new ProductTokenStatisticsId(productTokenId, new Date(currentTimeMillis));
        ProductTokenStatisticsEntity productTokenStatisticsEntity = productTokenStatisticsRepository.findById(productTokenStatisticsId).orElseGet(() -> {
            ProductTokenStatisticsEntity newEntity = new ProductTokenStatisticsEntity();
            newEntity.setId(productTokenStatisticsId);
            newEntity.setSuccessCount(0L);
            newEntity.setFailureCount(0L);
            newEntity.setCreatedAt(timestamp);
            return newEntity;
        });

        int status = statusCode / 100;
        if (status == 2) {
            productTokenStatisticsEntity.setSuccessCount(productTokenStatisticsEntity.getSuccessCount() + 1);
        } else if (status == 4 || status == 5) {
            productTokenStatisticsEntity.setFailureCount(productTokenStatisticsEntity.getFailureCount() + 1);
        }
        productTokenStatisticsEntity.setUpdatedAt(timestamp);

        productTokenStatisticsRepository.save(productTokenStatisticsEntity);
    }

}
