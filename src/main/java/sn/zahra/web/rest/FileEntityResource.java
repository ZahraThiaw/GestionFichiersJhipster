package sn.zahra.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.zahra.repository.FileEntityRepository;
import sn.zahra.service.FileEntityQueryService;
import sn.zahra.service.FileEntityService;
import sn.zahra.service.criteria.FileEntityCriteria;
import sn.zahra.service.dto.FileEntityDTO;
import sn.zahra.service.dto.FileRequestDTO;
import sn.zahra.service.exception.BadRequestException;
import sn.zahra.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.zahra.domain.FileEntity}.
 */
@RestController
@RequestMapping("/api/file-entities")
public class FileEntityResource {

    private static final Logger LOG = LoggerFactory.getLogger(FileEntityResource.class);

    private static final String ENTITY_NAME = "gestionfichiersjhipsterFileEntity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FileEntityService fileEntityService;

    private final FileEntityRepository fileEntityRepository;

    private final FileEntityQueryService fileEntityQueryService;

    public FileEntityResource(
        FileEntityService fileEntityService,
        FileEntityRepository fileEntityRepository,
        FileEntityQueryService fileEntityQueryService
    ) {
        this.fileEntityService = fileEntityService;
        this.fileEntityRepository = fileEntityRepository;
        this.fileEntityQueryService = fileEntityQueryService;
    }

    /**
     * {@code POST  /file-entities} : Create a new fileEntity.
     *
     * @param fileRequestDTO the fileRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fileEntityDTO, or with status {@code 400 (Bad Request)} if the fileEntity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileEntityDTO> createFileEntity(@ModelAttribute FileRequestDTO fileRequestDTO) throws URISyntaxException {
        LOG.debug("REST request to upload FileEntity : {}", fileRequestDTO);

        if (fileRequestDTO.getFile().isEmpty()) {
            throw new BadRequestAlertException("File must not be empty", ENTITY_NAME, "filenotprovided");
        }

        FileEntityDTO savedFile = fileEntityService.save(fileRequestDTO);

        return ResponseEntity.created(new URI("/api/file-entities/" + savedFile.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, savedFile.getId().toString()))
            .body(savedFile);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Long id) {
        try {
            // Télécharger le fichier via le service
            byte[] fileData = fileEntityService.downloadFile(id);

            // Créer une ressource à partir des données du fichier
            ByteArrayResource resource = new ByteArrayResource(fileData);

            // Renvoyer la ressource avec les bons en-têtes HTTP
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"file_" + id + ".bin\"")
                .body(resource);

        } catch (BadRequestAlertException e) {
            // Retourner une réponse avec une erreur 404 si le fichier n'est pas trouvé
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Fichier non trouvé");
        } catch (BadRequestException e) {
            // Retourner une réponse avec une erreur 500 si un problème de téléchargement survient
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors du téléchargement du fichier");
        }
    }

    /**
     * {@code PUT  /file-entities/:id} : Updates an existing fileEntity.
     *
     * @param id the id of the fileEntityDTO to save.
     * @param fileEntityDTO the fileEntityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileEntityDTO,
     * or with status {@code 400 (Bad Request)} if the fileEntityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fileEntityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FileEntityDTO> updateFileEntity(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FileEntityDTO fileEntityDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FileEntity : {}, {}", id, fileEntityDTO);
        if (fileEntityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fileEntityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fileEntityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        fileEntityDTO = fileEntityService.update(fileEntityDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fileEntityDTO.getId().toString()))
            .body(fileEntityDTO);
    }

    /**
     * {@code PATCH  /file-entities/:id} : Partial updates given fields of an existing fileEntity, field will ignore if it is null
     *
     * @param id the id of the fileEntityDTO to save.
     * @param fileEntityDTO the fileEntityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileEntityDTO,
     * or with status {@code 400 (Bad Request)} if the fileEntityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the fileEntityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the fileEntityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FileEntityDTO> partialUpdateFileEntity(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FileEntityDTO fileEntityDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FileEntity partially : {}, {}", id, fileEntityDTO);
        if (fileEntityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fileEntityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fileEntityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FileEntityDTO> result = fileEntityService.partialUpdate(fileEntityDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, fileEntityDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /file-entities} : get all the fileEntities.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fileEntities in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FileEntityDTO>> getAllFileEntities(
        FileEntityCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get FileEntities by criteria: {}", criteria);

        Page<FileEntityDTO> page = fileEntityQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /file-entities/count} : count all the fileEntities.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countFileEntities(FileEntityCriteria criteria) {
        LOG.debug("REST request to count FileEntities by criteria: {}", criteria);
        return ResponseEntity.ok().body(fileEntityQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /file-entities/:id} : get the "id" fileEntity.
     *
     * @param id the id of the fileEntityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fileEntityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FileEntityDTO> getFileEntity(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FileEntity : {}", id);
        Optional<FileEntityDTO> fileEntityDTO = fileEntityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fileEntityDTO);
    }

    /**
     * {@code DELETE  /file-entities/:id} : delete the "id" fileEntity.
     *
     * @param id the id of the fileEntityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFileEntity(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FileEntity : {}", id);
        fileEntityService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
