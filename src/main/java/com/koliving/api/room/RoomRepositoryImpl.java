package com.koliving.api.room;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public void getRoomList() {

    }
}
