package com.koliving.api.fixtures;


import com.koliving.api.location.domain.Location;

import static com.koliving.api.location.domain.LocationType.DONG;
import static com.koliving.api.location.domain.LocationType.GU;

public class LocationFixture {

    public static Location 성동구 = Location.valueOf("seongdong", GU);
    public static Location 성수동 = Location.valueOf("seongsu", DONG, 성동구);
}
