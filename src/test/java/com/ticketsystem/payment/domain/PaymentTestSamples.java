package com.ticketsystem.payment.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Payment getPaymentSample1() {
        return new Payment()
            .id(1L)
            .bookingId(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .userId(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .currency("currency1")
            .paymentMethod("paymentMethod1")
            .gatewayTransactionId("gatewayTransactionId1");
    }

    public static Payment getPaymentSample2() {
        return new Payment()
            .id(2L)
            .bookingId(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .userId(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .currency("currency2")
            .paymentMethod("paymentMethod2")
            .gatewayTransactionId("gatewayTransactionId2");
    }

    public static Payment getPaymentRandomSampleGenerator() {
        return new Payment()
            .id(longCount.incrementAndGet())
            .bookingId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .currency(UUID.randomUUID().toString())
            .paymentMethod(UUID.randomUUID().toString())
            .gatewayTransactionId(UUID.randomUUID().toString());
    }
}
