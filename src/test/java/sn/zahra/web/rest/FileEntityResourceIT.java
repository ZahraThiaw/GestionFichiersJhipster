package sn.zahra.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.zahra.domain.FileEntityAsserts.*;
import static sn.zahra.web.rest.TestUtil.createUpdateProxyForBean;
import static sn.zahra.web.rest.TestUtil.sameInstant;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.zahra.IntegrationTest;
import sn.zahra.domain.FileEntity;
import sn.zahra.domain.enumeration.StorageType;
import sn.zahra.repository.FileEntityRepository;
import sn.zahra.service.dto.FileEntityDTO;
import sn.zahra.service.mapper.FileEntityMapper;

/**
 * Integration tests for the {@link FileEntityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FileEntityResourceIT {

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ORIGINAL_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_FILE_SIZE = 1L;
    private static final Long UPDATED_FILE_SIZE = 2L;
    private static final Long SMALLER_FILE_SIZE = 1L - 1L;

    private static final String DEFAULT_FILE_PATH = "AAAAAAAAAA";
    private static final String UPDATED_FILE_PATH = "BBBBBBBBBB";

    private static final StorageType DEFAULT_STORAGE_TYPE = StorageType.LOCAL;
    private static final StorageType UPDATED_STORAGE_TYPE = StorageType.DATABASE;

    private static final byte[] DEFAULT_FILE_DATA = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE_DATA = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FILE_DATA_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_DATA_CONTENT_TYPE = "image/png";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final ZonedDateTime DEFAULT_DELETED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DELETED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DELETED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/file-entities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FileEntityRepository fileEntityRepository;

    @Autowired
    private FileEntityMapper fileEntityMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFileEntityMockMvc;

    private FileEntity fileEntity;

    private FileEntity insertedFileEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileEntity createEntity() {
        return new FileEntity()
            .fileName(DEFAULT_FILE_NAME)
            .originalFileName(DEFAULT_ORIGINAL_FILE_NAME)
            .contentType(DEFAULT_CONTENT_TYPE)
            .fileSize(DEFAULT_FILE_SIZE)
            .filePath(DEFAULT_FILE_PATH)
            .storageType(DEFAULT_STORAGE_TYPE)
            .fileData(DEFAULT_FILE_DATA)
            .fileDataContentType(DEFAULT_FILE_DATA_CONTENT_TYPE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .deleted(DEFAULT_DELETED)
            .deletedAt(DEFAULT_DELETED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FileEntity createUpdatedEntity() {
        return new FileEntity()
            .fileName(UPDATED_FILE_NAME)
            .originalFileName(UPDATED_ORIGINAL_FILE_NAME)
            .contentType(UPDATED_CONTENT_TYPE)
            .fileSize(UPDATED_FILE_SIZE)
            .filePath(UPDATED_FILE_PATH)
            .storageType(UPDATED_STORAGE_TYPE)
            .fileData(UPDATED_FILE_DATA)
            .fileDataContentType(UPDATED_FILE_DATA_CONTENT_TYPE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .deleted(UPDATED_DELETED)
            .deletedAt(UPDATED_DELETED_AT);
    }

    @BeforeEach
    public void initTest() {
        fileEntity = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedFileEntity != null) {
            fileEntityRepository.delete(insertedFileEntity);
            insertedFileEntity = null;
        }
    }

    @Test
    @Transactional
    void createFileEntity() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FileEntity
        FileEntityDTO fileEntityDTO = fileEntityMapper.toDto(fileEntity);
        var returnedFileEntityDTO = om.readValue(
            restFileEntityMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fileEntityDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FileEntityDTO.class
        );

        // Validate the FileEntity in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFileEntity = fileEntityMapper.toEntity(returnedFileEntityDTO);
        assertFileEntityUpdatableFieldsEquals(returnedFileEntity, getPersistedFileEntity(returnedFileEntity));

        insertedFileEntity = returnedFileEntity;
    }

    @Test
    @Transactional
    void createFileEntityWithExistingId() throws Exception {
        // Create the FileEntity with an existing ID
        fileEntity.setId(1L);
        FileEntityDTO fileEntityDTO = fileEntityMapper.toDto(fileEntity);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFileEntityMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fileEntityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FileEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFileEntities() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList
        restFileEntityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fileEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].originalFileName").value(hasItem(DEFAULT_ORIGINAL_FILE_NAME)))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fileSize").value(hasItem(DEFAULT_FILE_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].filePath").value(hasItem(DEFAULT_FILE_PATH)))
            .andExpect(jsonPath("$.[*].storageType").value(hasItem(DEFAULT_STORAGE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fileDataContentType").value(hasItem(DEFAULT_FILE_DATA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fileData").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_FILE_DATA))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(sameInstant(DEFAULT_DELETED_AT))));
    }

    @Test
    @Transactional
    void getFileEntity() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get the fileEntity
        restFileEntityMockMvc
            .perform(get(ENTITY_API_URL_ID, fileEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fileEntity.getId().intValue()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME))
            .andExpect(jsonPath("$.originalFileName").value(DEFAULT_ORIGINAL_FILE_NAME))
            .andExpect(jsonPath("$.contentType").value(DEFAULT_CONTENT_TYPE))
            .andExpect(jsonPath("$.fileSize").value(DEFAULT_FILE_SIZE.intValue()))
            .andExpect(jsonPath("$.filePath").value(DEFAULT_FILE_PATH))
            .andExpect(jsonPath("$.storageType").value(DEFAULT_STORAGE_TYPE.toString()))
            .andExpect(jsonPath("$.fileDataContentType").value(DEFAULT_FILE_DATA_CONTENT_TYPE))
            .andExpect(jsonPath("$.fileData").value(Base64.getEncoder().encodeToString(DEFAULT_FILE_DATA)))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(sameInstant(DEFAULT_DELETED_AT)));
    }

    @Test
    @Transactional
    void getFileEntitiesByIdFiltering() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        Long id = fileEntity.getId();

        defaultFileEntityFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultFileEntityFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultFileEntityFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where fileName equals to
        defaultFileEntityFiltering("fileName.equals=" + DEFAULT_FILE_NAME, "fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where fileName in
        defaultFileEntityFiltering("fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME, "fileName.in=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where fileName is not null
        defaultFileEntityFiltering("fileName.specified=true", "fileName.specified=false");
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFileNameContainsSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where fileName contains
        defaultFileEntityFiltering("fileName.contains=" + DEFAULT_FILE_NAME, "fileName.contains=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where fileName does not contain
        defaultFileEntityFiltering("fileName.doesNotContain=" + UPDATED_FILE_NAME, "fileName.doesNotContain=" + DEFAULT_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByOriginalFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where originalFileName equals to
        defaultFileEntityFiltering(
            "originalFileName.equals=" + DEFAULT_ORIGINAL_FILE_NAME,
            "originalFileName.equals=" + UPDATED_ORIGINAL_FILE_NAME
        );
    }

    @Test
    @Transactional
    void getAllFileEntitiesByOriginalFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where originalFileName in
        defaultFileEntityFiltering(
            "originalFileName.in=" + DEFAULT_ORIGINAL_FILE_NAME + "," + UPDATED_ORIGINAL_FILE_NAME,
            "originalFileName.in=" + UPDATED_ORIGINAL_FILE_NAME
        );
    }

    @Test
    @Transactional
    void getAllFileEntitiesByOriginalFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where originalFileName is not null
        defaultFileEntityFiltering("originalFileName.specified=true", "originalFileName.specified=false");
    }

    @Test
    @Transactional
    void getAllFileEntitiesByOriginalFileNameContainsSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where originalFileName contains
        defaultFileEntityFiltering(
            "originalFileName.contains=" + DEFAULT_ORIGINAL_FILE_NAME,
            "originalFileName.contains=" + UPDATED_ORIGINAL_FILE_NAME
        );
    }

    @Test
    @Transactional
    void getAllFileEntitiesByOriginalFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where originalFileName does not contain
        defaultFileEntityFiltering(
            "originalFileName.doesNotContain=" + UPDATED_ORIGINAL_FILE_NAME,
            "originalFileName.doesNotContain=" + DEFAULT_ORIGINAL_FILE_NAME
        );
    }

    @Test
    @Transactional
    void getAllFileEntitiesByContentTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where contentType equals to
        defaultFileEntityFiltering("contentType.equals=" + DEFAULT_CONTENT_TYPE, "contentType.equals=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByContentTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where contentType in
        defaultFileEntityFiltering(
            "contentType.in=" + DEFAULT_CONTENT_TYPE + "," + UPDATED_CONTENT_TYPE,
            "contentType.in=" + UPDATED_CONTENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllFileEntitiesByContentTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where contentType is not null
        defaultFileEntityFiltering("contentType.specified=true", "contentType.specified=false");
    }

    @Test
    @Transactional
    void getAllFileEntitiesByContentTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where contentType contains
        defaultFileEntityFiltering("contentType.contains=" + DEFAULT_CONTENT_TYPE, "contentType.contains=" + UPDATED_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByContentTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where contentType does not contain
        defaultFileEntityFiltering(
            "contentType.doesNotContain=" + UPDATED_CONTENT_TYPE,
            "contentType.doesNotContain=" + DEFAULT_CONTENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFileSizeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where fileSize equals to
        defaultFileEntityFiltering("fileSize.equals=" + DEFAULT_FILE_SIZE, "fileSize.equals=" + UPDATED_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFileSizeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where fileSize in
        defaultFileEntityFiltering("fileSize.in=" + DEFAULT_FILE_SIZE + "," + UPDATED_FILE_SIZE, "fileSize.in=" + UPDATED_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFileSizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where fileSize is not null
        defaultFileEntityFiltering("fileSize.specified=true", "fileSize.specified=false");
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFileSizeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where fileSize is greater than or equal to
        defaultFileEntityFiltering("fileSize.greaterThanOrEqual=" + DEFAULT_FILE_SIZE, "fileSize.greaterThanOrEqual=" + UPDATED_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFileSizeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where fileSize is less than or equal to
        defaultFileEntityFiltering("fileSize.lessThanOrEqual=" + DEFAULT_FILE_SIZE, "fileSize.lessThanOrEqual=" + SMALLER_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFileSizeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where fileSize is less than
        defaultFileEntityFiltering("fileSize.lessThan=" + UPDATED_FILE_SIZE, "fileSize.lessThan=" + DEFAULT_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFileSizeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where fileSize is greater than
        defaultFileEntityFiltering("fileSize.greaterThan=" + SMALLER_FILE_SIZE, "fileSize.greaterThan=" + DEFAULT_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFilePathIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where filePath equals to
        defaultFileEntityFiltering("filePath.equals=" + DEFAULT_FILE_PATH, "filePath.equals=" + UPDATED_FILE_PATH);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFilePathIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where filePath in
        defaultFileEntityFiltering("filePath.in=" + DEFAULT_FILE_PATH + "," + UPDATED_FILE_PATH, "filePath.in=" + UPDATED_FILE_PATH);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFilePathIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where filePath is not null
        defaultFileEntityFiltering("filePath.specified=true", "filePath.specified=false");
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFilePathContainsSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where filePath contains
        defaultFileEntityFiltering("filePath.contains=" + DEFAULT_FILE_PATH, "filePath.contains=" + UPDATED_FILE_PATH);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByFilePathNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where filePath does not contain
        defaultFileEntityFiltering("filePath.doesNotContain=" + UPDATED_FILE_PATH, "filePath.doesNotContain=" + DEFAULT_FILE_PATH);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByStorageTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where storageType equals to
        defaultFileEntityFiltering("storageType.equals=" + DEFAULT_STORAGE_TYPE, "storageType.equals=" + UPDATED_STORAGE_TYPE);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByStorageTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where storageType in
        defaultFileEntityFiltering(
            "storageType.in=" + DEFAULT_STORAGE_TYPE + "," + UPDATED_STORAGE_TYPE,
            "storageType.in=" + UPDATED_STORAGE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllFileEntitiesByStorageTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where storageType is not null
        defaultFileEntityFiltering("storageType.specified=true", "storageType.specified=false");
    }

    @Test
    @Transactional
    void getAllFileEntitiesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where createdAt equals to
        defaultFileEntityFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where createdAt in
        defaultFileEntityFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where createdAt is not null
        defaultFileEntityFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllFileEntitiesByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where createdAt is greater than or equal to
        defaultFileEntityFiltering(
            "createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT,
            "createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllFileEntitiesByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where createdAt is less than or equal to
        defaultFileEntityFiltering("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT, "createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where createdAt is less than
        defaultFileEntityFiltering("createdAt.lessThan=" + UPDATED_CREATED_AT, "createdAt.lessThan=" + DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where createdAt is greater than
        defaultFileEntityFiltering("createdAt.greaterThan=" + SMALLER_CREATED_AT, "createdAt.greaterThan=" + DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where updatedAt equals to
        defaultFileEntityFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where updatedAt in
        defaultFileEntityFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where updatedAt is not null
        defaultFileEntityFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllFileEntitiesByUpdatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where updatedAt is greater than or equal to
        defaultFileEntityFiltering(
            "updatedAt.greaterThanOrEqual=" + DEFAULT_UPDATED_AT,
            "updatedAt.greaterThanOrEqual=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllFileEntitiesByUpdatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where updatedAt is less than or equal to
        defaultFileEntityFiltering("updatedAt.lessThanOrEqual=" + DEFAULT_UPDATED_AT, "updatedAt.lessThanOrEqual=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByUpdatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where updatedAt is less than
        defaultFileEntityFiltering("updatedAt.lessThan=" + UPDATED_UPDATED_AT, "updatedAt.lessThan=" + DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByUpdatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where updatedAt is greater than
        defaultFileEntityFiltering("updatedAt.greaterThan=" + SMALLER_UPDATED_AT, "updatedAt.greaterThan=" + DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where deleted equals to
        defaultFileEntityFiltering("deleted.equals=" + DEFAULT_DELETED, "deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where deleted in
        defaultFileEntityFiltering("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED, "deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where deleted is not null
        defaultFileEntityFiltering("deleted.specified=true", "deleted.specified=false");
    }

    @Test
    @Transactional
    void getAllFileEntitiesByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where deletedAt equals to
        defaultFileEntityFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where deletedAt in
        defaultFileEntityFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where deletedAt is not null
        defaultFileEntityFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllFileEntitiesByDeletedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where deletedAt is greater than or equal to
        defaultFileEntityFiltering(
            "deletedAt.greaterThanOrEqual=" + DEFAULT_DELETED_AT,
            "deletedAt.greaterThanOrEqual=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllFileEntitiesByDeletedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where deletedAt is less than or equal to
        defaultFileEntityFiltering("deletedAt.lessThanOrEqual=" + DEFAULT_DELETED_AT, "deletedAt.lessThanOrEqual=" + SMALLER_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByDeletedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where deletedAt is less than
        defaultFileEntityFiltering("deletedAt.lessThan=" + UPDATED_DELETED_AT, "deletedAt.lessThan=" + DEFAULT_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllFileEntitiesByDeletedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        // Get all the fileEntityList where deletedAt is greater than
        defaultFileEntityFiltering("deletedAt.greaterThan=" + SMALLER_DELETED_AT, "deletedAt.greaterThan=" + DEFAULT_DELETED_AT);
    }

    private void defaultFileEntityFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultFileEntityShouldBeFound(shouldBeFound);
        defaultFileEntityShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFileEntityShouldBeFound(String filter) throws Exception {
        restFileEntityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fileEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].originalFileName").value(hasItem(DEFAULT_ORIGINAL_FILE_NAME)))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fileSize").value(hasItem(DEFAULT_FILE_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].filePath").value(hasItem(DEFAULT_FILE_PATH)))
            .andExpect(jsonPath("$.[*].storageType").value(hasItem(DEFAULT_STORAGE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fileDataContentType").value(hasItem(DEFAULT_FILE_DATA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fileData").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_FILE_DATA))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(sameInstant(DEFAULT_DELETED_AT))));

        // Check, that the count call also returns 1
        restFileEntityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFileEntityShouldNotBeFound(String filter) throws Exception {
        restFileEntityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFileEntityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFileEntity() throws Exception {
        // Get the fileEntity
        restFileEntityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFileEntity() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileEntity
        FileEntity updatedFileEntity = fileEntityRepository.findById(fileEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFileEntity are not directly saved in db
        em.detach(updatedFileEntity);
        updatedFileEntity
            .fileName(UPDATED_FILE_NAME)
            .originalFileName(UPDATED_ORIGINAL_FILE_NAME)
            .contentType(UPDATED_CONTENT_TYPE)
            .fileSize(UPDATED_FILE_SIZE)
            .filePath(UPDATED_FILE_PATH)
            .storageType(UPDATED_STORAGE_TYPE)
            .fileData(UPDATED_FILE_DATA)
            .fileDataContentType(UPDATED_FILE_DATA_CONTENT_TYPE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .deleted(UPDATED_DELETED)
            .deletedAt(UPDATED_DELETED_AT);
        FileEntityDTO fileEntityDTO = fileEntityMapper.toDto(updatedFileEntity);

        restFileEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fileEntityDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fileEntityDTO))
            )
            .andExpect(status().isOk());

        // Validate the FileEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFileEntityToMatchAllProperties(updatedFileEntity);
    }

    @Test
    @Transactional
    void putNonExistingFileEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileEntity.setId(longCount.incrementAndGet());

        // Create the FileEntity
        FileEntityDTO fileEntityDTO = fileEntityMapper.toDto(fileEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFileEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fileEntityDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fileEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FileEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFileEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileEntity.setId(longCount.incrementAndGet());

        // Create the FileEntity
        FileEntityDTO fileEntityDTO = fileEntityMapper.toDto(fileEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fileEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FileEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFileEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileEntity.setId(longCount.incrementAndGet());

        // Create the FileEntity
        FileEntityDTO fileEntityDTO = fileEntityMapper.toDto(fileEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileEntityMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fileEntityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FileEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFileEntityWithPatch() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileEntity using partial update
        FileEntity partialUpdatedFileEntity = new FileEntity();
        partialUpdatedFileEntity.setId(fileEntity.getId());

        partialUpdatedFileEntity
            .fileName(UPDATED_FILE_NAME)
            .originalFileName(UPDATED_ORIGINAL_FILE_NAME)
            .fileData(UPDATED_FILE_DATA)
            .fileDataContentType(UPDATED_FILE_DATA_CONTENT_TYPE)
            .updatedAt(UPDATED_UPDATED_AT)
            .deleted(UPDATED_DELETED)
            .deletedAt(UPDATED_DELETED_AT);

        restFileEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFileEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFileEntity))
            )
            .andExpect(status().isOk());

        // Validate the FileEntity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFileEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFileEntity, fileEntity),
            getPersistedFileEntity(fileEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateFileEntityWithPatch() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fileEntity using partial update
        FileEntity partialUpdatedFileEntity = new FileEntity();
        partialUpdatedFileEntity.setId(fileEntity.getId());

        partialUpdatedFileEntity
            .fileName(UPDATED_FILE_NAME)
            .originalFileName(UPDATED_ORIGINAL_FILE_NAME)
            .contentType(UPDATED_CONTENT_TYPE)
            .fileSize(UPDATED_FILE_SIZE)
            .filePath(UPDATED_FILE_PATH)
            .storageType(UPDATED_STORAGE_TYPE)
            .fileData(UPDATED_FILE_DATA)
            .fileDataContentType(UPDATED_FILE_DATA_CONTENT_TYPE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .deleted(UPDATED_DELETED)
            .deletedAt(UPDATED_DELETED_AT);

        restFileEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFileEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFileEntity))
            )
            .andExpect(status().isOk());

        // Validate the FileEntity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFileEntityUpdatableFieldsEquals(partialUpdatedFileEntity, getPersistedFileEntity(partialUpdatedFileEntity));
    }

    @Test
    @Transactional
    void patchNonExistingFileEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileEntity.setId(longCount.incrementAndGet());

        // Create the FileEntity
        FileEntityDTO fileEntityDTO = fileEntityMapper.toDto(fileEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFileEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fileEntityDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(fileEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FileEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFileEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileEntity.setId(longCount.incrementAndGet());

        // Create the FileEntity
        FileEntityDTO fileEntityDTO = fileEntityMapper.toDto(fileEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(fileEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FileEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFileEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fileEntity.setId(longCount.incrementAndGet());

        // Create the FileEntity
        FileEntityDTO fileEntityDTO = fileEntityMapper.toDto(fileEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFileEntityMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(fileEntityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FileEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFileEntity() throws Exception {
        // Initialize the database
        insertedFileEntity = fileEntityRepository.saveAndFlush(fileEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the fileEntity
        restFileEntityMockMvc
            .perform(delete(ENTITY_API_URL_ID, fileEntity.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return fileEntityRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected FileEntity getPersistedFileEntity(FileEntity fileEntity) {
        return fileEntityRepository.findById(fileEntity.getId()).orElseThrow();
    }

    protected void assertPersistedFileEntityToMatchAllProperties(FileEntity expectedFileEntity) {
        assertFileEntityAllPropertiesEquals(expectedFileEntity, getPersistedFileEntity(expectedFileEntity));
    }

    protected void assertPersistedFileEntityToMatchUpdatableProperties(FileEntity expectedFileEntity) {
        assertFileEntityAllUpdatablePropertiesEquals(expectedFileEntity, getPersistedFileEntity(expectedFileEntity));
    }
}
