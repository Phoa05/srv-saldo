package br.com.itau.challenge.saldo.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountBalance(
        UUID accountId,
        UUID owner,
        BigDecimal amount,
        String currency,
        Instant updatedAt
) {
}
