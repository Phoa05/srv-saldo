package br.com.itau.challenge.hello.adapter.input.kafka;

import br.com.itau.challenge.hello.adapter.input.kafka.dto.TransactionEventMessage;
import br.com.itau.challenge.hello.domain.model.TransactionEvent;
import br.com.itau.challenge.hello.port.input.ProcessTransactionEventUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class TransactionEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionEventConsumer.class);

    private final ProcessTransactionEventUseCase processTransactionEventUseCase;
    private final ObjectMapper objectMapper;

    public TransactionEventConsumer(
            ProcessTransactionEventUseCase processTransactionEventUseCase,
            ObjectMapper objectMapper) {
        this.processTransactionEventUseCase = processTransactionEventUseCase;
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

        processTransactionEventUseCase.process(event);
    }
}