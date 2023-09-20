package com.koliving.api.room.infra;

import com.koliving.api.room.application.dto.RoomSearchCondition;
import com.koliving.api.room.domain.Room;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.koliving.api.location.domain.QLocation.location;
import static com.koliving.api.room.domain.QRoom.room;

@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepositoryQueryDsl {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Room> search(Pageable pageable, RoomSearchCondition condition) {
        List<Room> rooms = queryFactory.selectFrom(room)
            .leftJoin(room.location)
            .where(
                haveLocationIds(condition.locationIds())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();


        long count = queryFactory.selectFrom(room)
            .leftJoin(room.location, location)
            .where(
                haveLocationIds(condition.locationIds())
            )
            .stream()
            .count();


        return PageableExecutionUtils.getPage(rooms, pageable, () -> count);
    }

    private BooleanExpression haveLocationIds(List<Long> locationIds) {
        if (CollectionUtils.isEmpty(locationIds)) {
            return null;
        }

        return room.location.id.in(locationIds);
    }
}
