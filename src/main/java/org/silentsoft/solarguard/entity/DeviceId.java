package org.silentsoft.solarguard.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class DeviceId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "license_id")
    private LicenseEntity license;

    private String code;

}
