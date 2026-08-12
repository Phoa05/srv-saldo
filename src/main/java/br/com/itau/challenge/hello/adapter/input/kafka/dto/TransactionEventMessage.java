package br.com.itau.challenge.hello.adapter.input.kafka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransactionEventMessage(
        TransactionDto transaction,
        AccountDto account
) {

    public record TransactionDto(
            String id,
            String type,
            String amount,
            String currency,
            String status,
            long timestamp
    ) {
    }

    public record AccountDto(
            String id,
            String owner,
            @JsonProperty("created_at") long createdAt,
            String status,
            BalanceDto balance
    ) {
    }

    public record BalanceDto(
            String amount,
            String currency
    ) {
    }
}