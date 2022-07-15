package org.silentsoft.solarguard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevicePatchVO {

    @Schema(required = true)
    private String name;

}
