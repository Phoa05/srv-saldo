package br.com.itau.challenge.hello.adapter.output.dynamodb;

import br.com.itau.challenge.hello.domain.model.AccountBalance;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.core.exception.SdkException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamoDbAccountBalanceRepositoryTest {

    @Mock
    private DynamoDbClient dynamoDbClient;

    private static final String TABLE_NAME = "AccountBalances";
    private Retry retry;

    @BeforeEach
    void setUp() {
        retry = Retry.of("test-retry", RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(1))
                .retryExceptions(SdkException.class)
                .ignoreExceptions(ConditionalCheckFailedException.class)
                .build());
    }

    @Test
    void shouldReturnEmptyWhenItemDoesNotExist() {
        DynamoDbAccountBalanceRepository repository =
                new DynamoDbAccountBalanceRepository(dynamoDbClient, TABLE_NAME, retry);

        when(dynamoDbClient.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().build());

        Optional<AccountBalance> result = repository.findByAccountId(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnBalanceWhenItemExists() {
        DynamoDbAccountBalanceRepository repository =
                new DynamoDbAccountBalanceRepository(dynamoDbClient, TABLE_NAME, retry);

        UUID accountId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        long epochMicros = 1751641364589998L;

        Map<String, AttributeValue> item = Map.of(
                "accountId", AttributeValue.builder().s(accountId.toString()).build(),
                "owner", AttributeValue.builder().s(owner.toString()).build(),
                "amount", AttributeValue.builder().n("183.12").build(),
                "currency", AttributeValue.builder().s("BRL").build(),
                "updatedAt", AttributeValue.builder().n(String.valueOf(epochMicros)).build()
        );

        when(dynamoDbClient.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(item).build());

        Optional<AccountBalance> result = repository.findByAccountId(accountId);

        assertThat(result).isPresent();
        assertThat(result.get().accountId()).isEqualTo(accountId);
        assertThat(result.get().owner()).isEqualTo(owner);
        assertThat(result.get().amount()).isEqualByComparingTo(BigDecimal.valueOf(183.12));
        assertThat(result.get().currency()).isEqualTo("BRL");
    }

    @Test
    void shouldReturnTrueWhenSaveSucceeds() {
        DynamoDbAccountBalanceRepository repository =
                new DynamoDbAccountBalanceRepository(dynamoDbClient, TABLE_NAME, retry);

        AccountBalance balance = new AccountBalance(
                UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, "BRL", Instant.now());

        boolean result = repository.saveIfNewer(balance);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenConditionalCheckFailsWithoutRetrying() {
        DynamoDbAccountBalanceRepository repository =
                new DynamoDbAccountBalanceRepository(dynamoDbClient, TABLE_NAME, retry);

        AccountBalance balance = new AccountBalance(
                UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, "BRL", Instant.now());

        doThrow(ConditionalCheckFailedException.builder().build())
                .when(dynamoDbClient).putItem(any(PutItemRequest.class));

        boolean result = repository.saveIfNewer(balance);

        assertThat(result).isFalse();
        verify(dynamoDbClient, times(1)).putItem(any(PutItemRequest.class));
    }

    @Test
    void shouldRetryOnTransientFailureAndEventuallySucceed() {
        DynamoDbAccountBalanceRepository repository =
                new DynamoDbAccountBalanceRepository(dynamoDbClient, TABLE_NAME, retry);

        AccountBalance balance = new AccountBalance(
                UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, "BRL", Instant.now());

        doThrow(SdkException.builder().message("throttled").build())
                .doThrow(SdkException.builder().message("throttled").build())
                .doReturn(PutItemResponse.builder().build())
                .when(dynamoDbClient).putItem(any(PutItemRequest.class));

        boolean result = repository.saveIfNewer(balance);

        assertThat(result).isTrue();
        verify(dynamoDbClient, times(3)).putItem(any(PutItemRequest.class));
    }
}