package com.koliving.api.room.infra;

import com.koliving.api.room.application.dto.RoomSearchCondition;
import com.koliving.api.room.domain.FurnishingType;
import com.koliving.api.room.domain.QFurnishing;
import com.koliving.api.room.domain.Room;
import com.koliving.api.room.domain.RoomType;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.koliving.api.room.domain.QRoom.room;

@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepositoryQueryDsl {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Room> search(Pageable pageable, RoomSearchCondition condition) {
        List<Room> rooms = queryFactory.selectFrom(room)
            .where(
                filterByLocationIds(condition.locationIds()),
                filterByDeposit(condition.minDeposit(), condition.maxDeposit()),
                filterByMonthlyRent(condition.minMonthlyRent(), condition.maxMonthlyRent()),
                filterByAvailableDate(condition.availableDate()),
                filterByTypes(condition.types()),
                filterByFurnishings(condition.furnishingTypes())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();


        long count = queryFactory.selectFrom(room)
            .where(
                filterByLocationIds(condition.locationIds()),
                filterByDeposit(condition.minDeposit(), condition.maxDeposit()),
                filterByMonthlyRent(condition.minMonthlyRent(), condition.maxMonthlyRent()),
                filterByAvailableDate(condition.availableDate()),
                filterByTypes(condition.types()),
                filterByFurnishings(condition.furnishingTypes())
            )
            .fetch()
            .size();


        return PageableExecutionUtils.getPage(rooms, pageable, () -> count);
    }

    private BooleanExpression filterByFurnishings(List<FurnishingType> furnishingTypes) {
        if (CollectionUtils.isEmpty(furnishingTypes)) {
            return null;
        }

        return room.furnishings.any().type.in(furnishingTypes);
    }

    private BooleanExpression filterByTypes(List<RoomType> types) {
        if (CollectionUtils.isEmpty(types)) {
            return null;
        }

        return room.roomInfo.roomType.in(types);
    }

    private BooleanExpression filterByAvailableDate(LocalDate localDate) {
        if (Objects.isNull(localDate)) {
            return null;
        }

        return room.availableDate.eq(localDate);
    }


    private BooleanExpression filterByLocationIds(List<Long> locationIds) {
        if (CollectionUtils.isEmpty(locationIds)) {
            return null;
        }

        return room.location.id.in(locationIds);
    }

    private BooleanExpression filterByDeposit(Integer minDeposit, Integer maxDeposit) {
        if (Objects.nonNull(minDeposit) && Objects.nonNull(maxDeposit)) {
            return room.deposit.amount.between(minDeposit, maxDeposit);
        } else if (Objects.nonNull(minDeposit)) {
            return room.deposit.amount.goe(minDeposit);
        } else if (Objects.nonNull(maxDeposit)) {
            return room.deposit.amount.loe(maxDeposit);
        }

        return null;
    }


    private BooleanExpression filterByMonthlyRent(Integer minMonthlyRent, Integer maxMonthlyRent) {
        if (Objects.nonNull(minMonthlyRent) && Objects.nonNull(maxMonthlyRent)) {
            return room.deposit.amount.between(minMonthlyRent, maxMonthlyRent);
        } else if (Objects.nonNull(minMonthlyRent)) {
            return room.deposit.amount.goe(minMonthlyRent);
        } else if (Objects.nonNull(maxMonthlyRent)) {
            return room.deposit.amount.loe(maxMonthlyRent);
        }

        return null;
    }
}
