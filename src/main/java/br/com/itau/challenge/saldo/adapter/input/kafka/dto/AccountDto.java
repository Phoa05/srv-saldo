package br.com.itau.challenge.saldo.adapter.input.kafka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccountDto(
        String id,
        String owner,
        @JsonProperty("created_at") long createdAt,
        String status,
        BalanceDto balance
) {
}
