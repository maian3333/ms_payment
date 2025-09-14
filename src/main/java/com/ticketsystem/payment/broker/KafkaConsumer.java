package com.ticketsystem.payment.broker;

import com.ticketsystem.kafka.broker.GenericKafkaConsumer;
import com.ticketsystem.kafka.service.KafkaUtilityService;
import org.springframework.stereotype.Component;

@Component("kafkaConsumer")
public class KafkaConsumer extends GenericKafkaConsumer {

    public KafkaConsumer(KafkaUtilityService kafkaUtilityService) {
        super(kafkaUtilityService);
    }

}
