package org.silentsoft.solarguard.repository;

import org.silentsoft.solarguard.entity.ProductTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductTokenRepository extends JpaRepository<ProductTokenEntity, Long> {

    List<ProductTokenEntity> findAllByProductId(long productId);

    List<ProductTokenEntity> findAllByProductIdIn(List<Long> productIds);

    ProductTokenEntity findByProductIdAndAccessToken(long productId, String accessToken);

}
