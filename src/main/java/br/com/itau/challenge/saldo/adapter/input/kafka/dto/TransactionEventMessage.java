package br.com.itau.challenge.saldo.adapter.input.kafka.dto;

public record TransactionEventMessage(
        TransactionDto transaction,
        AccountDto account
) {






}