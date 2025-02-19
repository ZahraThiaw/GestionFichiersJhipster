package sn.zahra.service;

import java.util.Optional;
import sn.zahra.service.dto.UserEntityDTO;

/**
 * Service Interface for managing {@link sn.zahra.domain.UserEntity}.
 */
public interface UserEntityService {
    /**
     * Save a userEntity.
     *
     * @param userEntityDTO the entity to save.
     * @return the persisted entity.
     */
    UserEntityDTO save(UserEntityDTO userEntityDTO);

    /**
     * Updates a userEntity.
     *
     * @param userEntityDTO the entity to update.
     * @return the persisted entity.
     */
    UserEntityDTO update(UserEntityDTO userEntityDTO);

    /**
     * Partially updates a userEntity.
     *
     * @param userEntityDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<UserEntityDTO> partialUpdate(UserEntityDTO userEntityDTO);

    /**
     * Get the "id" userEntity.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UserEntityDTO> findOne(Long id);

    /**
     * Delete the "id" userEntity.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
