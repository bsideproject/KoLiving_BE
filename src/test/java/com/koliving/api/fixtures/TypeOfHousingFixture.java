package com.koliving.api.fixtures;

import com.koliving.api.room.domain.RoomType;
import com.koliving.api.room.domain.TypeOfHousing;

/**
 * author : haedoang date : 2023/08/21 description :
 */
public class TypeOfHousingFixture {
    public static final TypeOfHousing 스튜디오 = TypeOfHousing.valueOf(RoomType.STUDIO);
    public static final TypeOfHousing 원룸 = TypeOfHousing.valueOf(RoomType.ONE_BED_FLATS);
    public static final TypeOfHousing 쉐어하우스 = TypeOfHousing.valueOf(RoomType.SHARE_HOUSE);

}
