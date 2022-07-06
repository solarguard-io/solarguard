package org.silentsoft.solarguard.vo;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTokenPatchVO {

    private String note;

    private Boolean revoke;

}
