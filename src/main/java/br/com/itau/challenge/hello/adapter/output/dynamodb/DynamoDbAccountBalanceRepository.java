package br.com.itau.challenge.hello.adapter.output.dynamodb;

import br.com.itau.challenge.hello.domain.model.AccountBalance;
import br.com.itau.challenge.hello.port.output.AccountBalanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class DynamoDbAccountBalanceRepository implements AccountBalanceRepository {

    private static final Logger log = LoggerFactory.getLogger(DynamoDbAccountBalanceRepository.class);

    private static final String ACCOUNT_ID_ATTRIBUTE = "accountId";
    private static final String OWNER_ATTRIBUTE = "owner";
    private static final String AMOUNT_ATTRIBUTE = "amount";
    private static final String CURRENCY_ATTRIBUTE = "currency";
    private static final String UPDATED_AT_ATTRIBUTE = "updatedAt";

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public DynamoDbAccountBalanceRepository(
            DynamoDbClient dynamoDbClient,
            @Value("${dynamodb.balance-table-name}") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    @Override
    public Optional<AccountBalance> findByAccountId(UUID accountId) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(ACCOUNT_ID_ATTRIBUTE, AttributeValue.builder().s(accountId.toString()).build()))
                .build();

        GetItemResponse response = dynamoDbClient.getItem(request);

        if (!response.hasItem()) {
            return Optional.empty();
        }

        return Optional.of(toDomain(response.item()));
    }

    @Override
    public boolean saveIfNewer(AccountBalance balance) {
        long updatedAtEpochMicros = toEpochMicros(balance.updatedAt());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(Map.of(
                        ACCOUNT_ID_ATTRIBUTE, AttributeValue.builder().s(balance.accountId().toString()).build(),
                        OWNER_ATTRIBUTE, AttributeValue.builder().s(balance.owner().toString()).build(),
                        AMOUNT_ATTRIBUTE, AttributeValue.builder().n(balance.amount().toPlainString()).build(),
                        CURRENCY_ATTRIBUTE, AttributeValue.builder().s(balance.currency()).build(),
                        UPDATED_AT_ATTRIBUTE, AttributeValue.builder().n(String.valueOf(updatedAtEpochMicros)).build()
                ))
                .conditionExpression(
                        "attribute_not_exists(" + ACCOUNT_ID_ATTRIBUTE + ") OR " + UPDATED_AT_ATTRIBUTE + " < :newUpdatedAt")
                .expressionAttributeValues(Map.of(
                        ":newUpdatedAt", AttributeValue.builder().n(String.valueOf(updatedAtEpochMicros)).build()
                ))
                .build();

        try {
            dynamoDbClient.putItem(request);
            return true;
        } catch (ConditionalCheckFailedException e) {
            log.info("Discarded stale/duplicate balance update for account {} (updatedAt={})",
                    balance.accountId(), balance.updatedAt());
            return false;
        }
    }

    private AccountBalance toDomain(Map<String, AttributeValue> item) {
        return new AccountBalance(
                UUID.fromString(item.get(ACCOUNT_ID_ATTRIBUTE).s()),
                UUID.fromString(item.get(OWNER_ATTRIBUTE).s()),
                new BigDecimal(item.get(AMOUNT_ATTRIBUTE).n()),
                item.get(CURRENCY_ATTRIBUTE).s(),
                fromEpochMicros(Long.parseLong(item.get(UPDATED_AT_ATTRIBUTE).n()))
        );
    }

    private static long toEpochMicros(Instant instant) {
        return instant.getEpochSecond() * 1_000_000L + instant.getNano() / 1_000L;
    }

    private static Instant fromEpochMicros(long epochMicros) {
        long seconds = epochMicros / 1_000_000L;
        long nanos = (epochMicros % 1_000_000L) * 1_000L;
        return Instant.ofEpochSecond(seconds, nanos);
    }
}