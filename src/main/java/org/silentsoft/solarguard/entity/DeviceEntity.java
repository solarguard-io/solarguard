package org.silentsoft.solarguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
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
    @Delegate
    @JsonIgnore
    private DeviceId id;

    private String name;

    private Long activationCount;

    private Boolean isBanned;

    private Timestamp firstActivatedAt;

    private Timestamp lastActivatedAt;

}
