package br.com.itau.challenge.hello.application;

import br.com.itau.challenge.hello.domain.exception.AccountNotFoundException;
import br.com.itau.challenge.hello.domain.model.AccountBalance;
import br.com.itau.challenge.hello.port.input.GetAccountBalanceUseCase;
import br.com.itau.challenge.hello.port.output.AccountBalanceRepository;

import java.util.UUID;

public class AccountBalanceQueryService implements GetAccountBalanceUseCase {

    private final AccountBalanceRepository accountBalanceRepository;

    public AccountBalanceQueryService(AccountBalanceRepository accountBalanceRepository) {
        this.accountBalanceRepository = accountBalanceRepository;
    }

    @Override
    public AccountBalance getBalance(UUID accountId) {
        return accountBalanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}