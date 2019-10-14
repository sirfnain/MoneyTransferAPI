package com.revolut.interview.repository;

import com.revolut.interview.model.Account;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepositoryImpl implements AccountRepository {

    private final Map<UUID, Account> accountsStorage;

    public AccountRepositoryImpl(){
        this.accountsStorage = new ConcurrentHashMap<>();
    }

    @Override
    public void save(final Account account) {
        accountsStorage.put(account.getId(), account);
    }

    @Override
    public void saveAll(Collection<Account> accounts) {
        accounts.forEach(account -> accountsStorage.put(account.getId(), account));
    }

    @Override
    public Account getAccountById(final UUID id) {
        return accountsStorage.get(id);
    }

    @Override
    public Collection<Account> getAllAccounts() {
        return accountsStorage.values();
    }

    @Override
    public void deleteAccount(final UUID accountId) {
        accountsStorage.remove(accountId);
    }

    @Override
    public void deleteAllAccounts() {
        accountsStorage.clear();
    }

    @Override
    public boolean isAccountExist(final String userId) {
        return accountsStorage.values().stream().anyMatch(account -> account.getUserId().equals(userId));
    }
}
