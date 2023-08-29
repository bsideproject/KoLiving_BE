package com.koliving.api.room.domain;

import static lombok.AccessLevel.PROTECTED;

import com.koliving.api.base.ServiceError;
import com.koliving.api.base.exception.KolivingServiceException;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Maintenance {

    @Transient
    public static final int MAX_MAINTENANCE_FEE = 5_000_000;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "maintenance_fee", columnDefinition = "integer default 0"))
    private Money maintenanceFee;

    @Column(name = "gas_included", nullable = false, columnDefinition = "boolean default false")
    private boolean gasIncluded;

    @Column(name = "water_included", nullable = false, columnDefinition = "boolean default false")
    private boolean waterIncluded;

    @Column(name = "electricity_included", nullable = false, columnDefinition = "boolean default false")
    private boolean electricityIncluded;

    @Column(name = "cleaning_included", nullable = false, columnDefinition = "boolean default false")
    private boolean cleaningIncluded;

    private Maintenance(Money maintenanceFee) {
        this.maintenanceFee = maintenanceFee;
    }

    private Maintenance(
        Money maintenanceFee,
        boolean gasIncluded,
        boolean waterIncluded,
        boolean electricityIncluded,
        boolean cleaningIncluded
    ) {
        validate(maintenanceFee, gasIncluded, waterIncluded, electricityIncluded, cleaningIncluded);
        this.maintenanceFee = maintenanceFee;
        this.gasIncluded = gasIncluded;
        this.waterIncluded = waterIncluded;
        this.electricityIncluded = electricityIncluded;
        this.cleaningIncluded = cleaningIncluded;
    }

    private void validate(
        Money maintenanceFee,
        boolean gasIncluded,
        boolean waterIncluded,
        boolean electricityIncluded,
        boolean cleaningIncluded
    ) {
        if (isInvalidMaintenanceFee(maintenanceFee)) {
            throw new KolivingServiceException(ServiceError.INVALID_MAINTENANCE_FEE);
        }

        if (isEmptyAmountWithOptions(
            maintenanceFee,
            List.of(gasIncluded, waterIncluded, electricityIncluded, cleaningIncluded))
        ) {
            throw new KolivingServiceException(ServiceError.ILLEGAL_MAINTENANCE);
        }
    }

    private boolean isEmptyAmountWithOptions(Money maintenanceFee, List<Boolean> options) {
        if (!maintenanceFee.isEmpty()) {
            return false;
        }

        return options.stream()
            .anyMatch(option -> option == Boolean.TRUE);
    }

    private boolean isInvalidMaintenanceFee(Money maintenanceFee) {
        return maintenanceFee.value() > MAX_MAINTENANCE_FEE;
    }

    public int value() {
        return this.maintenanceFee.value();
    }

    public static Maintenance valueOf(
        Money maintenanceFee,
        boolean gasIncluded,
        boolean waterIncluded,
        boolean electricityIncluded,
        boolean cleaningIncluded
    ) {
        return new Maintenance(maintenanceFee, gasIncluded, waterIncluded, electricityIncluded, cleaningIncluded);
    }

    public static Maintenance empty() {
        return new Maintenance(Money.empty());
    }
}
