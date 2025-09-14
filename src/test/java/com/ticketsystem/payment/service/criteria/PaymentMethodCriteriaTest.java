package com.ticketsystem.payment.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PaymentMethodCriteriaTest {

    @Test
    void newPaymentMethodCriteriaHasAllFiltersNullTest() {
        var paymentMethodCriteria = new PaymentMethodCriteria();
        assertThat(paymentMethodCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void paymentMethodCriteriaFluentMethodsCreatesFiltersTest() {
        var paymentMethodCriteria = new PaymentMethodCriteria();

        setAllFilters(paymentMethodCriteria);

        assertThat(paymentMethodCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void paymentMethodCriteriaCopyCreatesNullFilterTest() {
        var paymentMethodCriteria = new PaymentMethodCriteria();
        var copy = paymentMethodCriteria.copy();

        assertThat(paymentMethodCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(paymentMethodCriteria)
        );
    }

    @Test
    void paymentMethodCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var paymentMethodCriteria = new PaymentMethodCriteria();
        setAllFilters(paymentMethodCriteria);

        var copy = paymentMethodCriteria.copy();

        assertThat(paymentMethodCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(paymentMethodCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var paymentMethodCriteria = new PaymentMethodCriteria();

        assertThat(paymentMethodCriteria).hasToString("PaymentMethodCriteria{}");
    }

    private static void setAllFilters(PaymentMethodCriteria paymentMethodCriteria) {
        paymentMethodCriteria.id();
        paymentMethodCriteria.userId();
        paymentMethodCriteria.type();
        paymentMethodCriteria.provider();
        paymentMethodCriteria.maskedDetails();
        paymentMethodCriteria.isDefault();
        paymentMethodCriteria.expiresAt();
        paymentMethodCriteria.distinct();
    }

    private static Condition<PaymentMethodCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getProvider()) &&
                condition.apply(criteria.getMaskedDetails()) &&
                condition.apply(criteria.getIsDefault()) &&
                condition.apply(criteria.getExpiresAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PaymentMethodCriteria> copyFiltersAre(
        PaymentMethodCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getProvider(), copy.getProvider()) &&
                condition.apply(criteria.getMaskedDetails(), copy.getMaskedDetails()) &&
                condition.apply(criteria.getIsDefault(), copy.getIsDefault()) &&
                condition.apply(criteria.getExpiresAt(), copy.getExpiresAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
