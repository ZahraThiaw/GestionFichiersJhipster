package sn.zahra.service;

import java.util.Optional;
import sn.zahra.service.dto.FileEntityDTO;

/**
 * Service Interface for managing {@link sn.zahra.domain.FileEntity}.
 */
public interface FileEntityService {
    /**
     * Save a fileEntity.
     *
     * @param fileEntityDTO the entity to save.
     * @return the persisted entity.
     */
    FileEntityDTO save(FileEntityDTO fileEntityDTO);

    /**
     * Updates a fileEntity.
     *
     * @param fileEntityDTO the entity to update.
     * @return the persisted entity.
     */
    FileEntityDTO update(FileEntityDTO fileEntityDTO);

    /**
     * Partially updates a fileEntity.
     *
     * @param fileEntityDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FileEntityDTO> partialUpdate(FileEntityDTO fileEntityDTO);

    /**
     * Get the "id" fileEntity.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FileEntityDTO> findOne(Long id);

    /**
     * Delete the "id" fileEntity.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
