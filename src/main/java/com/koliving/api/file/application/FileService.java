package com.koliving.api.file.application;

import com.koliving.api.file.domain.ImageFile;
import com.koliving.api.file.infra.FileRepository;
import com.koliving.api.file.infra.ImageFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final ImageFileRepository imageFileRepository;

    @Transactional
    public ImageFile upload(MultipartFile multipartFile) {
        String uploadedPath = fileRepository.upload(multipartFile);
        ImageFile imageFile = ImageFile.valueOf(uploadedPath, multipartFile.getSize());
        imageFileRepository.save(imageFile);
        return imageFile;
    }

    public List<ImageFile> list() {
        return imageFileRepository.findAll();
    }
}
