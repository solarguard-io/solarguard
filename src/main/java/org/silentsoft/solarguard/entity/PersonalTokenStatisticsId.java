package org.silentsoft.solarguard.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Date;

@Data
@NoArgsConstructor
@Embeddable
public class PersonalTokenStatisticsId implements Serializable {

    private long personalTokenId;

    private Date date;

    public PersonalTokenStatisticsId(long personalTokenId, Date date) {
        this.personalTokenId = personalTokenId;
        this.date = date;
    }

}
