package com.koliving.api.room.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "TB_FURNISHING")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
public class Furnishing {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(STRING)
    private FurnishingType type;

    private Furnishing(FurnishingType type) {
        this.type = Objects.requireNonNull(type);
    }

    public static Furnishing valueOf(FurnishingType type) {
        return new Furnishing(type);
    }
}
