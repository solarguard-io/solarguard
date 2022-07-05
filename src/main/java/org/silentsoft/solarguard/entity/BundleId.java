package org.silentsoft.solarguard.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class BundleId implements Serializable {

    private long packageId;

    private long productId;

    public BundleId(long packageId, long productId) {
        this.packageId = packageId;
        this.productId = productId;
    }

}
