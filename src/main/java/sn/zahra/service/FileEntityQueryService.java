package sn.zahra.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.zahra.domain.*; // for static metamodels
import sn.zahra.domain.FileEntity;
import sn.zahra.repository.FileEntityRepository;
import sn.zahra.service.criteria.FileEntityCriteria;
import sn.zahra.service.dto.FileEntityDTO;
import sn.zahra.service.mapper.FileEntityMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link FileEntity} entities in the database.
 * The main input is a {@link FileEntityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link FileEntityDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FileEntityQueryService extends QueryService<FileEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(FileEntityQueryService.class);

    private final FileEntityRepository fileEntityRepository;

    private final FileEntityMapper fileEntityMapper;

    public FileEntityQueryService(FileEntityRepository fileEntityRepository, FileEntityMapper fileEntityMapper) {
        this.fileEntityRepository = fileEntityRepository;
        this.fileEntityMapper = fileEntityMapper;
    }

    /**
     * Return a {@link Page} of {@link FileEntityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FileEntityDTO> findByCriteria(FileEntityCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<FileEntity> specification = createSpecification(criteria);
        return fileEntityRepository.findAll(specification, page).map(fileEntityMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FileEntityCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<FileEntity> specification = createSpecification(criteria);
        return fileEntityRepository.count(specification);
    }

    /**
     * Function to convert {@link FileEntityCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FileEntity> createSpecification(FileEntityCriteria criteria) {
        Specification<FileEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), FileEntity_.id));
            }
            if (criteria.getFileName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFileName(), FileEntity_.fileName));
            }
            if (criteria.getOriginalFileName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOriginalFileName(), FileEntity_.originalFileName));
            }
            if (criteria.getContentType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContentType(), FileEntity_.contentType));
            }
            if (criteria.getFileSize() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFileSize(), FileEntity_.fileSize));
            }
            if (criteria.getFilePath() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFilePath(), FileEntity_.filePath));
            }
            if (criteria.getStorageType() != null) {
                specification = specification.and(buildSpecification(criteria.getStorageType(), FileEntity_.storageType));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), FileEntity_.createdAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), FileEntity_.updatedAt));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), FileEntity_.deleted));
            }
            if (criteria.getDeletedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDeletedAt(), FileEntity_.deletedAt));
            }
        }
        return specification;
    }
}
