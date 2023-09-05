package com.koliving.api.location.domain;

import com.koliving.api.base.domain.BaseEntity;
import com.koliving.api.base.exception.KolivingServiceException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.Objects;

import static com.koliving.api.base.ServiceError.INVALID_LOCATION;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@ToString
@Getter
@Entity(name = "TB_LOCATION")
@SQLDelete(sql = "UPDATE TB_LOCATION SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = PROTECTED)
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "location_type")
    @Enumerated(STRING)
    private LocationType locationType;

    @ManyToOne
    @JoinColumn(name = "upper_location_id")
    private Location upperLocation;

    private Location(String name, LocationType locationType, Location upperLocation) {
        validate(locationType, upperLocation);
        this.name = Objects.requireNonNull(name);
        this.locationType = Objects.requireNonNull(locationType);
        this.upperLocation = upperLocation;
    }

    private void validate(LocationType locationType, Location upperLocation) {
        if (locationType.isRequiredUpperLocation() && Objects.isNull(upperLocation)) {
            throw new KolivingServiceException(INVALID_LOCATION);
        }

        if (locationType.isTopLocation() && Objects.nonNull(upperLocation)) {
            throw new KolivingServiceException(INVALID_LOCATION);
        }
    }

    public static Location valueOf(String name, LocationType locationType) {
        return new Location(name, locationType, null);
    }

    public static Location valueOf(String name, LocationType locationType, Location upperLocation) {
        return new Location(name, locationType, upperLocation);
    }

    public String displayName() {
        return String.format("%s-%s", name, locationType.getEnName());
    }

    public Long getUpperLocationId() {
        if (Objects.isNull(upperLocation)) {
            return null;
        }

        return upperLocation.id;
    }

    public void update(String name) {
        this.name = Objects.requireNonNull(name);
    }
}
