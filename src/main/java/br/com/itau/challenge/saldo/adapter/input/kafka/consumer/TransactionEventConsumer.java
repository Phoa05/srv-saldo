package br.com.itau.challenge.saldo.adapter.input.kafka.consumer;

import br.com.itau.challenge.saldo.adapter.input.kafka.dto.TransactionEventMessage;
import br.com.itau.challenge.saldo.adapter.input.kafka.mapper.TransactionEventMapper;
import br.com.itau.challenge.saldo.domain.model.TransactionEvent;
import br.com.itau.challenge.saldo.port.input.IProcessTransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class TransactionEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionEventConsumer.class);

    private final IProcessTransactionEvent IProcessTransactionEvent;
    private final ObjectMapper objectMapper;

    public TransactionEventConsumer(
            IProcessTransactionEvent IProcessTransactionEvent,
            ObjectMapper objectMapper) {
        this.IProcessTransactionEvent = IProcessTransactionEvent;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${transactions.topic-name}")
    public void consume(String payload) {
        TransactionEventMessage message;
        try {
            message = objectMapper.readValue(payload, TransactionEventMessage.class);
        } catch (Exception e) {
            log.error("Discarding malformed transaction message: {}", e.getMessage());
            return;
        }

        TransactionEvent event;
        try {
            event = TransactionEventMapper.toDomain(message);
        } catch (Exception e) {
            log.error("Discarding invalid transaction message (validation failed): {}", e.getMessage());
            return;
        }

        IProcessTransactionEvent.process(event);
    }
}