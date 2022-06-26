package org.silentsoft.solarguard.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Date;

@Data
@NoArgsConstructor
@Embeddable
public class ProductTokenStatisticsId implements Serializable {

    private long productTokenId;

    private Date date;

    public ProductTokenStatisticsId(long productTokenId, Date date) {
        this.productTokenId = productTokenId;
        this.date = date;
    }

}
