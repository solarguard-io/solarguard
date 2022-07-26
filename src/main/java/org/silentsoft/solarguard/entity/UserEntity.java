package org.silentsoft.solarguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity(name = "users")
@DynamicInsert
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    private Boolean isTemporaryPassword;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Timestamp createdAt;

    private Long createdBy;

    private Timestamp updatedAt;

    private Long updatedBy;

}
