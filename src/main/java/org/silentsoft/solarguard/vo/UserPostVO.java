package org.silentsoft.solarguard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPostVO {

    @Schema(required = true)
    private String username;

    @Schema(nullable = true)
    private String email;

    @Schema(nullable = true, minLength = 8, description = "If not provided, user can set password at first login.")
    private String password;

}
