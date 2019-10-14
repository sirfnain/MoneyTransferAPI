package com.revolut.interview.service;

import com.google.inject.Inject;

import com.revolut.interview.execption.AccountAlreadyExistException;
import com.revolut.interview.execption.AccountNotExistsException;
import com.revolut.interview.execption.InsufficientBalanceException;
import com.revolut.interview.mapper.AccountMapper;
import com.revolut.interview.model.Account;
import com.revolut.interview.repository.AccountRepository;
import com.revolut.interview.request.CreateAccount;
import com.revolut.interview.request.DepositMoney;
import com.revolut.interview.request.TransferMoney;
import com.revolut.interview.request.WithDrawMoney;
import com.revolut.interview.response.AccountResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Inject
    public AccountServiceImpl(final AccountRepository accountRepository, final AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    public UUID createAccount(final CreateAccount createAccount) {
        lock.writeLock().lock();
        try {
            logger.info(String.format("Request for creating account: %s", createAccount));
            if(this.isAccountExist(createAccount.getUserId())) {
                throw new AccountAlreadyExistException(String.format("Account already exists for userId: %s", createAccount.getUserId()));
            }

            final UUID accountId = UUID.randomUUID();
            logger.info(String.format("New account number generated: %s", accountId));
            final Account account = accountMapper.createFrom(createAccount, accountId);

            this.accountRepository.save(account);
            return accountId;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public AccountResponse getAccount(final UUID accountId) {
        lock.readLock().lock();
        try {
            logger.info(String.format("Request for get account for account Id: %s", accountId));
            final Account account = getAccountFromRepository(accountId);
            return accountMapper.createFrom(account);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        lock.readLock().lock();
        try {
            logger.info("Request for get all accounts in the system");
            final Collection<Account> accounts = accountRepository.getAllAccounts();
            return accounts.stream()
                    .map(accountMapper::createFrom)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void deleteAccount(final UUID accountId) {
        lock.writeLock().lock();
        try {
            logger.info(String.format("Request for deleting account for account Id: %s", accountId));
            final Account account = getAccountFromRepository(accountId);
            accountRepository.deleteAccount(account.getId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void deleteAllAccounts() {
        lock.writeLock().lock();
        try {
            logger.info("Request for deleting all accounts");
            accountRepository.deleteAllAccounts();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void transferAmount(final TransferMoney transferMoney){
        lock.writeLock().lock();
        try{
            logger.info(String.format("Request for transfer money for: %s", transferMoney.toString()));
            final Account fromAccount = getAccountFromRepository(transferMoney.getFromAccount());
            final Account toAccount = getAccountFromRepository(transferMoney.getToAccount());

            if(fromAccount.getBalance().compareTo(transferMoney.getAmount()) < 0){
                throw new InsufficientBalanceException(String.format("Insufficient balance in account id: %s", transferMoney.getFromAccount()));
            }

            fromAccount.setBalance(fromAccount.getBalance().subtract(transferMoney.getAmount()));
            toAccount.setBalance(toAccount.getBalance().add(transferMoney.getAmount()));

            // This method call only needed in-case of change from in memory data store (ConcurrentMap) to other type of repository
            accountRepository.saveAll(Arrays.asList(fromAccount, toAccount));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void withDrawMoney(final WithDrawMoney withDrawMoney) {
        lock.writeLock().lock();
        try{
            logger.info(String.format("Request for withdraw money from account Id: %s", withDrawMoney.getAccountId().toString()));
            final Account account = getAccountFromRepository(withDrawMoney.getAccountId());

            if(account.getBalance().compareTo(withDrawMoney.getAmount()) < 0){
                throw new InsufficientBalanceException(String.format("Insufficient balance in account id: %s", withDrawMoney.getAccountId()));
            }
            account.setBalance(account.getBalance().subtract(withDrawMoney.getAmount()));

            accountRepository.save(account);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void depositMoney(final DepositMoney depositMoney) {
        lock.writeLock().lock();
        try{
            logger.info(String.format("Request for deposit money to account Id: %s", depositMoney.getAccountId()));
            final Account account = getAccountFromRepository(depositMoney.getAccountId());
            account.setBalance(account.getBalance().add(depositMoney.getAmount()));

            accountRepository.save(account);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean isAccountExist(String userId) {
        lock.readLock().lock();
        try {
            logger.info(String.format("Searching the account for userId: %s", userId));
            return accountRepository.isAccountExist(userId);
        } finally {
            lock.readLock().unlock();
        }
    }

    private Account getAccountFromRepository(final UUID accountId) {
        logger.info(String.format("Getting account detail from store for accountId: %s", accountId));
        final Account account = accountRepository.getAccountById(accountId);
        if(account == null){
            throw new AccountNotExistsException(String.format("Account not exist for accountId: %s", accountId));
        }
        return account;
    }
}
