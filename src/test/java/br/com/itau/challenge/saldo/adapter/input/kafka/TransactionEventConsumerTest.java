package br.com.itau.challenge.saldo.adapter.input.kafka;

import br.com.itau.challenge.saldo.adapter.input.kafka.consumer.TransactionEventConsumer;
import br.com.itau.challenge.saldo.domain.model.TransactionEvent;
import br.com.itau.challenge.saldo.port.input.IProcessTransactionEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class TransactionEventConsumerTest {

    @Mock
    private IProcessTransactionEvent IProcessTransactionEvent;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldProcessValidMessage() {
        TransactionEventConsumer consumer =
                new TransactionEventConsumer(IProcessTransactionEvent, objectMapper);

        String payload = """
                {
                  "transaction": {
                    "id": "8e8ae808-b154-48b5-9f3e-553935cc4543",
                    "type": "CREDIT",
                    "amount": "97.07",
                    "currency": "BRL",
                    "status": "APPROVED",
                    "timestamp": 1751641364589998
                  },
                  "account": {
                    "id": "5b19c8b6-0cc4-4c72-a989-0c2ee15fa975",
                    "owner": "315e3cfe-f4af-4cd2-b298-a449e614349a",
                    "created_at": 1634874339000000,
                    "status": "ENABLED",
                    "balance": {
                      "amount": "183.12",
                      "currency": "BRL"
                    }
                  }
                }
                """;

        consumer.consume(payload);

        verify(IProcessTransactionEvent).process(any(TransactionEvent.class));
    }

    @Test
    void shouldDiscardMalformedJson() {
        TransactionEventConsumer consumer =
                new TransactionEventConsumer(IProcessTransactionEvent, objectMapper);

        consumer.consume("{not-valid-json");

        verify(IProcessTransactionEvent, never()).process(any());
    }

    @Test
    void shouldDiscardMessageWithInvalidEnumValue() {
        TransactionEventConsumer consumer =
                new TransactionEventConsumer(IProcessTransactionEvent, objectMapper);

        String payload = """
                {
                  "transaction": {
                    "id": "8e8ae808-b154-48b5-9f3e-553935cc4543",
                    "type": "INVALID_TYPE",
                    "amount": "97.07",
                    "currency": "BRL",
                    "status": "APPROVED",
                    "timestamp": 1751641364589998
                  },
                  "account": {
                    "id": "5b19c8b6-0cc4-4c72-a989-0c2ee15fa975",
                    "owner": "315e3cfe-f4af-4cd2-b298-a449e614349a",
                    "created_at": 1634874339000000,
                    "status": "ENABLED",
                    "balance": {
                      "amount": "183.12",
                      "currency": "BRL"
                    }
                  }
                }
                """;

        consumer.consume(payload);

        verify(IProcessTransactionEvent, never()).process(any());
    }
}