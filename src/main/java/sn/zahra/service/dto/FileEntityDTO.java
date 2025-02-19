package sn.zahra.service.dto;

import jakarta.persistence.Lob;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import sn.zahra.domain.enumeration.StorageType;

/**
 * A DTO for the {@link sn.zahra.domain.FileEntity} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FileEntityDTO implements Serializable {

    private Long id;

    private String fileName;

    private String originalFileName;

    private String contentType;

    private Long fileSize;

    private String filePath;

    private StorageType storageType;

    @Lob
    private byte[] fileData;

    private String fileDataContentType;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    private Boolean deleted;

    private ZonedDateTime deletedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getFileDataContentType() {
        return fileDataContentType;
    }

    public void setFileDataContentType(String fileDataContentType) {
        this.fileDataContentType = fileDataContentType;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public ZonedDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(ZonedDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileEntityDTO)) {
            return false;
        }

        FileEntityDTO fileEntityDTO = (FileEntityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, fileEntityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FileEntityDTO{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", originalFileName='" + getOriginalFileName() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", fileSize=" + getFileSize() +
            ", filePath='" + getFilePath() + "'" +
            ", storageType='" + getStorageType() + "'" +
            ", fileData='" + getFileData() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", deleted='" + getDeleted() + "'" +
            ", deletedAt='" + getDeletedAt() + "'" +
            "}";
    }
}
