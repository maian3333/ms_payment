package com.ticketsystem.payment.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentMethodTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PaymentMethod getPaymentMethodSample1() {
        return new PaymentMethod()
            .id(1L)
            .userId(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .type("type1")
            .provider("provider1")
            .maskedDetails("maskedDetails1");
    }

    public static PaymentMethod getPaymentMethodSample2() {
        return new PaymentMethod()
            .id(2L)
            .userId(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .type("type2")
            .provider("provider2")
            .maskedDetails("maskedDetails2");
    }

    public static PaymentMethod getPaymentMethodRandomSampleGenerator() {
        return new PaymentMethod()
            .id(longCount.incrementAndGet())
            .userId(UUID.randomUUID())
            .type(UUID.randomUUID().toString())
            .provider(UUID.randomUUID().toString())
            .maskedDetails(UUID.randomUUID().toString());
    }
}
