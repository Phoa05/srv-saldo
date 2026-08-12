package br.com.itau.challenge.saldo.application;

import br.com.itau.challenge.saldo.domain.model.AccountBalance;
import br.com.itau.challenge.saldo.domain.model.TransactionEvent;
import br.com.itau.challenge.saldo.port.input.IProcessTransactionEvent;
import br.com.itau.challenge.saldo.port.output.AccountBalanceRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionEventProcessingService implements IProcessTransactionEvent {

    private final AccountBalanceRepository accountBalanceRepository;

    public TransactionEventProcessingService(AccountBalanceRepository accountBalanceRepository) {
        this.accountBalanceRepository = accountBalanceRepository;
    }

    @Override
    public void process(TransactionEvent event) {
        AccountBalance balance = new AccountBalance(
                event.accountId(),
                event.accountOwner(),
                event.balanceAmount(),
                event.balanceCurrency(),
                event.transactionTimestamp()
        );

        accountBalanceRepository.saveIfNewer(balance);
    }
}