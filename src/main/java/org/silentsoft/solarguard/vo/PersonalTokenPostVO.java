package org.silentsoft.solarguard.vo;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalTokenPostVO {

    private String note;

    private LocalDate expiredAt;

}
