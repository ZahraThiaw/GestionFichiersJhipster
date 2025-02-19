package sn.zahra.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.zahra.domain.UserEntityAsserts.*;
import static sn.zahra.web.rest.TestUtil.createUpdateProxyForBean;
import static sn.zahra.web.rest.TestUtil.sameInstant;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
import sn.zahra.domain.UserEntity;
import sn.zahra.domain.enumeration.Role;
import sn.zahra.repository.UserEntityRepository;
import sn.zahra.service.dto.UserEntityDTO;
import sn.zahra.service.mapper.UserEntityMapper;

/**
 * Integration tests for the {@link UserEntityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserEntityResourceIT {

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

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final Role DEFAULT_ROLE = Role.ADMIN;
    private static final Role UPDATED_ROLE = Role.USER;

    private static final String ENTITY_API_URL = "/api/user-entities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private UserEntityMapper userEntityMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserEntityMockMvc;

    private UserEntity userEntity;

    private UserEntity insertedUserEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserEntity createEntity() {
        return new UserEntity()
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .deleted(DEFAULT_DELETED)
            .deletedAt(DEFAULT_DELETED_AT)
            .nom(DEFAULT_NOM)
            .prenom(DEFAULT_PRENOM)
            .email(DEFAULT_EMAIL)
            .password(DEFAULT_PASSWORD)
            .role(DEFAULT_ROLE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserEntity createUpdatedEntity() {
        return new UserEntity()
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .deleted(UPDATED_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .role(UPDATED_ROLE);
    }

    @BeforeEach
    public void initTest() {
        userEntity = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedUserEntity != null) {
            userEntityRepository.delete(insertedUserEntity);
            insertedUserEntity = null;
        }
    }

    @Test
    @Transactional
    void createUserEntity() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserEntity
        UserEntityDTO userEntityDTO = userEntityMapper.toDto(userEntity);
        var returnedUserEntityDTO = om.readValue(
            restUserEntityMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userEntityDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserEntityDTO.class
        );

        // Validate the UserEntity in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserEntity = userEntityMapper.toEntity(returnedUserEntityDTO);
        assertUserEntityUpdatableFieldsEquals(returnedUserEntity, getPersistedUserEntity(returnedUserEntity));

        insertedUserEntity = returnedUserEntity;
    }

    @Test
    @Transactional
    void createUserEntityWithExistingId() throws Exception {
        // Create the UserEntity with an existing ID
        userEntity.setId(1L);
        UserEntityDTO userEntityDTO = userEntityMapper.toDto(userEntity);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserEntityMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userEntityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPasswordIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userEntity.setPassword(null);

        // Create the UserEntity, which fails.
        UserEntityDTO userEntityDTO = userEntityMapper.toDto(userEntity);

        restUserEntityMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userEntityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserEntities() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList
        restUserEntityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(sameInstant(DEFAULT_DELETED_AT))))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));
    }

    @Test
    @Transactional
    void getUserEntity() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get the userEntity
        restUserEntityMockMvc
            .perform(get(ENTITY_API_URL_ID, userEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userEntity.getId().intValue()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED))
            .andExpect(jsonPath("$.deletedAt").value(sameInstant(DEFAULT_DELETED_AT)))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()));
    }

    @Test
    @Transactional
    void getUserEntitiesByIdFiltering() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        Long id = userEntity.getId();

        defaultUserEntityFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultUserEntityFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultUserEntityFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where createdAt equals to
        defaultUserEntityFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where createdAt in
        defaultUserEntityFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where createdAt is not null
        defaultUserEntityFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserEntitiesByCreatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where createdAt is greater than or equal to
        defaultUserEntityFiltering(
            "createdAt.greaterThanOrEqual=" + DEFAULT_CREATED_AT,
            "createdAt.greaterThanOrEqual=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllUserEntitiesByCreatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where createdAt is less than or equal to
        defaultUserEntityFiltering("createdAt.lessThanOrEqual=" + DEFAULT_CREATED_AT, "createdAt.lessThanOrEqual=" + SMALLER_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByCreatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where createdAt is less than
        defaultUserEntityFiltering("createdAt.lessThan=" + UPDATED_CREATED_AT, "createdAt.lessThan=" + DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByCreatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where createdAt is greater than
        defaultUserEntityFiltering("createdAt.greaterThan=" + SMALLER_CREATED_AT, "createdAt.greaterThan=" + DEFAULT_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where updatedAt equals to
        defaultUserEntityFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where updatedAt in
        defaultUserEntityFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where updatedAt is not null
        defaultUserEntityFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserEntitiesByUpdatedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where updatedAt is greater than or equal to
        defaultUserEntityFiltering(
            "updatedAt.greaterThanOrEqual=" + DEFAULT_UPDATED_AT,
            "updatedAt.greaterThanOrEqual=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllUserEntitiesByUpdatedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where updatedAt is less than or equal to
        defaultUserEntityFiltering("updatedAt.lessThanOrEqual=" + DEFAULT_UPDATED_AT, "updatedAt.lessThanOrEqual=" + SMALLER_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByUpdatedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where updatedAt is less than
        defaultUserEntityFiltering("updatedAt.lessThan=" + UPDATED_UPDATED_AT, "updatedAt.lessThan=" + DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByUpdatedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where updatedAt is greater than
        defaultUserEntityFiltering("updatedAt.greaterThan=" + SMALLER_UPDATED_AT, "updatedAt.greaterThan=" + DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where deleted equals to
        defaultUserEntityFiltering("deleted.equals=" + DEFAULT_DELETED, "deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where deleted in
        defaultUserEntityFiltering("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED, "deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where deleted is not null
        defaultUserEntityFiltering("deleted.specified=true", "deleted.specified=false");
    }

    @Test
    @Transactional
    void getAllUserEntitiesByDeletedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where deletedAt equals to
        defaultUserEntityFiltering("deletedAt.equals=" + DEFAULT_DELETED_AT, "deletedAt.equals=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByDeletedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where deletedAt in
        defaultUserEntityFiltering("deletedAt.in=" + DEFAULT_DELETED_AT + "," + UPDATED_DELETED_AT, "deletedAt.in=" + UPDATED_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByDeletedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where deletedAt is not null
        defaultUserEntityFiltering("deletedAt.specified=true", "deletedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllUserEntitiesByDeletedAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where deletedAt is greater than or equal to
        defaultUserEntityFiltering(
            "deletedAt.greaterThanOrEqual=" + DEFAULT_DELETED_AT,
            "deletedAt.greaterThanOrEqual=" + UPDATED_DELETED_AT
        );
    }

    @Test
    @Transactional
    void getAllUserEntitiesByDeletedAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where deletedAt is less than or equal to
        defaultUserEntityFiltering("deletedAt.lessThanOrEqual=" + DEFAULT_DELETED_AT, "deletedAt.lessThanOrEqual=" + SMALLER_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByDeletedAtIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where deletedAt is less than
        defaultUserEntityFiltering("deletedAt.lessThan=" + UPDATED_DELETED_AT, "deletedAt.lessThan=" + DEFAULT_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByDeletedAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where deletedAt is greater than
        defaultUserEntityFiltering("deletedAt.greaterThan=" + SMALLER_DELETED_AT, "deletedAt.greaterThan=" + DEFAULT_DELETED_AT);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where nom equals to
        defaultUserEntityFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByNomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where nom in
        defaultUserEntityFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where nom is not null
        defaultUserEntityFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    @Transactional
    void getAllUserEntitiesByNomContainsSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where nom contains
        defaultUserEntityFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByNomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where nom does not contain
        defaultUserEntityFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByPrenomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where prenom equals to
        defaultUserEntityFiltering("prenom.equals=" + DEFAULT_PRENOM, "prenom.equals=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByPrenomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where prenom in
        defaultUserEntityFiltering("prenom.in=" + DEFAULT_PRENOM + "," + UPDATED_PRENOM, "prenom.in=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByPrenomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where prenom is not null
        defaultUserEntityFiltering("prenom.specified=true", "prenom.specified=false");
    }

    @Test
    @Transactional
    void getAllUserEntitiesByPrenomContainsSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where prenom contains
        defaultUserEntityFiltering("prenom.contains=" + DEFAULT_PRENOM, "prenom.contains=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByPrenomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where prenom does not contain
        defaultUserEntityFiltering("prenom.doesNotContain=" + UPDATED_PRENOM, "prenom.doesNotContain=" + DEFAULT_PRENOM);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where email equals to
        defaultUserEntityFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where email in
        defaultUserEntityFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where email is not null
        defaultUserEntityFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllUserEntitiesByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where email contains
        defaultUserEntityFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where email does not contain
        defaultUserEntityFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByPasswordIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where password equals to
        defaultUserEntityFiltering("password.equals=" + DEFAULT_PASSWORD, "password.equals=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByPasswordIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where password in
        defaultUserEntityFiltering("password.in=" + DEFAULT_PASSWORD + "," + UPDATED_PASSWORD, "password.in=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByPasswordIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where password is not null
        defaultUserEntityFiltering("password.specified=true", "password.specified=false");
    }

    @Test
    @Transactional
    void getAllUserEntitiesByPasswordContainsSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where password contains
        defaultUserEntityFiltering("password.contains=" + DEFAULT_PASSWORD, "password.contains=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByPasswordNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where password does not contain
        defaultUserEntityFiltering("password.doesNotContain=" + UPDATED_PASSWORD, "password.doesNotContain=" + DEFAULT_PASSWORD);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where role equals to
        defaultUserEntityFiltering("role.equals=" + DEFAULT_ROLE, "role.equals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where role in
        defaultUserEntityFiltering("role.in=" + DEFAULT_ROLE + "," + UPDATED_ROLE, "role.in=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    void getAllUserEntitiesByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        // Get all the userEntityList where role is not null
        defaultUserEntityFiltering("role.specified=true", "role.specified=false");
    }

    private void defaultUserEntityFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultUserEntityShouldBeFound(shouldBeFound);
        defaultUserEntityShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserEntityShouldBeFound(String filter) throws Exception {
        restUserEntityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)))
            .andExpect(jsonPath("$.[*].deletedAt").value(hasItem(sameInstant(DEFAULT_DELETED_AT))))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())));

        // Check, that the count call also returns 1
        restUserEntityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserEntityShouldNotBeFound(String filter) throws Exception {
        restUserEntityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserEntityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUserEntity() throws Exception {
        // Get the userEntity
        restUserEntityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserEntity() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userEntity
        UserEntity updatedUserEntity = userEntityRepository.findById(userEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserEntity are not directly saved in db
        em.detach(updatedUserEntity);
        updatedUserEntity
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .deleted(UPDATED_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .role(UPDATED_ROLE);
        UserEntityDTO userEntityDTO = userEntityMapper.toDto(updatedUserEntity);

        restUserEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userEntityDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userEntityDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserEntityToMatchAllProperties(updatedUserEntity);
    }

    @Test
    @Transactional
    void putNonExistingUserEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userEntity.setId(longCount.incrementAndGet());

        // Create the UserEntity
        UserEntityDTO userEntityDTO = userEntityMapper.toDto(userEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userEntityDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userEntity.setId(longCount.incrementAndGet());

        // Create the UserEntity
        UserEntityDTO userEntityDTO = userEntityMapper.toDto(userEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userEntity.setId(longCount.incrementAndGet());

        // Create the UserEntity
        UserEntityDTO userEntityDTO = userEntityMapper.toDto(userEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserEntityMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userEntityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserEntityWithPatch() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userEntity using partial update
        UserEntity partialUpdatedUserEntity = new UserEntity();
        partialUpdatedUserEntity.setId(userEntity.getId());

        partialUpdatedUserEntity
            .updatedAt(UPDATED_UPDATED_AT)
            .deleted(UPDATED_DELETED)
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .role(UPDATED_ROLE);

        restUserEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserEntity))
            )
            .andExpect(status().isOk());

        // Validate the UserEntity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserEntity, userEntity),
            getPersistedUserEntity(userEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserEntityWithPatch() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userEntity using partial update
        UserEntity partialUpdatedUserEntity = new UserEntity();
        partialUpdatedUserEntity.setId(userEntity.getId());

        partialUpdatedUserEntity
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .deleted(UPDATED_DELETED)
            .deletedAt(UPDATED_DELETED_AT)
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .role(UPDATED_ROLE);

        restUserEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserEntity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserEntity))
            )
            .andExpect(status().isOk());

        // Validate the UserEntity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserEntityUpdatableFieldsEquals(partialUpdatedUserEntity, getPersistedUserEntity(partialUpdatedUserEntity));
    }

    @Test
    @Transactional
    void patchNonExistingUserEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userEntity.setId(longCount.incrementAndGet());

        // Create the UserEntity
        UserEntityDTO userEntityDTO = userEntityMapper.toDto(userEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userEntityDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userEntity.setId(longCount.incrementAndGet());

        // Create the UserEntity
        UserEntityDTO userEntityDTO = userEntityMapper.toDto(userEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userEntity.setId(longCount.incrementAndGet());

        // Create the UserEntity
        UserEntityDTO userEntityDTO = userEntityMapper.toDto(userEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserEntityMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userEntityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserEntity() throws Exception {
        // Initialize the database
        insertedUserEntity = userEntityRepository.saveAndFlush(userEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userEntity
        restUserEntityMockMvc
            .perform(delete(ENTITY_API_URL_ID, userEntity.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userEntityRepository.count();
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

    protected UserEntity getPersistedUserEntity(UserEntity userEntity) {
        return userEntityRepository.findById(userEntity.getId()).orElseThrow();
    }

    protected void assertPersistedUserEntityToMatchAllProperties(UserEntity expectedUserEntity) {
        assertUserEntityAllPropertiesEquals(expectedUserEntity, getPersistedUserEntity(expectedUserEntity));
    }

    protected void assertPersistedUserEntityToMatchUpdatableProperties(UserEntity expectedUserEntity) {
        assertUserEntityAllUpdatablePropertiesEquals(expectedUserEntity, getPersistedUserEntity(expectedUserEntity));
    }
}
