package com.koliving.api.room.domain;

import com.koliving.api.base.domain.BaseEntity;
import com.koliving.api.base.exception.KolivingServiceException;
import com.koliving.api.file.domain.ImageFile;
import com.koliving.api.location.domain.Location;
import com.koliving.api.room.domain.info.RoomInfo;
import com.koliving.api.user.domain.User;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.koliving.api.base.ServiceError.ILLEGAL_ROOM_INFO;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;


@Getter
@Entity(name = "TB_ROOM")
@SQLDelete(sql = "UPDATE TB_ROOM SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = PROTECTED)
public class Room extends BaseEntity {

    @Transient
    public static final Long MAX_DEPOSIT = 500_000_000L;

    @Transient
    public static final Long MAX_MONTHLY_RENT = 20_000_000L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Boolean available = Boolean.FALSE;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "deposit", columnDefinition = "integer default 0"))
    private Money deposit;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "monthly_rent", columnDefinition = "integer default 0"))
    private Money monthlyRent;

    @Embedded
    private Maintenance maintenance;

    @Embedded
    private RoomInfo roomInfo;

    @ManyToMany
    @JoinTable(
        name = "TB_ROOM_FURNISHINGS",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "furnishing_id")
    )
    private Set<Furnishing> furnishings = new HashSet<>();

    @Column(name = "available_date")
    private LocalDate availableDate;

    @Lob
    private String description;

    @ManyToMany
    @JoinTable(
        name = "TB_ROOM_IMAGES",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private Set<ImageFile> imageFiles;

    private Room(Location location, RoomInfo roomInfo, Money deposit, Money monthlyRent, Maintenance maintenance, Set<Furnishing> furnishings, LocalDate availableDate, boolean available, String description, Set<ImageFile> imageFiles) {
        validate(deposit, monthlyRent, imageFiles);
        this.location = location;
        this.roomInfo = roomInfo;
        this.deposit = deposit;
        this.monthlyRent = monthlyRent;
        this.maintenance = maintenance;
        this.furnishings = furnishings;
        this.availableDate = availableDate;
        this.available = available;
        this.description = description;
        this.imageFiles = imageFiles;
    }

    public static Room valueOf(
        Location location,
        RoomInfo info,
        Money deposit,
        Money monthlyRent,
        Maintenance maintenance,
        Set<Furnishing> furnishings,
        LocalDate availableDate,
        String description,
        Set<ImageFile> imageFiles
    ) {
        return new Room(location, info, deposit, monthlyRent, maintenance, furnishings, availableDate, Objects.isNull(availableDate), description, imageFiles);
    }

    private void validate(Money deposit, Money monthlyRent, Set<ImageFile> imageFiles) {
        //TODO 최대값 validation 추가
    }

    public Room by(User user) {
        if (Objects.isNull(user)) {
            throw new KolivingServiceException(ILLEGAL_ROOM_INFO);
        }

        this.user = user;
        return this;
    }

    public boolean checkUser(User user) {
        return this.user.equals(user);
    }
}
