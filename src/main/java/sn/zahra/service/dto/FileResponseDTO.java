package sn.zahra.service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FileResponseDTO {

    private Long id;

    private String fileName;

    private String originalFileName;

    private String contentType;

    private Long fileSize;

    private LocalDateTime createdAt;
}
