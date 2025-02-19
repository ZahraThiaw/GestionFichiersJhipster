package sn.zahra.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import sn.zahra.domain.enumeration.StorageType;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.zahra.domain.FileEntity} entity. This class is used
 * in {@link sn.zahra.web.rest.FileEntityResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /file-entities?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FileEntityCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StorageType
     */
    public static class StorageTypeFilter extends Filter<StorageType> {

        public StorageTypeFilter() {}

        public StorageTypeFilter(StorageTypeFilter filter) {
            super(filter);
        }

        @Override
        public StorageTypeFilter copy() {
            return new StorageTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter fileName;

    private StringFilter originalFileName;

    private StringFilter contentType;

    private LongFilter fileSize;

    private StringFilter filePath;

    private StorageTypeFilter storageType;

    private ZonedDateTimeFilter createdAt;

    private ZonedDateTimeFilter updatedAt;

    private BooleanFilter deleted;

    private ZonedDateTimeFilter deletedAt;

    private Boolean distinct;

    public FileEntityCriteria() {}

    public FileEntityCriteria(FileEntityCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.fileName = other.optionalFileName().map(StringFilter::copy).orElse(null);
        this.originalFileName = other.optionalOriginalFileName().map(StringFilter::copy).orElse(null);
        this.contentType = other.optionalContentType().map(StringFilter::copy).orElse(null);
        this.fileSize = other.optionalFileSize().map(LongFilter::copy).orElse(null);
        this.filePath = other.optionalFilePath().map(StringFilter::copy).orElse(null);
        this.storageType = other.optionalStorageType().map(StorageTypeFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(ZonedDateTimeFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(ZonedDateTimeFilter::copy).orElse(null);
        this.deleted = other.optionalDeleted().map(BooleanFilter::copy).orElse(null);
        this.deletedAt = other.optionalDeletedAt().map(ZonedDateTimeFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public FileEntityCriteria copy() {
        return new FileEntityCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getFileName() {
        return fileName;
    }

    public Optional<StringFilter> optionalFileName() {
        return Optional.ofNullable(fileName);
    }

    public StringFilter fileName() {
        if (fileName == null) {
            setFileName(new StringFilter());
        }
        return fileName;
    }

    public void setFileName(StringFilter fileName) {
        this.fileName = fileName;
    }

    public StringFilter getOriginalFileName() {
        return originalFileName;
    }

    public Optional<StringFilter> optionalOriginalFileName() {
        return Optional.ofNullable(originalFileName);
    }

    public StringFilter originalFileName() {
        if (originalFileName == null) {
            setOriginalFileName(new StringFilter());
        }
        return originalFileName;
    }

    public void setOriginalFileName(StringFilter originalFileName) {
        this.originalFileName = originalFileName;
    }

    public StringFilter getContentType() {
        return contentType;
    }

    public Optional<StringFilter> optionalContentType() {
        return Optional.ofNullable(contentType);
    }

    public StringFilter contentType() {
        if (contentType == null) {
            setContentType(new StringFilter());
        }
        return contentType;
    }

    public void setContentType(StringFilter contentType) {
        this.contentType = contentType;
    }

    public LongFilter getFileSize() {
        return fileSize;
    }

    public Optional<LongFilter> optionalFileSize() {
        return Optional.ofNullable(fileSize);
    }

    public LongFilter fileSize() {
        if (fileSize == null) {
            setFileSize(new LongFilter());
        }
        return fileSize;
    }

    public void setFileSize(LongFilter fileSize) {
        this.fileSize = fileSize;
    }

    public StringFilter getFilePath() {
        return filePath;
    }

    public Optional<StringFilter> optionalFilePath() {
        return Optional.ofNullable(filePath);
    }

    public StringFilter filePath() {
        if (filePath == null) {
            setFilePath(new StringFilter());
        }
        return filePath;
    }

    public void setFilePath(StringFilter filePath) {
        this.filePath = filePath;
    }

    public StorageTypeFilter getStorageType() {
        return storageType;
    }

    public Optional<StorageTypeFilter> optionalStorageType() {
        return Optional.ofNullable(storageType);
    }

    public StorageTypeFilter storageType() {
        if (storageType == null) {
            setStorageType(new StorageTypeFilter());
        }
        return storageType;
    }

    public void setStorageType(StorageTypeFilter storageType) {
        this.storageType = storageType;
    }

    public ZonedDateTimeFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<ZonedDateTimeFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public ZonedDateTimeFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new ZonedDateTimeFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTimeFilter createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTimeFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<ZonedDateTimeFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public ZonedDateTimeFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new ZonedDateTimeFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTimeFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BooleanFilter getDeleted() {
        return deleted;
    }

    public Optional<BooleanFilter> optionalDeleted() {
        return Optional.ofNullable(deleted);
    }

    public BooleanFilter deleted() {
        if (deleted == null) {
            setDeleted(new BooleanFilter());
        }
        return deleted;
    }

    public void setDeleted(BooleanFilter deleted) {
        this.deleted = deleted;
    }

    public ZonedDateTimeFilter getDeletedAt() {
        return deletedAt;
    }

    public Optional<ZonedDateTimeFilter> optionalDeletedAt() {
        return Optional.ofNullable(deletedAt);
    }

    public ZonedDateTimeFilter deletedAt() {
        if (deletedAt == null) {
            setDeletedAt(new ZonedDateTimeFilter());
        }
        return deletedAt;
    }

    public void setDeletedAt(ZonedDateTimeFilter deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FileEntityCriteria that = (FileEntityCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(fileName, that.fileName) &&
            Objects.equals(originalFileName, that.originalFileName) &&
            Objects.equals(contentType, that.contentType) &&
            Objects.equals(fileSize, that.fileSize) &&
            Objects.equals(filePath, that.filePath) &&
            Objects.equals(storageType, that.storageType) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(deleted, that.deleted) &&
            Objects.equals(deletedAt, that.deletedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            fileName,
            originalFileName,
            contentType,
            fileSize,
            filePath,
            storageType,
            createdAt,
            updatedAt,
            deleted,
            deletedAt,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FileEntityCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFileName().map(f -> "fileName=" + f + ", ").orElse("") +
            optionalOriginalFileName().map(f -> "originalFileName=" + f + ", ").orElse("") +
            optionalContentType().map(f -> "contentType=" + f + ", ").orElse("") +
            optionalFileSize().map(f -> "fileSize=" + f + ", ").orElse("") +
            optionalFilePath().map(f -> "filePath=" + f + ", ").orElse("") +
            optionalStorageType().map(f -> "storageType=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalDeleted().map(f -> "deleted=" + f + ", ").orElse("") +
            optionalDeletedAt().map(f -> "deletedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
