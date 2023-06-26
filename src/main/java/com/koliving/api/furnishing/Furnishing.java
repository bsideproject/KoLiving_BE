package com.koliving.api.furnishing;

import com.koliving.api.room.Room;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Furnishing {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "furnishing_id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    private FurnishingType furnishingType;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

}
