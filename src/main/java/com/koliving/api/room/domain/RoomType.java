package com.koliving.api.room.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {
    STUDIO(
        "studio"
    ) {
        @Override
        public boolean isValidBedrooms(Integer count) {
            return 0 == count;
        }

        @Override
        public boolean isValidBathrooms(Integer count) {
            return 1 <= count;
        }

        @Override
        public boolean isValidRoommates(Integer count) {
            return 1 <= count;
        }
    },
    ONE_BED_FLATS(
        "1bed flats"
    ) {
        @Override
        public boolean isValidBedrooms(Integer count) {
            return 1 == count;
        }

        @Override
        public boolean isValidBathrooms(Integer count) {
            return 1 <= count;
        }

        @Override
        public boolean isValidRoommates(Integer count) {
            return 1 <= count;
        }
    },
    SHARE_HOUSE(
        "share house"
    ) {
        @Override
        public boolean isValidBedrooms(Integer count) {
            return 2 <= count;
        }

        @Override
        public boolean isValidBathrooms(Integer count) {
            return 1 <= count;
        }

        @Override
        public boolean isValidRoommates(Integer count) {
            return 1 <= count;
        }
    };

    private final String description;

    public abstract boolean isValidBedrooms(Integer count);

    public abstract boolean isValidBathrooms(Integer count);

    public abstract boolean isValidRoommates(Integer count);

    public boolean isStudio() {
        return this == STUDIO;
    }

    public boolean isOneBedFlats() {
        return this == ONE_BED_FLATS;
    }

    public boolean isShareHouse() {
        return this == SHARE_HOUSE;
    }
}
