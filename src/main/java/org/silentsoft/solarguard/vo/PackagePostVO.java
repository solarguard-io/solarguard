package org.silentsoft.solarguard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagePostVO {

    @Schema(required = true)
    private String name;

    @Schema(required = true, minLength = 1)
    private List<Long> productIds;

}
