package sn.zahra.service.validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import sn.zahra.service.exception.BadRequestException;

public class FileValidator {

    @Value("${file.upload.max-file-size}")
    private long maxSizefile ;

    private final FileStorageConfig fileStorageConfig;
    private static final String ENTITY_NAME = "gestionfichiersjhipsterFileEntity";

    public FileValidator(FileStorageConfig fileStorageConfig) {
        this.fileStorageConfig = fileStorageConfig;
    }

    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty", ENTITY_NAME, "empty" );
        }

        if (file.getSize() > fileStorageConfig.getMaxFileSize()) {
            throw new BadRequestException("File size exceeds maximum limit of " +
                fileStorageConfig.getMaxFileSize() / maxSizefile + "MB", ENTITY_NAME, "size_exceeded");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new BadRequestException("File type not allowed. Allowed types: " +
                String.join(", ", fileStorageConfig.getAllowedContentTypes()), ENTITY_NAME, "invalid_type");
        }
    }

    private boolean isAllowedContentType(String contentType) {
        return fileStorageConfig.getAllowedContentTypes().contains(contentType);
    }
}
