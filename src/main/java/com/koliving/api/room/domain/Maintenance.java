package com.koliving.api.room.domain;

import static lombok.AccessLevel.PROTECTED;

import com.koliving.api.base.ServiceError;
import com.koliving.api.base.exception.KolivingServiceException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Maintenance {

    @Transient
    public static final Integer MIN_MAINTENANCE_FEE = 0;

    @Transient
    public static final Integer MAX_MAINTENANCE_FEE = 5_000_000;

    private int amount;

    @Column(name = "gas_included", nullable = false, columnDefinition = "boolean default false")
    private boolean gasIncluded;

    @Column(name = "water_included", nullable = false, columnDefinition = "boolean default false")
    private boolean waterIncluded;

    @Column(name = "electricity_included", nullable = false, columnDefinition = "boolean default false")
    private boolean electricityIncluded;

    @Column(name = "cleaning_included", nullable = false, columnDefinition = "boolean default false")
    private boolean cleaningIncluded;

    private Maintenance(
        int amount,
        boolean gasIncluded,
        boolean waterIncluded,
        boolean electricityIncluded,
        boolean cleaningIncluded
    ) {
        validate(amount, gasIncluded, waterIncluded, electricityIncluded, cleaningIncluded);
        this.amount = amount;
        this.gasIncluded = gasIncluded;
        this.waterIncluded = waterIncluded;
        this.electricityIncluded = electricityIncluded;
        this.cleaningIncluded = cleaningIncluded;
    }

    private void validate(Integer amount, boolean gasIncluded, boolean waterIncluded, boolean electricityIncluded, boolean cleaningIncluded) {
        if (isInvalidAmount(amount)) {
            throw new KolivingServiceException(ServiceError.INVALID_MAINTENANCE_FEE);
        }

        if (isEmptyAmountWithOptions(amount, List.of(gasIncluded, waterIncluded, electricityIncluded, cleaningIncluded))) {
            throw new KolivingServiceException(ServiceError.ILLEGAL_MAINTENANCE);
        }
    }

    private boolean isEmptyAmountWithOptions(int amount, List<Boolean> options) {
        if (amount != MIN_MAINTENANCE_FEE) {
            return true;
        }

        return options.stream()
            .anyMatch(option -> option == Boolean.TRUE);
    }

    private boolean isInvalidAmount(Integer amount) {
        return amount < MIN_MAINTENANCE_FEE || amount > MAX_MAINTENANCE_FEE;
    }

    public static Maintenance valueOf(
        int amount,
        boolean gasIncluded,
        boolean waterIncluded,
        boolean electricityIncluded,
        boolean cleaningIncluded
    ) {
        return new Maintenance(amount, gasIncluded, waterIncluded, electricityIncluded, cleaningIncluded);
    }

    public static Maintenance valueOfEmpty() {
        return new Maintenance();
    }
}
