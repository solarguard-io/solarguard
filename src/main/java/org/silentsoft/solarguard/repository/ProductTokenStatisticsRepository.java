package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.ProductTokenStatisticsEntity;
import org.silentsoft.solarguard.entity.ProductTokenStatisticsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTokenStatisticsRepository extends JpaRepository<ProductTokenStatisticsEntity, ProductTokenStatisticsId> {

    void deleteAllById_ProductTokenId(long productTokenId);

}
