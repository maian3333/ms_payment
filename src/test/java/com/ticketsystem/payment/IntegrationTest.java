package com.ticketsystem.payment;

import com.ticketsystem.payment.config.AsyncSyncConfiguration;
import com.ticketsystem.payment.config.EmbeddedKafka;
import com.ticketsystem.payment.config.EmbeddedSQL;
import com.ticketsystem.payment.config.JacksonConfiguration;
import com.ticketsystem.payment.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { MsPaymentApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class })
@EmbeddedSQL
@EmbeddedKafka
public @interface IntegrationTest {
}
