package br.com.itau.challenge.saldo.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionEvent(
        UUID transactionId,
        TransactionType type,
        BigDecimal transactionAmount,
        String transactionCurrency,
        TransactionStatus status,
        Instant transactionTimestamp,
        UUID accountId,
        UUID accountOwner,
        BigDecimal balanceAmount,
        String balanceCurrency
) {
}
