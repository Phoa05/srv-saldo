package br.com.itau.challenge.saldo.port.input;

import br.com.itau.challenge.saldo.domain.model.AccountBalance;

import java.util.UUID;

public interface IAccountBalance {

    AccountBalance getBalance(UUID accountId);
}
