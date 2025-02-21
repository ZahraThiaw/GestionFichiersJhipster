// StorageStrategy.java
package sn.zahra.service.strategy;

import org.springframework.web.multipart.MultipartFile;
import sn.zahra.domain.FileEntity;
import sn.zahra.domain.enumeration.StorageType;


public interface StorageStrategy {
    void store(MultipartFile file, FileEntity fileEntity);
    byte[] retrieve(FileEntity fileEntity);
    boolean supports(StorageType storageType);
}
