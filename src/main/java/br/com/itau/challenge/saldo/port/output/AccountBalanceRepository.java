package br.com.itau.challenge.saldo.port.output;

import br.com.itau.challenge.saldo.domain.model.AccountBalance;

import java.util.Optional;
import java.util.UUID;

public interface AccountBalanceRepository {

    Optional<AccountBalance> findByAccountId(UUID accountId);

    boolean saveIfNewer(AccountBalance balance);
}
