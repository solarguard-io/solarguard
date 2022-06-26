package org.silentsoft.solarguard.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class BundleId implements Serializable {

    private long id;

    private long productId;

    public BundleId(long id, long productId) {
        this.id = id;
        this.productId = productId;
    }

}
