package sn.zahra.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import sn.zahra.domain.enumeration.StorageType;

@Data
public class FileRequestDTO {

    @NotNull
    @Schema(description = "Le fichier à upload avec extension")
    private MultipartFile file;

    @Schema(description = "Choisir le type de stockage entre local et database")
    private StorageType storageType = StorageType.LOCAL;

    public @NotNull MultipartFile getFile() {
        return file;
    }
    public void setFile(@NotNull MultipartFile file) {
        this.file = file;
    }
    public @NotNull StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(@NotNull StorageType storageType) {
        this.storageType = storageType;
    }
}
