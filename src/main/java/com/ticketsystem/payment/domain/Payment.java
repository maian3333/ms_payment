package com.ticketsystem.payment.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ticketsystem.payment.domain.enumeration.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A Payment.
 */
@Entity
@Table(name = "payment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "booking_id", length = 36, nullable = false)
    private UUID bookingId;

    @NotNull
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "user_id", length = 36, nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "currency", nullable = false)
    private String currency;

    @NotNull
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;

    @Lob
    @Column(name = "gateway_response")
    private String gatewayResponse;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "refundable_until")
    private Instant refundableUntil;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "payment")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "payment" }, allowSetters = true)
    private Set<Refund> refunds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Payment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getBookingId() {
        return this.bookingId;
    }

    public Payment bookingId(UUID bookingId) {
        this.setBookingId(bookingId);
        return this;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public Payment userId(UUID userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Payment amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public Payment currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    public Payment paymentMethod(String paymentMethod) {
        this.setPaymentMethod(paymentMethod);
        return this;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return this.status;
    }

    public Payment status(PaymentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getGatewayTransactionId() {
        return this.gatewayTransactionId;
    }

    public Payment gatewayTransactionId(String gatewayTransactionId) {
        this.setGatewayTransactionId(gatewayTransactionId);
        return this;
    }

    public void setGatewayTransactionId(String gatewayTransactionId) {
        this.gatewayTransactionId = gatewayTransactionId;
    }

    public String getGatewayResponse() {
        return this.gatewayResponse;
    }

    public Payment gatewayResponse(String gatewayResponse) {
        this.setGatewayResponse(gatewayResponse);
        return this;
    }

    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }

    public Instant getPaidAt() {
        return this.paidAt;
    }

    public Payment paidAt(Instant paidAt) {
        this.setPaidAt(paidAt);
        return this;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public Instant getRefundableUntil() {
        return this.refundableUntil;
    }

    public Payment refundableUntil(Instant refundableUntil) {
        this.setRefundableUntil(refundableUntil);
        return this;
    }

    public void setRefundableUntil(Instant refundableUntil) {
        this.refundableUntil = refundableUntil;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Payment createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Refund> getRefunds() {
        return this.refunds;
    }

    public void setRefunds(Set<Refund> refunds) {
        if (this.refunds != null) {
            this.refunds.forEach(i -> i.setPayment(null));
        }
        if (refunds != null) {
            refunds.forEach(i -> i.setPayment(this));
        }
        this.refunds = refunds;
    }

    public Payment refunds(Set<Refund> refunds) {
        this.setRefunds(refunds);
        return this;
    }

    public Payment addRefunds(Refund refund) {
        this.refunds.add(refund);
        refund.setPayment(this);
        return this;
    }

    public Payment removeRefunds(Refund refund) {
        this.refunds.remove(refund);
        refund.setPayment(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return getId() != null && getId().equals(((Payment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", bookingId='" + getBookingId() + "'" +
            ", userId='" + getUserId() + "'" +
            ", amount=" + getAmount() +
            ", currency='" + getCurrency() + "'" +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            ", status='" + getStatus() + "'" +
            ", gatewayTransactionId='" + getGatewayTransactionId() + "'" +
            ", gatewayResponse='" + getGatewayResponse() + "'" +
            ", paidAt='" + getPaidAt() + "'" +
            ", refundableUntil='" + getRefundableUntil() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
