package org.silentsoft.solarguard.vo;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    private String username;

    private String password;

    private String deviceName;

}
