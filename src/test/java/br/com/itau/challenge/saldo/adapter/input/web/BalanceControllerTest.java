package br.com.itau.challenge.saldo.adapter.input.web;

import br.com.itau.challenge.saldo.adapter.input.web.controller.BalanceController;
import br.com.itau.challenge.saldo.domain.exception.AccountNotFoundException;
import br.com.itau.challenge.saldo.domain.model.AccountBalance;
import br.com.itau.challenge.saldo.port.input.IAccountBalance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAccountBalance IAccountBalance;

    @Test
    void shouldReturnBalanceWhenAccountExists() throws Exception {
        UUID accountId = UUID.fromString("5b19c8b6-0cc4-4c72-a989-0c2ee15fa975");
        UUID owner = UUID.fromString("315e3cfe-f4af-4cd2-b298-a449e614349a");
        AccountBalance balance = new AccountBalance(
                accountId, owner, BigDecimal.valueOf(183.12), "BRL", Instant.parse("2025-07-05T21:04:13.433Z"));

        when(IAccountBalance.getBalance(accountId)).thenReturn(balance);

        mockMvc.perform(get("/balances/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.owner").value(owner.toString()))
                .andExpect(jsonPath("$.balance.amount").value(183.12))
                .andExpect(jsonPath("$.balance.currency").value("BRL"));
    }

    @Test
    void shouldReturn404WhenAccountDoesNotExist() throws Exception {
        UUID accountId = UUID.randomUUID();
        when(IAccountBalance.getBalance(accountId))
                .thenThrow(new AccountNotFoundException(accountId));

        mockMvc.perform(get("/balances/{accountId}", accountId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenAccountIdIsNotValidUuid() throws Exception {
        mockMvc.perform(get("/balances/{accountId}", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }
}