package org.silentsoft.solarguard.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class DeviceId implements Serializable {

    private long licenseId;

    private String code;

    public DeviceId(long licenseId, String code) {
        this.licenseId = licenseId;
        this.code = code;
    }

}
