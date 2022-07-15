package org.silentsoft.solarguard.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.sql.Timestamp;

@Getter
@Setter
@Entity(name = "devices")
@DynamicInsert
public class DeviceEntity {

    @EmbeddedId
    private DeviceId id;

    private String name;

    private Long activationCount;

    private Boolean isBanned;

    private Timestamp firstActivatedAt;

    private Timestamp lastActivatedAt;

}
