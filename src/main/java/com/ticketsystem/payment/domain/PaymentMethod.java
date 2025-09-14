package com.ticketsystem.payment.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A PaymentMethod.
 */
@Entity
@Table(name = "payment_method")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentMethod implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "user_id", length = 36, nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "provider")
    private String provider;

    @Column(name = "masked_details")
    private String maskedDetails;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "expires_at")
    private LocalDate expiresAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PaymentMethod id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public PaymentMethod userId(UUID userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getType() {
        return this.type;
    }

    public PaymentMethod type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvider() {
        return this.provider;
    }

    public PaymentMethod provider(String provider) {
        this.setProvider(provider);
        return this;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getMaskedDetails() {
        return this.maskedDetails;
    }

    public PaymentMethod maskedDetails(String maskedDetails) {
        this.setMaskedDetails(maskedDetails);
        return this;
    }

    public void setMaskedDetails(String maskedDetails) {
        this.maskedDetails = maskedDetails;
    }

    public Boolean getIsDefault() {
        return this.isDefault;
    }

    public PaymentMethod isDefault(Boolean isDefault) {
        this.setIsDefault(isDefault);
        return this;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDate getExpiresAt() {
        return this.expiresAt;
    }

    public PaymentMethod expiresAt(LocalDate expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(LocalDate expiresAt) {
        this.expiresAt = expiresAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentMethod)) {
            return false;
        }
        return getId() != null && getId().equals(((PaymentMethod) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentMethod{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", type='" + getType() + "'" +
            ", provider='" + getProvider() + "'" +
            ", maskedDetails='" + getMaskedDetails() + "'" +
            ", isDefault='" + getIsDefault() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            "}";
    }
}
