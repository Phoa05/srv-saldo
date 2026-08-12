package br.com.itau.challenge.hello.port.input;

import br.com.itau.challenge.hello.domain.model.AccountBalance;

import java.util.UUID;

public interface GetAccountBalanceUseCase {

    AccountBalance getBalance(UUID accountId);
}
