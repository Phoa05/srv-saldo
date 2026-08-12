package br.com.itau.challenge.saldo.application;

import br.com.itau.challenge.saldo.domain.model.AccountBalance;
import br.com.itau.challenge.saldo.domain.model.TransactionEvent;
import br.com.itau.challenge.saldo.domain.model.TransactionStatus;
import br.com.itau.challenge.saldo.domain.model.TransactionType;
import br.com.itau.challenge.saldo.port.output.AccountBalanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionEventProcessingServiceTest {

    @Mock
    private AccountBalanceRepository accountBalanceRepository;

    @Test
    void shouldMapEventToBalanceAndDelegateToRepository() {
        UUID accountId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        Instant timestamp = Instant.now();

        TransactionEvent event = new TransactionEvent(
                UUID.randomUUID(),
                TransactionType.CREDIT,
                BigDecimal.valueOf(97.07),
                "BRL",
                TransactionStatus.APPROVED,
                timestamp,
                accountId,
                owner,
                BigDecimal.valueOf(183.12),
                "BRL"
        );

        TransactionEventProcessingService service =
                new TransactionEventProcessingService(accountBalanceRepository);

        service.process(event);

        ArgumentCaptor<AccountBalance> captor = ArgumentCaptor.forClass(AccountBalance.class);
        verify(accountBalanceRepository).saveIfNewer(captor.capture());

        AccountBalance saved = captor.getValue();
        assertThat(saved.accountId()).isEqualTo(accountId);
        assertThat(saved.owner()).isEqualTo(owner);
        assertThat(saved.amount()).isEqualTo(BigDecimal.valueOf(183.12));
        assertThat(saved.currency()).isEqualTo("BRL");
        assertThat(saved.updatedAt()).isEqualTo(timestamp);
    }
}