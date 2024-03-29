package org.project.nuwabackend.nuwa.file.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TopSevenFileInfoResponseDto(Long fileId,
                                          String fileName,
                                          Long fileSize,
                                          String fileExtension,
                                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
                                     LocalDateTime createdAt) {
}
