package com.koliving.api.file.infra;

import com.koliving.api.file.domain.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

    Optional<ImageFile> findByPath(String path);

}
