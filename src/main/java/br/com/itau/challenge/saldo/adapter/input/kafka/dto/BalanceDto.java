package br.com.itau.challenge.saldo.adapter.input.kafka.dto;

public record BalanceDto(
        String amount,
        String currency
) {
}
