package com.ticketsystem.payment.domain;

import static com.ticketsystem.payment.domain.PaymentTestSamples.*;
import static com.ticketsystem.payment.domain.RefundTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ticketsystem.payment.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RefundTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Refund.class);
        Refund refund1 = getRefundSample1();
        Refund refund2 = new Refund();
        assertThat(refund1).isNotEqualTo(refund2);

        refund2.setId(refund1.getId());
        assertThat(refund1).isEqualTo(refund2);

        refund2 = getRefundSample2();
        assertThat(refund1).isNotEqualTo(refund2);
    }

    @Test
    void paymentTest() {
        Refund refund = getRefundRandomSampleGenerator();
        Payment paymentBack = getPaymentRandomSampleGenerator();

        refund.setPayment(paymentBack);
        assertThat(refund.getPayment()).isEqualTo(paymentBack);

        refund.payment(null);
        assertThat(refund.getPayment()).isNull();
    }
}
