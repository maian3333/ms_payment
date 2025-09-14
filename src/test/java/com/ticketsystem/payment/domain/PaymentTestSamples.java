package com.ticketsystem.payment.domain;

import java.util.UUID;

public class PaymentTestSamples {

    public static Payment getPaymentSample1() {
        return new Payment()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .bookingId(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .userId(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .currency("currency1")
            .paymentMethod("paymentMethod1")
            .transactionId("transactionId1");
    }

    public static Payment getPaymentSample2() {
        return new Payment()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .bookingId(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .userId(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .currency("currency2")
            .paymentMethod("paymentMethod2")
            .transactionId("transactionId2");
    }

    public static Payment getPaymentRandomSampleGenerator() {
        return new Payment()
            .id(UUID.randomUUID())
            .bookingId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .currency(UUID.randomUUID().toString())
            .paymentMethod(UUID.randomUUID().toString())
            .transactionId(UUID.randomUUID().toString());
    }
}
