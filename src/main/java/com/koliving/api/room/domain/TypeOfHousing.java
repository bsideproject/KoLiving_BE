package com.koliving.api.room.domain;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "TB_TYPE_OF_HOUSING")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
public class TypeOfHousing {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(STRING)
    private RoomType type;

    private TypeOfHousing(RoomType type) {
        this.type = Objects.requireNonNull(type);
    }

    public static TypeOfHousing valueOf(RoomType roomType) {
        return new TypeOfHousing(roomType);
    }
}
