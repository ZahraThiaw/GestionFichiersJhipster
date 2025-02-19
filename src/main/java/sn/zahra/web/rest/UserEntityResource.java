package sn.zahra.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.zahra.repository.UserEntityRepository;
import sn.zahra.service.UserEntityQueryService;
import sn.zahra.service.UserEntityService;
import sn.zahra.service.criteria.UserEntityCriteria;
import sn.zahra.service.dto.UserEntityDTO;
import sn.zahra.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.zahra.domain.UserEntity}.
 */
@RestController
@RequestMapping("/api/user-entities")
public class UserEntityResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserEntityResource.class);

    private static final String ENTITY_NAME = "gestionfichiersjhipsterUserEntity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserEntityService userEntityService;

    private final UserEntityRepository userEntityRepository;

    private final UserEntityQueryService userEntityQueryService;

    public UserEntityResource(
        UserEntityService userEntityService,
        UserEntityRepository userEntityRepository,
        UserEntityQueryService userEntityQueryService
    ) {
        this.userEntityService = userEntityService;
        this.userEntityRepository = userEntityRepository;
        this.userEntityQueryService = userEntityQueryService;
    }

    /**
     * {@code POST  /user-entities} : Create a new userEntity.
     *
     * @param userEntityDTO the userEntityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userEntityDTO, or with status {@code 400 (Bad Request)} if the userEntity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserEntityDTO> createUserEntity(@Valid @RequestBody UserEntityDTO userEntityDTO) throws URISyntaxException {
        LOG.debug("REST request to save UserEntity : {}", userEntityDTO);
        if (userEntityDTO.getId() != null) {
            throw new BadRequestAlertException("A new userEntity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userEntityDTO = userEntityService.save(userEntityDTO);
        return ResponseEntity.created(new URI("/api/user-entities/" + userEntityDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, userEntityDTO.getId().toString()))
            .body(userEntityDTO);
    }

    /**
     * {@code PUT  /user-entities/:id} : Updates an existing userEntity.
     *
     * @param id the id of the userEntityDTO to save.
     * @param userEntityDTO the userEntityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userEntityDTO,
     * or with status {@code 400 (Bad Request)} if the userEntityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userEntityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserEntityDTO> updateUserEntity(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserEntityDTO userEntityDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserEntity : {}, {}", id, userEntityDTO);
        if (userEntityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userEntityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userEntityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userEntityDTO = userEntityService.update(userEntityDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userEntityDTO.getId().toString()))
            .body(userEntityDTO);
    }

    /**
     * {@code PATCH  /user-entities/:id} : Partial updates given fields of an existing userEntity, field will ignore if it is null
     *
     * @param id the id of the userEntityDTO to save.
     * @param userEntityDTO the userEntityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userEntityDTO,
     * or with status {@code 400 (Bad Request)} if the userEntityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userEntityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userEntityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserEntityDTO> partialUpdateUserEntity(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserEntityDTO userEntityDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserEntity partially : {}, {}", id, userEntityDTO);
        if (userEntityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userEntityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userEntityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserEntityDTO> result = userEntityService.partialUpdate(userEntityDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, userEntityDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-entities} : get all the userEntities.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userEntities in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UserEntityDTO>> getAllUserEntities(
        UserEntityCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get UserEntities by criteria: {}", criteria);

        Page<UserEntityDTO> page = userEntityQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-entities/count} : count all the userEntities.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countUserEntities(UserEntityCriteria criteria) {
        LOG.debug("REST request to count UserEntities by criteria: {}", criteria);
        return ResponseEntity.ok().body(userEntityQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-entities/:id} : get the "id" userEntity.
     *
     * @param id the id of the userEntityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userEntityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserEntityDTO> getUserEntity(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserEntity : {}", id);
        Optional<UserEntityDTO> userEntityDTO = userEntityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userEntityDTO);
    }

    /**
     * {@code DELETE  /user-entities/:id} : delete the "id" userEntity.
     *
     * @param id the id of the userEntityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserEntity(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserEntity : {}", id);
        userEntityService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
