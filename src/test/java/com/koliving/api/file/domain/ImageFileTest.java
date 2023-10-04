package com.koliving.api.file.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("이미지 파일 도메인 테스트")
class ImageFileTest {

    @Test
    @DisplayName("이미지 파일 도메인 생성")
    public void create() {
        // given
        ImageFile imageFile = ImageFile.valueOf("/root", 10523L);

        // then
        assertThat(imageFile.getPath()).isEqualTo("/root");
    }
}