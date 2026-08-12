package br.com.itau.challenge.hello.application;

import br.com.itau.challenge.hello.domain.exception.AccountNotFoundException;
import br.com.itau.challenge.hello.domain.model.AccountBalance;
import br.com.itau.challenge.hello.port.output.AccountBalanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountBalanceQueryServiceTest {

    @Mock
    private AccountBalanceRepository accountBalanceRepository;

    @Test
    void shouldReturnBalanceWhenAccountExists() {
        UUID accountId = UUID.randomUUID();
        AccountBalance balance = new AccountBalance(
                accountId, UUID.randomUUID(), BigDecimal.TEN, "BRL", Instant.now());
        when(accountBalanceRepository.findByAccountId(accountId))
                .thenReturn(Optional.of(balance));

        AccountBalanceQueryService service = new AccountBalanceQueryService(accountBalanceRepository);

        assertThat(service.getBalance(accountId)).isEqualTo(balance);
    }

    @Test
    void shouldThrowWhenAccountDoesNotExist() {
        UUID accountId = UUID.randomUUID();
        when(accountBalanceRepository.findByAccountId(accountId))
                .thenReturn(Optional.empty());

        AccountBalanceQueryService service = new AccountBalanceQueryService(accountBalanceRepository);

        assertThatThrownBy(() -> service.getBalance(accountId))
                .isInstanceOf(AccountNotFoundException.class);
    }
}