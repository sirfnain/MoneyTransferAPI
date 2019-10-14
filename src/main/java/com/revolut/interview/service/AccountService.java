package com.revolut.interview.service;

import com.google.common.annotations.VisibleForTesting;

import com.revolut.interview.request.CreateAccount;
import com.revolut.interview.request.DepositMoney;
import com.revolut.interview.request.TransferMoney;
import com.revolut.interview.request.WithDrawMoney;
import com.revolut.interview.response.AccountResponse;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    UUID createAccount(final CreateAccount account);
    AccountResponse getAccount(final UUID accountID);
    List<AccountResponse> getAllAccounts();
    void deleteAccount(final UUID accountID);
    void transferAmount(final TransferMoney transferMoney);
    void withDrawMoney(final WithDrawMoney withDrawMoney);
    void depositMoney(final DepositMoney depositMoney);
    @VisibleForTesting
    void deleteAllAccounts();
}
