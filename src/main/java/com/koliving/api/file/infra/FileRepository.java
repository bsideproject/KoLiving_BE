package com.koliving.api.file.infra;

import org.springframework.web.multipart.MultipartFile;

public interface FileRepository {
    String upload(MultipartFile multipartFile);
}
