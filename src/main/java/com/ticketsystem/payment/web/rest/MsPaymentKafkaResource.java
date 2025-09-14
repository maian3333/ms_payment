package com.ticketsystem.payment.web.rest;

import com.ticketsystem.payment.broker.KafkaProducer;
import com.ticketsystem.payment.service.dto.PaymentDTO;
import com.ticketsystem.kafka.service.KafkaUtilityService;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.RetriableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ms-payment-kafka")
public class MsPaymentKafkaResource {

    private static final Logger LOG = LoggerFactory.getLogger(MsPaymentKafkaResource.class);

    private final KafkaProducer kafkaProducer; // now only has get() + send()
    private final KafkaUtilityService kafkaUtilityService;

    public MsPaymentKafkaResource(
            KafkaProducer kafkaProducer,
            KafkaUtilityService kafkaUtilityService) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaUtilityService = kafkaUtilityService;
    }

    /**
     * Test endpoint to send a simple message
     */
    @PostMapping("/publish/simple")
    public ResponseEntity<Map<String, String>> publishSimple(@RequestParam("message") String message) {
        LOG.debug("REST request to send simple message: {} to Kafka via Supplier", message);

        try {
            String messageKey = kafkaProducer.send("payment.test", message);
            if (messageKey == null) {
                throw new IllegalStateException("Failed to queue event (null key returned)");
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Message queued successfully");
            response.put("messageKey", messageKey);
            response.put("payload", message);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error("Error queueing simple message: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to queue message: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Send a payment creation event
     */
    @PostMapping("/publish/payment-created")
    public ResponseEntity<Map<String, String>> publishPaymentCreated(@RequestBody PaymentDTO paymentDTO) {
        LOG.debug("REST request to queue payment created event for: {}", paymentDTO);

        try {
            String messageKey = kafkaProducer.send("payment.created", paymentDTO);
            if (messageKey == null) {
                throw new IllegalStateException("Failed to queue event (null key returned)");
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Payment created event queued successfully");
            response.put("messageKey", messageKey);
            response.put("paymentId", String.valueOf(paymentDTO.getId()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error("Error queueing payment created event: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to queue payment created event: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Send a payment updated event
     */
    @PostMapping("/publish/payment-updated")
    public ResponseEntity<Map<String, String>> publishPaymentUpdated(@RequestBody PaymentDTO paymentDTO) {
        LOG.debug("REST request to queue payment updated event for: {}", paymentDTO);
        try {
            String messageKey = kafkaProducer.send("payment.updated", paymentDTO);
            if (messageKey == null) {
                throw new IllegalStateException("Failed to queue event (null key returned)");
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Payment updated event queued successfully");
            response.put("messageKey", messageKey);
            response.put("paymentId", String.valueOf(paymentDTO.getId()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error("Error queueing payment updated event: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to queue payment updated event: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }

    }

    /**
     * Send a payment deleted event
     */
    @DeleteMapping("/publish/payment-deleted/{id}")
    public ResponseEntity<Map<String, String>> publishPaymentDeleted(@PathVariable Long id) {
        LOG.debug("REST request to queue payment deleted event for ID: {}", id);

        try {
            String messageKey = kafkaProducer.send("payment.deleted", id);
            if (messageKey == null) {
                throw new IllegalStateException("Failed to queue event (null key returned)");
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Payment deleted event queued successfully");
            response.put("messageKey", messageKey);
            response.put("paymentId", String.valueOf(id));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOG.error("Error queueing payment deleted event: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to queue payment deleted event: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Get Kafka utility service status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "ms_payment");
        status.put("kafkaUtilityEnabled", true);
        status.put("sseClientsCount", kafkaUtilityService.getEmitters().size());
        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }
}
