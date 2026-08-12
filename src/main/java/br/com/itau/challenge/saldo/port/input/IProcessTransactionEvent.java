package br.com.itau.challenge.saldo.port.input;

import br.com.itau.challenge.saldo.domain.model.TransactionEvent;

public interface IProcessTransactionEvent {

    void process(TransactionEvent event);
}