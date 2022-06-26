package org.silentsoft.solarguard.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.sql.Timestamp;

@Getter
@Setter
@Entity(name = "product_token_statistics")
@DynamicInsert
public class ProductTokenStatisticsEntity {

    @EmbeddedId
    private ProductTokenStatisticsId id;

    private Long successCount;

    private Long failureCount;

    private Timestamp createdAt;

    private Timestamp updatedAt;

}
