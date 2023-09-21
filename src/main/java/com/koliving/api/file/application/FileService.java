package com.koliving.api.file.application;

import com.koliving.api.file.domain.ImageFile;
import com.koliving.api.file.infra.FileRepository;
import com.koliving.api.file.infra.ImageFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final ImageFileRepository imageFileRepository;

    public ImageFile upload(MultipartFile multipartFile) {
        String uploadedPath = fileRepository.upload(multipartFile);
        ImageFile imageFile = ImageFile.valueOf(uploadedPath, multipartFile.getSize());
        imageFileRepository.save(imageFile);
        return imageFile;
    }
}
