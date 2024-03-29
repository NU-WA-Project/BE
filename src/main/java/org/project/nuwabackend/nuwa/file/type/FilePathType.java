package org.project.nuwabackend.nuwa.file.type;

import lombok.Getter;

@Getter
public enum FilePathType {
    IMAGE_PATH("image/"), FILE_PATH("file/");

    private final String value;

    FilePathType(String value) {
        this.value = value;
    }
}
