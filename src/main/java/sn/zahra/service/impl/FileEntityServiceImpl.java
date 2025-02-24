package sn.zahra.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sn.zahra.domain.FileEntity;
import sn.zahra.repository.FileEntityRepository;
import sn.zahra.service.FileEntityService;
import sn.zahra.service.dto.FileEntityDTO;
import sn.zahra.service.dto.FileRequestDTO;
import sn.zahra.service.exception.ResourceNotFoundException;
import sn.zahra.service.mapper.FileEntityMapper;
import sn.zahra.service.mapper.FileRequestMapper;
import sn.zahra.service.strategy.StorageStrategy;
import sn.zahra.service.strategy.factory.StorageStrategyFactory;
import sn.zahra.service.validator.FileStorageConfig;
import sn.zahra.service.validator.FileValidator;

/**
 * Service Implementation for managing {@link sn.zahra.domain.FileEntity}.
 */
@Service
@Transactional
public class FileEntityServiceImpl implements FileEntityService {

    private static final Logger LOG = LoggerFactory.getLogger(FileEntityServiceImpl.class);

    private final FileEntityRepository fileEntityRepository;

    private final FileEntityMapper fileEntityMapper;

    private final StorageStrategyFactory storageStrategyFactory;

    private final FileValidator fileValidator;

    public FileEntityServiceImpl(FileEntityRepository fileEntityRepository, FileEntityMapper fileEntityMapper, StorageStrategyFactory storageStrategyFactory, FileStorageConfig fileStorageConfig) {
        this.fileEntityRepository = fileEntityRepository;
        this.fileEntityMapper = fileEntityMapper;
        this.storageStrategyFactory = storageStrategyFactory;
        this.fileValidator = new FileValidator(fileStorageConfig);
    }

    @Override
    public FileEntityDTO save(FileRequestDTO fileRequestDTO) {
        LOG.debug("Request to save FileEntity : {}", fileRequestDTO);

        // Valider le fichier
        fileValidator.validateFile(fileRequestDTO.getFile());

        // Créer une nouvelle entité
        FileEntity fileEntity = new FileEntity();

        // Sélectionner la bonne stratégie de stockage et sauvegarder le fichier
        StorageStrategy strategy = storageStrategyFactory.getStrategy(fileRequestDTO.getStorageType());
        strategy.store(fileRequestDTO.getFile(), fileEntity);

        return fileEntityMapper.toDto(fileEntity);
    }

    public byte[] downloadFile(Long fileId) {
        // Récupérer l'entité de fichier depuis la base de données
        FileEntity fileEntity = fileEntityRepository.findByIdAndDeletedFalse(fileId)
            .orElseThrow(() -> new ResourceNotFoundException("Fichier non trouvé"));

        // Récupérer la stratégie de stockage appropriée
        StorageStrategy storageStrategy = storageStrategyFactory.getStrategy(fileEntity.getStorageType());

        // Utiliser la stratégie de stockage pour récupérer le fichier
        byte[] fileData = storageStrategy.retrieve(fileEntity);

        if (fileData == null || fileData.length == 0) {
            throw new ResourceNotFoundException("Erreur lors du téléchargement du fichier");
        }

        return fileData;
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
