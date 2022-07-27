package org.silentsoft.solarguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
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
    @Delegate
    @JsonIgnore
    private OrganizationMemberId id;

    @Enumerated(EnumType.STRING)
    private OrganizationMemberRole role;

    private Timestamp createdAt;

    private Long createdBy;

    private Timestamp updatedAt;

    private Long updatedBy;

}
