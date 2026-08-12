package br.com.itau.challenge.saldo.adapter.input.kafka.mapper;

import br.com.itau.challenge.saldo.adapter.input.kafka.dto.AccountDto;
import br.com.itau.challenge.saldo.adapter.input.kafka.dto.BalanceDto;
import br.com.itau.challenge.saldo.adapter.input.kafka.dto.TransactionDto;
import br.com.itau.challenge.saldo.adapter.input.kafka.dto.TransactionEventMessage;
import br.com.itau.challenge.saldo.domain.model.TransactionEvent;
import br.com.itau.challenge.saldo.domain.model.TransactionStatus;
import br.com.itau.challenge.saldo.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class TransactionEventMapper {

    private TransactionEventMapper() {
    }

    public static TransactionEvent toDomain(TransactionEventMessage message) {
        TransactionDto transaction = message.transaction();
        AccountDto account = message.account();
        BalanceDto balance = account.balance();

        return new TransactionEvent(
                UUID.fromString(transaction.id()),
                TransactionType.valueOf(transaction.type()),
                new BigDecimal(transaction.amount()),
                transaction.currency(),
                TransactionStatus.valueOf(transaction.status()),
                fromEpochMicros(transaction.timestamp()),
                UUID.fromString(account.id()),
                UUID.fromString(account.owner()),
                new BigDecimal(balance.amount()),
                balance.currency()
        );
    }

    private static Instant fromEpochMicros(long epochMicros) {
        long seconds = epochMicros / 1_000_000L;
        long nanos = (epochMicros % 1_000_000L) * 1_000L;
        return Instant.ofEpochSecond(seconds, nanos);
    }
}