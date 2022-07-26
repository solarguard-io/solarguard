package org.silentsoft.solarguard.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class BundleId implements Serializable {

    @OneToOne
    @JoinColumn(name = "package_id")
    @Accessors(prefix = "_")
    private PackageEntity _package;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

}
