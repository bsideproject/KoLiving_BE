package com.koliving.api.file.ui;

import com.koliving.api.file.application.FileService;
import com.koliving.api.file.domain.ImageFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Tag(name = "파일 API", description = "FILE API")
@Slf4j
@RestController
@RequestMapping("api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @Operation(
        summary = "파일 업로드",
        description = "파일을 업로드합니다.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "업로드 성공"
            )
        })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageFile> upload(@RequestPart MultipartFile file) {
        ImageFile response = fileService.upload(file);
        return ResponseEntity.created(URI.create("api/v1/files/" + response.getId()))
            .body(response);
    }
}
