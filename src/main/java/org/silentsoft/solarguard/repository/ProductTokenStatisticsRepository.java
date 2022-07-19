package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.ProductTokenStatisticsEntity;
import org.silentsoft.solarguard.entity.ProductTokenStatisticsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductTokenStatisticsRepository extends JpaRepository<ProductTokenStatisticsEntity, ProductTokenStatisticsId> {

    @Modifying
    @Query("delete from product_token_statistics where id.productTokenId = :productTokenId")
    void deleteAllByProductTokenId(long productTokenId);

    @Modifying
    @Query("delete from product_token_statistics where id.productTokenId in :productTokenIds")
    void deleteAllByProductTokenIdIn(List<Long> productTokenIds);

}
