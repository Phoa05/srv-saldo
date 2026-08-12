package br.com.itau.challenge.saldo.application;

import br.com.itau.challenge.saldo.domain.exception.AccountNotFoundException;
import br.com.itau.challenge.saldo.domain.model.AccountBalance;
import br.com.itau.challenge.saldo.port.input.IAccountBalance;
import br.com.itau.challenge.saldo.port.output.AccountBalanceRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountBalanceQueryService implements IAccountBalance {

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