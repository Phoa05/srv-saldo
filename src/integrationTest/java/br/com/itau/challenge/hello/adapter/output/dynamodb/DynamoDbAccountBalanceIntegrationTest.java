package br.com.itau.challenge.hello.adapter.output.dynamodb;

import br.com.itau.challenge.hello.domain.model.AccountBalance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DynamoDbAccountBalanceIntegrationTest {

    @Autowired
    private DynamoDbAccountBalanceRepository repository;

    @Test
    void shouldSaveAndRetrieveBalance() {
        UUID accountId = UUID.randomUUID();
        AccountBalance balance = new AccountBalance(
                accountId, UUID.randomUUID(), BigDecimal.valueOf(100.50), "BRL", Instant.now());

        boolean saved = repository.saveIfNewer(balance);
        Optional<AccountBalance> found = repository.findByAccountId(accountId);

        assertThat(saved).isTrue();
        assertThat(found).isPresent();
        assertThat(found.get().amount()).isEqualByComparingTo(BigDecimal.valueOf(100.50));
    }

    @Test
    void shouldReturnEmptyWhenAccountDoesNotExist() {
        Optional<AccountBalance> found = repository.findByAccountId(UUID.randomUUID());

        assertThat(found).isEmpty();
    }

    @Test
    void shouldOverwriteWhenNewTransactionIsMoreRecent() {
        UUID accountId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        Instant older = Instant.now();
        Instant newer = older.plus(1, ChronoUnit.SECONDS);

        AccountBalance oldBalance = new AccountBalance(accountId, owner, BigDecimal.valueOf(50), "BRL", older);
        AccountBalance newBalance = new AccountBalance(accountId, owner, BigDecimal.valueOf(200), "BRL", newer);

        repository.saveIfNewer(oldBalance);
        boolean savedNewer = repository.saveIfNewer(newBalance);

        Optional<AccountBalance> found = repository.findByAccountId(accountId);

        assertThat(savedNewer).isTrue();
        assertThat(found).isPresent();
        assertThat(found.get().amount()).isEqualByComparingTo(BigDecimal.valueOf(200));
    }

    @Test
    void shouldDiscardOutOfOrderTransaction() {
        UUID accountId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        Instant newer = Instant.now();
        Instant older = newer.minus(1, ChronoUnit.SECONDS);

        AccountBalance newBalance = new AccountBalance(accountId, owner, BigDecimal.valueOf(200), "BRL", newer);
        AccountBalance oldBalance = new AccountBalance(accountId, owner, BigDecimal.valueOf(50), "BRL", older);

        repository.saveIfNewer(newBalance);
        boolean savedOlder = repository.saveIfNewer(oldBalance);

        Optional<AccountBalance> found = repository.findByAccountId(accountId);

        assertThat(savedOlder).isFalse();
        assertThat(found).isPresent();
        assertThat(found.get().amount()).isEqualByComparingTo(BigDecimal.valueOf(200));
    }

    @Test
    void shouldDiscardDuplicateTransaction() {
        UUID accountId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        Instant timestamp = Instant.now();

        AccountBalance balance = new AccountBalance(accountId, owner, BigDecimal.valueOf(75), "BRL", timestamp);

        repository.saveIfNewer(balance);
        boolean savedDuplicate = repository.saveIfNewer(balance);

        assertThat(savedDuplicate).isFalse();
    }
}