package br.com.itau.challenge.saldo.adapter.input.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.com.itau.challenge.saldo.domain.model.AccountBalance;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record BalanceResponse(
        UUID id,
        UUID owner,
        Balance balance,
        @JsonProperty("updated_at") String updatedAt
) {

    private static final ZoneId SAO_PAULO = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter ISO_WITH_OFFSET = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public record Balance(
            BigDecimal amount,
            String currency
    ) {
    }

    public static BalanceResponse from(AccountBalance accountBalance) {
        return new BalanceResponse(
                accountBalance.accountId(),
                accountBalance.owner(),
                new Balance(accountBalance.amount(), accountBalance.currency()),
                ISO_WITH_OFFSET.format(accountBalance.updatedAt().atZone(SAO_PAULO))
        );
    }
}