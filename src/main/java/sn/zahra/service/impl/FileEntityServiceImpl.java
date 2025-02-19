package sn.zahra.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.zahra.domain.FileEntity;
import sn.zahra.repository.FileEntityRepository;
import sn.zahra.service.FileEntityService;
import sn.zahra.service.dto.FileEntityDTO;
import sn.zahra.service.mapper.FileEntityMapper;

/**
 * Service Implementation for managing {@link sn.zahra.domain.FileEntity}.
 */
@Service
@Transactional
public class FileEntityServiceImpl implements FileEntityService {

    private static final Logger LOG = LoggerFactory.getLogger(FileEntityServiceImpl.class);

    private final FileEntityRepository fileEntityRepository;

    private final FileEntityMapper fileEntityMapper;

    public FileEntityServiceImpl(FileEntityRepository fileEntityRepository, FileEntityMapper fileEntityMapper) {
        this.fileEntityRepository = fileEntityRepository;
        this.fileEntityMapper = fileEntityMapper;
    }

    @Override
    public FileEntityDTO save(FileEntityDTO fileEntityDTO) {
        LOG.debug("Request to save FileEntity : {}", fileEntityDTO);
        FileEntity fileEntity = fileEntityMapper.toEntity(fileEntityDTO);
        fileEntity = fileEntityRepository.save(fileEntity);
        return fileEntityMapper.toDto(fileEntity);
    }

    @Override
    public FileEntityDTO update(FileEntityDTO fileEntityDTO) {
        LOG.debug("Request to update FileEntity : {}", fileEntityDTO);
        FileEntity fileEntity = fileEntityMapper.toEntity(fileEntityDTO);
        fileEntity = fileEntityRepository.save(fileEntity);
        return fileEntityMapper.toDto(fileEntity);
    }

    @Override
    public Optional<FileEntityDTO> partialUpdate(FileEntityDTO fileEntityDTO) {
        LOG.debug("Request to partially update FileEntity : {}", fileEntityDTO);

        return fileEntityRepository
            .findById(fileEntityDTO.getId())
            .map(existingFileEntity -> {
                fileEntityMapper.partialUpdate(existingFileEntity, fileEntityDTO);

                return existingFileEntity;
            })
            .map(fileEntityRepository::save)
            .map(fileEntityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FileEntityDTO> findOne(Long id) {
        LOG.debug("Request to get FileEntity : {}", id);
        return fileEntityRepository.findById(id).map(fileEntityMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete FileEntity : {}", id);
        fileEntityRepository.deleteById(id);
    }
}
