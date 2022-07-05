package org.silentsoft.solarguard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationMemberPostVO {

    @Schema(required = true, minLength = 1)
    private List<Long> userIds;

}
