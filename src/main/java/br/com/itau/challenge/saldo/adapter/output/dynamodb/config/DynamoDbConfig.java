package br.com.itau.challenge.saldo.adapter.output.dynamodb.config;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.net.URI;
import java.time.Duration;

@Configuration
public class DynamoDbConfig {

	@Bean
	public DynamoDbClient dynamoDbClient(
			@Value("${dynamodb.endpoint}") String endpoint,
			@Value("${dynamodb.region}") String region) {
		return DynamoDbClient
				.builder()
				.endpointOverride(URI.create(endpoint))
				.region(Region.of(region))
				.credentialsProvider(
						StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local")))
				.build();
	}

	@Bean
	public Retry dynamoDbRetry() {
		RetryConfig config = RetryConfig.custom()
				.maxAttempts(3)
				.intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofMillis(200), 2))
				.retryExceptions(SdkException.class)
				.ignoreExceptions(ConditionalCheckFailedException.class)
				.build();

		return Retry.of("dynamoDbRetry", config);
	}
}