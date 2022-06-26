package org.silentsoft.solarguard.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.sql.Timestamp;

@Getter
@Setter
@Entity(name = "organization_members")
@DynamicInsert
public class OrganizationMemberEntity {

    @EmbeddedId
    private OrganizationMemberId id;

    @Enumerated(EnumType.STRING)
    private OrganizationMemberRole role;

    private Timestamp createdAt;

    private Long createdBy;

    private Timestamp updatedAt;

    private Long updatedBy;

}
