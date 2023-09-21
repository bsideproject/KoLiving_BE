package com.koliving.api.file.domain;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum ImageType implements FileType {
    JPG("jpg"),
    PNG("png"),

    ETC("");

    private final String extension;

    @Override
    public String getExtension() {
        return extension;
    }

    public boolean isJpg() {
        return this == JPG;
    }

    public static ImageType of(String extension) {
        return Arrays.stream(values())
            .filter(type ->  type.getExtension().equals(extension))
            .findFirst()
            .orElse(ETC);
    }
}
