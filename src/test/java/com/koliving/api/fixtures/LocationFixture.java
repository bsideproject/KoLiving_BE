package com.koliving.api.fixtures;


import static com.koliving.api.location.domain.LocationType.DONG;
import static com.koliving.api.location.domain.LocationType.GU;

import com.koliving.api.location.domain.Location;

public class LocationFixture {

    public static Location 성동구 = Location.valueOf("seongdong", "성동", GU);
    public static Location 성수동 = Location.valueOf("seongsu", "성수", DONG, 성동구);
}
