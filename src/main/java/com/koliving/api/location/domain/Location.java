package com.koliving.api.location.domain;

import static com.koliving.api.base.ServiceError.*;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.koliving.api.base.ServiceError;
import com.koliving.api.base.exception.KolivingServiceException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "TB_LOCATION")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
public class Location {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, name = "en_name")
    private String enName;

    @Column(name = "kr_name")
    private String krName;

    @Column(nullable = false,name = "location_type")
    @Enumerated(STRING)
    private LocationType locationType;

    @ManyToOne
    @JoinColumn(name = "upper_location_id")
    private Location upperLocation;

    private Location(String enName, String krName, LocationType locationType, Location upperLocation) {
        validate(locationType, upperLocation);
        this.enName = Objects.requireNonNull(enName);
        this.krName = krName;
        this.locationType = Objects.requireNonNull(locationType);
        this.upperLocation = upperLocation;
    }

    //TODO 예외 코드화하기
    private void validate(LocationType locationType, Location upperLocation) {
        if (locationType.isRequiredUpperLocation() && Objects.isNull(upperLocation)) {
            throw new KolivingServiceException(INVALID_LOCATION);
        }

        if (locationType.isTopLocation() && Objects.nonNull(upperLocation)) {
            throw new KolivingServiceException(INVALID_LOCATION);
        }
    }

    public static Location valueOf(String enName, String krName, LocationType locationType) {
        return new Location(enName, krName, locationType, null);
    }

    public static Location valueOf(String enName, String krName, LocationType locationType, Location upperLocation) {
        return new Location(enName, krName, locationType, upperLocation);
    }

    public String displayName() {
        return String.format("%s-%s", enName, locationType.getEnName());
    }

    public Long getUpperLocationId() {
        if (Objects.isNull(upperLocation)) {
            return null;
        }

        return upperLocation.id;
    }

    public void update(String enName, String krName) {
        this.enName = Objects.requireNonNull(enName);
        this.krName = krName;
    }
}
