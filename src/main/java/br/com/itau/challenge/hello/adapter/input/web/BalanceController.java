package br.com.itau.challenge.hello.adapter.input.web;

import br.com.itau.challenge.hello.adapter.input.web.dto.BalanceResponse;
import br.com.itau.challenge.hello.domain.model.AccountBalance;
import br.com.itau.challenge.hello.port.input.GetAccountBalanceUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class BalanceController {

    private final GetAccountBalanceUseCase getAccountBalanceUseCase;

    public BalanceController(GetAccountBalanceUseCase getAccountBalanceUseCase) {
        this.getAccountBalanceUseCase = getAccountBalanceUseCase;
    }

    @GetMapping("/balances/{accountId}")
    public BalanceResponse getBalance(@PathVariable UUID accountId) {
        AccountBalance accountBalance = getAccountBalanceUseCase.getBalance(accountId);
        return BalanceResponse.from(accountBalance);
    }
}