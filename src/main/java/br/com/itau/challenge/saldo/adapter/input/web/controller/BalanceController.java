package br.com.itau.challenge.saldo.adapter.input.web.controller;

import br.com.itau.challenge.saldo.adapter.input.web.dto.BalanceResponse;
import br.com.itau.challenge.saldo.domain.model.AccountBalance;
import br.com.itau.challenge.saldo.port.input.IAccountBalance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class BalanceController {

    private final IAccountBalance IAccountBalance;

    public BalanceController(IAccountBalance IAccountBalance) {
        this.IAccountBalance = IAccountBalance;
    }

    @GetMapping("/balances/{accountId}")
    public BalanceResponse getBalance(@PathVariable UUID accountId) {
        AccountBalance accountBalance = IAccountBalance.getBalance(accountId);
        return BalanceResponse.from(accountBalance);
    }
}