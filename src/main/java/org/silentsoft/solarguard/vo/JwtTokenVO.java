package org.silentsoft.solarguard.vo;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenVO {

    private String accessToken;

    private int expiry;

}
