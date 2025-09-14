package com.ticketsystem.payment.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ticketsystem.payment.domain.PaymentMethod} entity. This class is used
 * in {@link com.ticketsystem.payment.web.rest.PaymentMethodResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /payment-methods?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentMethodCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private UUIDFilter userId;

    private StringFilter type;

    private StringFilter provider;

    private StringFilter maskedDetails;

    private BooleanFilter isDefault;

    private LocalDateFilter expiresAt;

    private Boolean distinct;

    public PaymentMethodCriteria() {}

    public PaymentMethodCriteria(PaymentMethodCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(UUIDFilter::copy).orElse(null);
        this.type = other.optionalType().map(StringFilter::copy).orElse(null);
        this.provider = other.optionalProvider().map(StringFilter::copy).orElse(null);
        this.maskedDetails = other.optionalMaskedDetails().map(StringFilter::copy).orElse(null);
        this.isDefault = other.optionalIsDefault().map(BooleanFilter::copy).orElse(null);
        this.expiresAt = other.optionalExpiresAt().map(LocalDateFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PaymentMethodCriteria copy() {
        return new PaymentMethodCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public UUIDFilter getUserId() {
        return userId;
    }

    public Optional<UUIDFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public UUIDFilter userId() {
        if (userId == null) {
            setUserId(new UUIDFilter());
        }
        return userId;
    }

    public void setUserId(UUIDFilter userId) {
        this.userId = userId;
    }

    public StringFilter getType() {
        return type;
    }

    public Optional<StringFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public StringFilter type() {
        if (type == null) {
            setType(new StringFilter());
        }
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
    }

    public StringFilter getProvider() {
        return provider;
    }

    public Optional<StringFilter> optionalProvider() {
        return Optional.ofNullable(provider);
    }

    public StringFilter provider() {
        if (provider == null) {
            setProvider(new StringFilter());
        }
        return provider;
    }

    public void setProvider(StringFilter provider) {
        this.provider = provider;
    }

    public StringFilter getMaskedDetails() {
        return maskedDetails;
    }

    public Optional<StringFilter> optionalMaskedDetails() {
        return Optional.ofNullable(maskedDetails);
    }

    public StringFilter maskedDetails() {
        if (maskedDetails == null) {
            setMaskedDetails(new StringFilter());
        }
        return maskedDetails;
    }

    public void setMaskedDetails(StringFilter maskedDetails) {
        this.maskedDetails = maskedDetails;
    }

    public BooleanFilter getIsDefault() {
        return isDefault;
    }

    public Optional<BooleanFilter> optionalIsDefault() {
        return Optional.ofNullable(isDefault);
    }

    public BooleanFilter isDefault() {
        if (isDefault == null) {
            setIsDefault(new BooleanFilter());
        }
        return isDefault;
    }

    public void setIsDefault(BooleanFilter isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateFilter getExpiresAt() {
        return expiresAt;
    }

    public Optional<LocalDateFilter> optionalExpiresAt() {
        return Optional.ofNullable(expiresAt);
    }

    public LocalDateFilter expiresAt() {
        if (expiresAt == null) {
            setExpiresAt(new LocalDateFilter());
        }
        return expiresAt;
    }

    public void setExpiresAt(LocalDateFilter expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PaymentMethodCriteria that = (PaymentMethodCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(type, that.type) &&
            Objects.equals(provider, that.provider) &&
            Objects.equals(maskedDetails, that.maskedDetails) &&
            Objects.equals(isDefault, that.isDefault) &&
            Objects.equals(expiresAt, that.expiresAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, type, provider, maskedDetails, isDefault, expiresAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentMethodCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalProvider().map(f -> "provider=" + f + ", ").orElse("") +
            optionalMaskedDetails().map(f -> "maskedDetails=" + f + ", ").orElse("") +
            optionalIsDefault().map(f -> "isDefault=" + f + ", ").orElse("") +
            optionalExpiresAt().map(f -> "expiresAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
