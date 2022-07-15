package org.silentsoft.solarguard.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@Entity(name = "licenses")
@DynamicInsert
public class LicenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "package_id")
    @Accessors(prefix = "_")
    private PackageEntity _package;

    private String key;

    @Enumerated(EnumType.STRING)
    private LicenseType type;

    private Date expiredAt;

    private Boolean isDeviceLimited;

    private Long deviceLimit;

    private String note;

    private Boolean isRevoked;

    private Timestamp revokedAt;

    private Long revokedBy;

    private Timestamp createdAt;

    private Long createdBy;

    private Timestamp updatedAt;

    private Long updatedBy;

}
