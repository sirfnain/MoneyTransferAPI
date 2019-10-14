package com.revolut.interview.mapper;

import com.revolut.interview.model.Account;
import com.revolut.interview.request.CreateAccount;
import com.revolut.interview.response.AccountResponse;

import java.util.UUID;

public class AccountMapper {

    public Account createFrom(final CreateAccount createAccount, final UUID accountId) {
        return Account.builder()
                      .id(accountId)
                      .userId(createAccount.getUserId())
                      .currencyCode(createAccount.getCurrency())
                      .balance(createAccount.getBalance())
                      .build();
    }

    public AccountResponse createFrom(final Account account) {
        return AccountResponse.builder()
                              .accountId(account.getId())
                              .userId(account.getUserId())
                              .balance(account.getBalance())
                              .currency(account.getCurrencyCode())
                              .build();
    }
}
