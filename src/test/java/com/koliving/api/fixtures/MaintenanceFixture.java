package com.koliving.api.fixtures;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import com.koliving.api.room.domain.Maintenance;
import com.koliving.api.room.domain.Money;

/**
 * author : haedoang date : 2023/08/26 description :
 */
public class MaintenanceFixture {
    public static final Maintenance 관리비_없음 = Maintenance.empty();
    public static final Maintenance 관리비30_전기세포함 = Maintenance.valueOf(Money.valueOf(300_000), FALSE, FALSE, TRUE, FALSE);
}
