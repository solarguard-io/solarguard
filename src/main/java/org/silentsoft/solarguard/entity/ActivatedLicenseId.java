package org.silentsoft.solarguard.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class ActivatedLicenseId implements Serializable {

    private long licenseId;

    private String deviceId;

    public ActivatedLicenseId(long licenseId, String deviceId) {
        this.licenseId = licenseId;
        this.deviceId = deviceId;
    }

}
