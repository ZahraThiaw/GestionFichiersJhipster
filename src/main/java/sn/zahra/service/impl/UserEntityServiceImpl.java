package sn.zahra.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.zahra.domain.UserEntity;
import sn.zahra.repository.UserEntityRepository;
import sn.zahra.service.UserEntityService;
import sn.zahra.service.dto.UserEntityDTO;
import sn.zahra.service.mapper.UserEntityMapper;

/**
 * Service Implementation for managing {@link sn.zahra.domain.UserEntity}.
 */
@Service
@Transactional
public class UserEntityServiceImpl implements UserEntityService {

    private static final Logger LOG = LoggerFactory.getLogger(UserEntityServiceImpl.class);

    private final UserEntityRepository userEntityRepository;

    private final UserEntityMapper userEntityMapper;

    public UserEntityServiceImpl(UserEntityRepository userEntityRepository, UserEntityMapper userEntityMapper) {
        this.userEntityRepository = userEntityRepository;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public UserEntityDTO save(UserEntityDTO userEntityDTO) {
        LOG.debug("Request to save UserEntity : {}", userEntityDTO);
        UserEntity userEntity = userEntityMapper.toEntity(userEntityDTO);
        userEntity = userEntityRepository.save(userEntity);
        return userEntityMapper.toDto(userEntity);
    }

    @Override
    public UserEntityDTO update(UserEntityDTO userEntityDTO) {
        LOG.debug("Request to update UserEntity : {}", userEntityDTO);
        UserEntity userEntity = userEntityMapper.toEntity(userEntityDTO);
        userEntity = userEntityRepository.save(userEntity);
        return userEntityMapper.toDto(userEntity);
    }

    @Override
    public Optional<UserEntityDTO> partialUpdate(UserEntityDTO userEntityDTO) {
        LOG.debug("Request to partially update UserEntity : {}", userEntityDTO);

        return userEntityRepository
            .findById(userEntityDTO.getId())
            .map(existingUserEntity -> {
                userEntityMapper.partialUpdate(existingUserEntity, userEntityDTO);

                return existingUserEntity;
            })
            .map(userEntityRepository::save)
            .map(userEntityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntityDTO> findOne(Long id) {
        LOG.debug("Request to get UserEntity : {}", id);
        return userEntityRepository.findById(id).map(userEntityMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete UserEntity : {}", id);
        userEntityRepository.deleteById(id);
    }
}
