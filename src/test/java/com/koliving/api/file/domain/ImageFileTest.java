package com.koliving.api.file.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("이미지 파일 도메인 테스트")
class ImageFileTest {
    @Test
    @DisplayName("이미지 파일 도메인 생성")
    public void create() {
        // given
        ImageFile imageFile = ImageFile.valueOf(ImageType.JPG, "/root", "profile", 10523L);

        // then
        assertThat(imageFile.getType().isJpg()).isTrue();
    }
}