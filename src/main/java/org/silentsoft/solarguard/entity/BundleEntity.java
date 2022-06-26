package org.silentsoft.solarguard.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
@Entity(name = "bundles")
@DynamicInsert
public class BundleEntity implements Serializable {

    @Delegate
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    @EmbeddedId
    private BundleId id;

    private Timestamp createdAt;

    private Long createdBy;

    private Timestamp updatedAt;

    private Long updatedBy;

}
