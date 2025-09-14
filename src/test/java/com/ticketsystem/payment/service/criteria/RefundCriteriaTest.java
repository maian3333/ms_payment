package com.ticketsystem.payment.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RefundCriteriaTest {

    @Test
    void newRefundCriteriaHasAllFiltersNullTest() {
        var refundCriteria = new RefundCriteria();
        assertThat(refundCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void refundCriteriaFluentMethodsCreatesFiltersTest() {
        var refundCriteria = new RefundCriteria();

        setAllFilters(refundCriteria);

        assertThat(refundCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void refundCriteriaCopyCreatesNullFilterTest() {
        var refundCriteria = new RefundCriteria();
        var copy = refundCriteria.copy();

        assertThat(refundCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(refundCriteria)
        );
    }

    @Test
    void refundCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var refundCriteria = new RefundCriteria();
        setAllFilters(refundCriteria);

        var copy = refundCriteria.copy();

        assertThat(refundCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(refundCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var refundCriteria = new RefundCriteria();

        assertThat(refundCriteria).hasToString("RefundCriteria{}");
    }

    private static void setAllFilters(RefundCriteria refundCriteria) {
        refundCriteria.id();
        refundCriteria.amount();
        refundCriteria.reason();
        refundCriteria.status();
        refundCriteria.processedAt();
        refundCriteria.gatewayRefundId();
        refundCriteria.paymentId();
        refundCriteria.distinct();
    }

    private static Condition<RefundCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getReason()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getProcessedAt()) &&
                condition.apply(criteria.getGatewayRefundId()) &&
                condition.apply(criteria.getPaymentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RefundCriteria> copyFiltersAre(RefundCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getReason(), copy.getReason()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getProcessedAt(), copy.getProcessedAt()) &&
                condition.apply(criteria.getGatewayRefundId(), copy.getGatewayRefundId()) &&
                condition.apply(criteria.getPaymentId(), copy.getPaymentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
