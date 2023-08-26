package com.koliving.api.fixtures;

import com.koliving.api.room.domain.RoomType;
import com.koliving.api.room.domain.info.Quantity;
import com.koliving.api.room.domain.info.RoomInfo;

/**
 * author : haedoang date : 2023/08/26 description :
 */
public class RoomInfoFixture {

    public RoomInfo 스튜디오_방1_욕실1_룸메1 = RoomInfo.valueOf(RoomType.STUDIO, Quantity.ONE, Quantity.ONE, Quantity.ONE);

}
