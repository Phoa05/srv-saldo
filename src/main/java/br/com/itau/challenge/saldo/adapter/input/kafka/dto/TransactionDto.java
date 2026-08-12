package br.com.itau.challenge.saldo.adapter.input.kafka.dto;

public record TransactionDto(
        String id,
        String type,
        String amount,
        String currency,
        String status,
        long timestamp
) {
}