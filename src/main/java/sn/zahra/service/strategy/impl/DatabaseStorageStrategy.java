// DatabaseStorageStrategy.java
package sn.zahra.service.strategy.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sn.zahra.domain.FileEntity;
import sn.zahra.domain.enumeration.StorageType;
import sn.zahra.repository.FileEntityRepository;
import sn.zahra.service.strategy.StorageStrategy;

import java.io.IOException;
import java.util.UUID;

@Service
public class DatabaseStorageStrategy implements StorageStrategy {

    private final FileEntityRepository fileRepository;

    public DatabaseStorageStrategy(FileEntityRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public void store(MultipartFile file, FileEntity fileEntity) {
        try {
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFileName);
            String nameWithoutExtension = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            String fileName = nameWithoutExtension + "_" + UUID.randomUUID().toString() + "." + fileExtension;

            fileEntity.setFileData(file.getBytes());
            fileEntity.setFileDataContentType(file.getContentType());
            fileEntity.setStorageType(StorageType.DATABASE);
            fileEntity.setFileName(fileName);
            fileEntity.setOriginalFileName(originalFileName);
            fileEntity.setContentType(file.getContentType());
            fileEntity.setFileSize(file.getSize());
            fileEntity.setDeleted(false);

            fileRepository.save(fileEntity);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file in database", e);
        }
    }

    @Override
    public byte[] retrieve(FileEntity fileEntity) {
        return fileEntity.getFileData();
    }

    @Override
    public boolean supports(StorageType storageType) {
        return StorageType.DATABASE.equals(storageType);
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
