package br.com.itau.challenge.saldo.adapter.input.web.controller;

import br.com.itau.challenge.saldo.adapter.input.web.dto.BalanceResponse;
import br.com.itau.challenge.saldo.domain.model.AccountBalance;
import br.com.itau.challenge.saldo.port.input.IAccountBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class BalanceController {

    private static final Logger log = LoggerFactory.getLogger(BalanceController.class);
    private final IAccountBalance IAccountBalance;

    public BalanceController(IAccountBalance IAccountBalance) {
        this.IAccountBalance = IAccountBalance;
    }

    @GetMapping("/balances/{accountId}")
    public BalanceResponse getBalance(@PathVariable UUID accountId) {
        log.info("Searching balances for {}", accountId);
        AccountBalance accountBalance = IAccountBalance.getBalance(accountId);
        log.info("Account {} balance is {}", accountId,accountBalance);
        return BalanceResponse.from(accountBalance);
    }
}