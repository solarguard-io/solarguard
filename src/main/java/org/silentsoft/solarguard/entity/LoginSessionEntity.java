package org.silentsoft.solarguard.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import java.sql.Timestamp;

@Getter
@Setter
@Entity(name = "login_sessions")
@DynamicInsert
public class LoginSessionEntity {

    @Id
    private Long loginTokenId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private LoginTokenEntity loginToken;

    private String ipAddress;

    private String deviceName;

    private Timestamp usedAt;

    private Timestamp createdAt;

    private Long createdBy;

    private Timestamp updatedAt;

    private Long updatedBy;

}
