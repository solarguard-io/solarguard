package org.silentsoft.solarguard.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity(name = "personal_tokens")
@DynamicInsert
public class PersonalTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String accessToken;

    private String note;

    private Timestamp expiredAt;

    private Timestamp createdAt;

    private Long createdBy;

    private Timestamp updatedAt;

    private Long updatedBy;

}
