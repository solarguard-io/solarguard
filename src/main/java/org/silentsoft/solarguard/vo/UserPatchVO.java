package org.silentsoft.solarguard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.silentsoft.solarguard.entity.UserRole;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchVO {

    @Schema(nullable = true)
    private String username;

    @Schema(nullable = true)
    private String email;

    @Schema(nullable = true, minLength = 8)
    private String password;

    @Schema(nullable = true)
    private UserRole role;

}
