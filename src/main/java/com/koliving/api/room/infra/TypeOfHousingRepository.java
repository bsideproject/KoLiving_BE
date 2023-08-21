package com.koliving.api.room.infra;


import com.koliving.api.room.domain.TypeOfHousing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeOfHousingRepository extends JpaRepository<TypeOfHousing, Long> {
}
