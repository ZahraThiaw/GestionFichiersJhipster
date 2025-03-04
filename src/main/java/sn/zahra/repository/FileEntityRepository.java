package sn.zahra.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.zahra.domain.FileEntity;

import java.util.Optional;

/**
 * Spring Data JPA repository for the FileEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FileEntityRepository extends JpaRepository<FileEntity, Long>, JpaSpecificationExecutor<FileEntity> {
    Optional<FileEntity> findByIdAndDeletedFalse(Long id);
}
