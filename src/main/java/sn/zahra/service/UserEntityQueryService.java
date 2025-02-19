package sn.zahra.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.zahra.domain.*; // for static metamodels
import sn.zahra.domain.UserEntity;
import sn.zahra.repository.UserEntityRepository;
import sn.zahra.service.criteria.UserEntityCriteria;
import sn.zahra.service.dto.UserEntityDTO;
import sn.zahra.service.mapper.UserEntityMapper;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link UserEntity} entities in the database.
 * The main input is a {@link UserEntityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link UserEntityDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserEntityQueryService extends QueryService<UserEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(UserEntityQueryService.class);

    private final UserEntityRepository userEntityRepository;

    private final UserEntityMapper userEntityMapper;

    public UserEntityQueryService(UserEntityRepository userEntityRepository, UserEntityMapper userEntityMapper) {
        this.userEntityRepository = userEntityRepository;
        this.userEntityMapper = userEntityMapper;
    }

    /**
     * Return a {@link Page} of {@link UserEntityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserEntityDTO> findByCriteria(UserEntityCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserEntity> specification = createSpecification(criteria);
        return userEntityRepository.findAll(specification, page).map(userEntityMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserEntityCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<UserEntity> specification = createSpecification(criteria);
        return userEntityRepository.count(specification);
    }

    /**
     * Function to convert {@link UserEntityCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserEntity> createSpecification(UserEntityCriteria criteria) {
        Specification<UserEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UserEntity_.id));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), UserEntity_.createdAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), UserEntity_.updatedAt));
            }
            if (criteria.getDeleted() != null) {
                specification = specification.and(buildSpecification(criteria.getDeleted(), UserEntity_.deleted));
            }
            if (criteria.getDeletedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDeletedAt(), UserEntity_.deletedAt));
            }
            if (criteria.getNom() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNom(), UserEntity_.nom));
            }
            if (criteria.getPrenom() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPrenom(), UserEntity_.prenom));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), UserEntity_.email));
            }
            if (criteria.getPassword() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPassword(), UserEntity_.password));
            }
            if (criteria.getRole() != null) {
                specification = specification.and(buildSpecification(criteria.getRole(), UserEntity_.role));
            }
        }
        return specification;
    }
}
