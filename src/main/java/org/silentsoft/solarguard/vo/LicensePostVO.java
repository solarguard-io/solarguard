package org.silentsoft.solarguard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.silentsoft.solarguard.entity.LicenseType;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicensePostVO {

    @Schema(required = true)
    private LicenseType licenseType;

    @Schema(nullable = true)
    private LocalDate expiredAt;

    @Schema(nullable = true)
    private Boolean isDeviceLimited;

    @Schema(nullable = true)
    private Long deviceLimit;

    @Schema(nullable = true)
    private String note;

}
