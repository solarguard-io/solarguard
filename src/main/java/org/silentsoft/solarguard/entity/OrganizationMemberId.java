package org.silentsoft.solarguard.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class OrganizationMemberId implements Serializable {

    private long organizationId;

    private long userId;

    public OrganizationMemberId(long organizationId, long userId) {
        this.organizationId = organizationId;
        this.userId = userId;
    }

}
