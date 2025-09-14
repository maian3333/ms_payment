package com.ticketsystem.payment.web.rest;

import static com.ticketsystem.payment.domain.PaymentMethodAsserts.*;
import static com.ticketsystem.payment.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketsystem.payment.IntegrationTest;
import com.ticketsystem.payment.domain.PaymentMethod;
import com.ticketsystem.payment.repository.PaymentMethodRepository;
import com.ticketsystem.payment.service.dto.PaymentMethodDTO;
import com.ticketsystem.payment.service.mapper.PaymentMethodMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.UUID;
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

/**
 * Integration tests for the {@link PaymentMethodResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentMethodResourceIT {

    private static final UUID DEFAULT_USER_ID = UUID.randomUUID();
    private static final UUID UPDATED_USER_ID = UUID.randomUUID();

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_PROVIDER = "AAAAAAAAAA";
    private static final String UPDATED_PROVIDER = "BBBBBBBBBB";

    private static final String DEFAULT_MASKED_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_MASKED_DETAILS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_DEFAULT = false;
    private static final Boolean UPDATED_IS_DEFAULT = true;

    private static final LocalDate DEFAULT_EXPIRES_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EXPIRES_AT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_EXPIRES_AT = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/payment-methods";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private PaymentMethodMapper paymentMethodMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentMethodMockMvc;

    private PaymentMethod paymentMethod;

    private PaymentMethod insertedPaymentMethod;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentMethod createEntity() {
        return new PaymentMethod()
            .userId(DEFAULT_USER_ID)
            .type(DEFAULT_TYPE)
            .provider(DEFAULT_PROVIDER)
            .maskedDetails(DEFAULT_MASKED_DETAILS)
            .isDefault(DEFAULT_IS_DEFAULT)
            .expiresAt(DEFAULT_EXPIRES_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentMethod createUpdatedEntity() {
        return new PaymentMethod()
            .userId(UPDATED_USER_ID)
            .type(UPDATED_TYPE)
            .provider(UPDATED_PROVIDER)
            .maskedDetails(UPDATED_MASKED_DETAILS)
            .isDefault(UPDATED_IS_DEFAULT)
            .expiresAt(UPDATED_EXPIRES_AT);
    }

    @BeforeEach
    void initTest() {
        paymentMethod = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPaymentMethod != null) {
            paymentMethodRepository.delete(insertedPaymentMethod);
            insertedPaymentMethod = null;
        }
    }

    @Test
    @Transactional
    void createPaymentMethod() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);
        var returnedPaymentMethodDTO = om.readValue(
            restPaymentMethodMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(paymentMethodDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PaymentMethodDTO.class
        );

        // Validate the PaymentMethod in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPaymentMethod = paymentMethodMapper.toEntity(returnedPaymentMethodDTO);
        assertPaymentMethodUpdatableFieldsEquals(returnedPaymentMethod, getPersistedPaymentMethod(returnedPaymentMethod));

        insertedPaymentMethod = returnedPaymentMethod;
    }

    @Test
    @Transactional
    void createPaymentMethodWithExistingId() throws Exception {
        // Create the PaymentMethod with an existing ID
        paymentMethod.setId(1L);
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentMethodMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentMethod.setUserId(null);

        // Create the PaymentMethod, which fails.
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        restPaymentMethodMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paymentMethod.setType(null);

        // Create the PaymentMethod, which fails.
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        restPaymentMethodMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPaymentMethods() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList
        restPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentMethod.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].provider").value(hasItem(DEFAULT_PROVIDER)))
            .andExpect(jsonPath("$.[*].maskedDetails").value(hasItem(DEFAULT_MASKED_DETAILS)))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT)))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())));
    }

    @Test
    @Transactional
    void getPaymentMethod() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get the paymentMethod
        restPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL_ID, paymentMethod.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paymentMethod.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.provider").value(DEFAULT_PROVIDER))
            .andExpect(jsonPath("$.maskedDetails").value(DEFAULT_MASKED_DETAILS))
            .andExpect(jsonPath("$.isDefault").value(DEFAULT_IS_DEFAULT))
            .andExpect(jsonPath("$.expiresAt").value(DEFAULT_EXPIRES_AT.toString()));
    }

    @Test
    @Transactional
    void getPaymentMethodsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        Long id = paymentMethod.getId();

        defaultPaymentMethodFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPaymentMethodFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPaymentMethodFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where userId equals to
        defaultPaymentMethodFiltering("userId.equals=" + DEFAULT_USER_ID, "userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where userId in
        defaultPaymentMethodFiltering("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID, "userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where userId is not null
        defaultPaymentMethodFiltering("userId.specified=true", "userId.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where type equals to
        defaultPaymentMethodFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where type in
        defaultPaymentMethodFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where type is not null
        defaultPaymentMethodFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where type contains
        defaultPaymentMethodFiltering("type.contains=" + DEFAULT_TYPE, "type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where type does not contain
        defaultPaymentMethodFiltering("type.doesNotContain=" + UPDATED_TYPE, "type.doesNotContain=" + DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByProviderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where provider equals to
        defaultPaymentMethodFiltering("provider.equals=" + DEFAULT_PROVIDER, "provider.equals=" + UPDATED_PROVIDER);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByProviderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where provider in
        defaultPaymentMethodFiltering("provider.in=" + DEFAULT_PROVIDER + "," + UPDATED_PROVIDER, "provider.in=" + UPDATED_PROVIDER);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByProviderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where provider is not null
        defaultPaymentMethodFiltering("provider.specified=true", "provider.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByProviderContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where provider contains
        defaultPaymentMethodFiltering("provider.contains=" + DEFAULT_PROVIDER, "provider.contains=" + UPDATED_PROVIDER);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByProviderNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where provider does not contain
        defaultPaymentMethodFiltering("provider.doesNotContain=" + UPDATED_PROVIDER, "provider.doesNotContain=" + DEFAULT_PROVIDER);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByMaskedDetailsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where maskedDetails equals to
        defaultPaymentMethodFiltering("maskedDetails.equals=" + DEFAULT_MASKED_DETAILS, "maskedDetails.equals=" + UPDATED_MASKED_DETAILS);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByMaskedDetailsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where maskedDetails in
        defaultPaymentMethodFiltering(
            "maskedDetails.in=" + DEFAULT_MASKED_DETAILS + "," + UPDATED_MASKED_DETAILS,
            "maskedDetails.in=" + UPDATED_MASKED_DETAILS
        );
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByMaskedDetailsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where maskedDetails is not null
        defaultPaymentMethodFiltering("maskedDetails.specified=true", "maskedDetails.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByMaskedDetailsContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where maskedDetails contains
        defaultPaymentMethodFiltering(
            "maskedDetails.contains=" + DEFAULT_MASKED_DETAILS,
            "maskedDetails.contains=" + UPDATED_MASKED_DETAILS
        );
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByMaskedDetailsNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where maskedDetails does not contain
        defaultPaymentMethodFiltering(
            "maskedDetails.doesNotContain=" + UPDATED_MASKED_DETAILS,
            "maskedDetails.doesNotContain=" + DEFAULT_MASKED_DETAILS
        );
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByIsDefaultIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where isDefault equals to
        defaultPaymentMethodFiltering("isDefault.equals=" + DEFAULT_IS_DEFAULT, "isDefault.equals=" + UPDATED_IS_DEFAULT);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByIsDefaultIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where isDefault in
        defaultPaymentMethodFiltering(
            "isDefault.in=" + DEFAULT_IS_DEFAULT + "," + UPDATED_IS_DEFAULT,
            "isDefault.in=" + UPDATED_IS_DEFAULT
        );
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByIsDefaultIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where isDefault is not null
        defaultPaymentMethodFiltering("isDefault.specified=true", "isDefault.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByExpiresAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where expiresAt equals to
        defaultPaymentMethodFiltering("expiresAt.equals=" + DEFAULT_EXPIRES_AT, "expiresAt.equals=" + UPDATED_EXPIRES_AT);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByExpiresAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where expiresAt in
        defaultPaymentMethodFiltering(
            "expiresAt.in=" + DEFAULT_EXPIRES_AT + "," + UPDATED_EXPIRES_AT,
            "expiresAt.in=" + UPDATED_EXPIRES_AT
        );
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByExpiresAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where expiresAt is not null
        defaultPaymentMethodFiltering("expiresAt.specified=true", "expiresAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByExpiresAtIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where expiresAt is greater than or equal to
        defaultPaymentMethodFiltering(
            "expiresAt.greaterThanOrEqual=" + DEFAULT_EXPIRES_AT,
            "expiresAt.greaterThanOrEqual=" + UPDATED_EXPIRES_AT
        );
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByExpiresAtIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where expiresAt is less than or equal to
        defaultPaymentMethodFiltering("expiresAt.lessThanOrEqual=" + DEFAULT_EXPIRES_AT, "expiresAt.lessThanOrEqual=" + SMALLER_EXPIRES_AT);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByExpiresAtIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where expiresAt is less than
        defaultPaymentMethodFiltering("expiresAt.lessThan=" + UPDATED_EXPIRES_AT, "expiresAt.lessThan=" + DEFAULT_EXPIRES_AT);
    }

    @Test
    @Transactional
    void getAllPaymentMethodsByExpiresAtIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        // Get all the paymentMethodList where expiresAt is greater than
        defaultPaymentMethodFiltering("expiresAt.greaterThan=" + SMALLER_EXPIRES_AT, "expiresAt.greaterThan=" + DEFAULT_EXPIRES_AT);
    }

    private void defaultPaymentMethodFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPaymentMethodShouldBeFound(shouldBeFound);
        defaultPaymentMethodShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPaymentMethodShouldBeFound(String filter) throws Exception {
        restPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paymentMethod.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].provider").value(hasItem(DEFAULT_PROVIDER)))
            .andExpect(jsonPath("$.[*].maskedDetails").value(hasItem(DEFAULT_MASKED_DETAILS)))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT)))
            .andExpect(jsonPath("$.[*].expiresAt").value(hasItem(DEFAULT_EXPIRES_AT.toString())));

        // Check, that the count call also returns 1
        restPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPaymentMethodShouldNotBeFound(String filter) throws Exception {
        restPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPaymentMethodMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPaymentMethod() throws Exception {
        // Get the paymentMethod
        restPaymentMethodMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPaymentMethod() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentMethod
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.findById(paymentMethod.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPaymentMethod are not directly saved in db
        em.detach(updatedPaymentMethod);
        updatedPaymentMethod
            .userId(UPDATED_USER_ID)
            .type(UPDATED_TYPE)
            .provider(UPDATED_PROVIDER)
            .maskedDetails(UPDATED_MASKED_DETAILS)
            .isDefault(UPDATED_IS_DEFAULT)
            .expiresAt(UPDATED_EXPIRES_AT);
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(updatedPaymentMethod);

        restPaymentMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentMethodDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isOk());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaymentMethodToMatchAllProperties(updatedPaymentMethod);
    }

    @Test
    @Transactional
    void putNonExistingPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentMethodDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaymentMethodWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentMethod using partial update
        PaymentMethod partialUpdatedPaymentMethod = new PaymentMethod();
        partialUpdatedPaymentMethod.setId(paymentMethod.getId());

        partialUpdatedPaymentMethod.provider(UPDATED_PROVIDER).maskedDetails(UPDATED_MASKED_DETAILS).isDefault(UPDATED_IS_DEFAULT);

        restPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentMethod.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentMethod))
            )
            .andExpect(status().isOk());

        // Validate the PaymentMethod in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentMethodUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPaymentMethod, paymentMethod),
            getPersistedPaymentMethod(paymentMethod)
        );
    }

    @Test
    @Transactional
    void fullUpdatePaymentMethodWithPatch() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paymentMethod using partial update
        PaymentMethod partialUpdatedPaymentMethod = new PaymentMethod();
        partialUpdatedPaymentMethod.setId(paymentMethod.getId());

        partialUpdatedPaymentMethod
            .userId(UPDATED_USER_ID)
            .type(UPDATED_TYPE)
            .provider(UPDATED_PROVIDER)
            .maskedDetails(UPDATED_MASKED_DETAILS)
            .isDefault(UPDATED_IS_DEFAULT)
            .expiresAt(UPDATED_EXPIRES_AT);

        restPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaymentMethod.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaymentMethod))
            )
            .andExpect(status().isOk());

        // Validate the PaymentMethod in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaymentMethodUpdatableFieldsEquals(partialUpdatedPaymentMethod, getPersistedPaymentMethod(partialUpdatedPaymentMethod));
    }

    @Test
    @Transactional
    void patchNonExistingPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentMethodDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaymentMethod() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paymentMethod.setId(longCount.incrementAndGet());

        // Create the PaymentMethod
        PaymentMethodDTO paymentMethodDTO = paymentMethodMapper.toDto(paymentMethod);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMethodMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paymentMethodDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaymentMethod in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePaymentMethod() throws Exception {
        // Initialize the database
        insertedPaymentMethod = paymentMethodRepository.saveAndFlush(paymentMethod);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the paymentMethod
        restPaymentMethodMockMvc
            .perform(delete(ENTITY_API_URL_ID, paymentMethod.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paymentMethodRepository.count();
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

    protected PaymentMethod getPersistedPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.findById(paymentMethod.getId()).orElseThrow();
    }

    protected void assertPersistedPaymentMethodToMatchAllProperties(PaymentMethod expectedPaymentMethod) {
        assertPaymentMethodAllPropertiesEquals(expectedPaymentMethod, getPersistedPaymentMethod(expectedPaymentMethod));
    }

    protected void assertPersistedPaymentMethodToMatchUpdatableProperties(PaymentMethod expectedPaymentMethod) {
        assertPaymentMethodAllUpdatablePropertiesEquals(expectedPaymentMethod, getPersistedPaymentMethod(expectedPaymentMethod));
    }
}
