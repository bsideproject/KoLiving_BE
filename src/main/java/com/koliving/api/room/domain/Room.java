package com.koliving.api.room.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

//TODO Room 도메인생성

@Getter
@Entity(name = "TB_ROOM")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
public class Room {
    @Transient
    public static final Long MAX_DEPOSIT = 500_000_000L;

    @Transient
    public static final Long MAX_MONTHLY_RENT = 20_000_000L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column
    private Long locationId;

//    @Embedded
//    private Money deposit;
//
//    @Embedded
//    private Money monthlyRent;


    //TODO 이미지
    @Transient
    private List<Long> images;

    //TODO 유저
    @Transient
    private Long userId;

}
