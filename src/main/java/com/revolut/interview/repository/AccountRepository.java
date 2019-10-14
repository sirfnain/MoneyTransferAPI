package com.revolut.interview.repository;

import com.revolut.interview.model.Account;

import java.util.Collection;
import java.util.UUID;

public interface AccountRepository {

    void save(final Account account);
    void saveAll(final Collection<Account> accounts);
    Account getAccountById(final UUID accountId);
    Collection<Account> getAllAccounts();
    void deleteAccount(final UUID accountId);
    void deleteAllAccounts();
    boolean isAccountExist(final String userId);
}
