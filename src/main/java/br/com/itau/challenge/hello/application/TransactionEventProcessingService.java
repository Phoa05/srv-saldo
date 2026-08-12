package br.com.itau.challenge.hello.application;

import br.com.itau.challenge.hello.domain.model.AccountBalance;
import br.com.itau.challenge.hello.domain.model.TransactionEvent;
import br.com.itau.challenge.hello.port.input.ProcessTransactionEventUseCase;
import br.com.itau.challenge.hello.port.output.AccountBalanceRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionEventProcessingService implements ProcessTransactionEventUseCase {

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