package com.revolut.interview.service;

import com.revolut.interview.data.MoneyTransferData;
import com.revolut.interview.execption.AccountAlreadyExistException;
import com.revolut.interview.execption.AccountNotExistsException;
import com.revolut.interview.execption.InsufficientBalanceException;
import com.revolut.interview.mapper.AccountMapper;
import com.revolut.interview.model.Account;
import com.revolut.interview.repository.AccountRepository;
import com.revolut.interview.request.CreateAccount;
import com.revolut.interview.response.AccountResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.revolut.interview.data.MoneyTransferData.getAccountInstance;
import static com.revolut.interview.data.MoneyTransferData.getDepositMoneyInstance;
import static com.revolut.interview.data.MoneyTransferData.getTransferMoneyInstance;
import static com.revolut.interview.data.MoneyTransferData.getWithDrawMoneyInstance;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final AccountMapper accountMapper = mock(AccountMapper.class);

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_new_account_creation() {
        given(accountRepository.isAccountExist(anyString())).willReturn(false);
        given(accountMapper.createFrom(any(CreateAccount.class), any(UUID.class))).willReturn(any(Account.class));

        accountServiceImpl.createAccount(getAccountInstance("Zulqarnain", 20, "GBP"));
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test(expected = AccountAlreadyExistException.class)
    public void test_duplicate_account_creation() {
        given(accountRepository.isAccountExist(anyString())).willReturn(true);
        accountServiceImpl.createAccount(getAccountInstance("Zulqarnain", 120, "GBP"));
    }

    @Test
    public void test_get_account() {
        final UUID accountId = UUID.randomUUID();
        final String userId = "Zulqarnain";
        final Account account = getAccountInstance(accountId, userId, 20);
        given(accountRepository.getAccountById(accountId)).willReturn(account);

        final AccountResponse response = MoneyTransferData.getAccountResponseInstance(accountId, userId, 20);
        given(accountMapper.createFrom(account)).willReturn(response);
        final AccountResponse accountResponse = accountServiceImpl.getAccount(accountId);

        verify(accountRepository, times(1)).getAccountById(accountId);
        assertEquals(response, accountResponse);
    }

    @Test(expected = AccountNotExistsException.class)
    public void test_non_existing_account() {
        given(accountRepository.getAccountById(any(UUID.class))).willReturn(null);
        accountServiceImpl.getAccount(UUID.randomUUID());
    }

    @Test
    public void test_retrieve_all_accounts() {
        final UUID accountId = UUID.randomUUID();
        final String userId = "Zulqarnain";
        final Account account = getAccountInstance(accountId, userId, 60);
        given(accountRepository.getAllAccounts()).willReturn(Arrays.asList(account));

        final AccountResponse response = MoneyTransferData.getAccountResponseInstance(accountId, userId, 60);
        given(accountMapper.createFrom(account)).willReturn(response);

        final List<AccountResponse> accountResponseList = accountServiceImpl.getAllAccounts();

        verify(accountRepository, times(1)).getAllAccounts();
        assertEquals(accountResponseList.size(), 1);
        assertEquals(response, accountResponseList.get(0));
    }

    @Test
    public void test_account_deletion() {
        final UUID accountId = UUID.randomUUID();
        final Account account = getAccountInstance(accountId, "Zulqarnain", 30);
        given(accountRepository.getAccountById(accountId)).willReturn(account);

        accountServiceImpl.deleteAccount(accountId);
        verify(accountRepository, times(1)).deleteAccount(accountId);
    }

    @Test(expected = AccountNotExistsException.class)
    public void test_non_existing_account_deletion() {
        given(accountRepository.getAccountById(any(UUID.class))).willReturn(null);
        accountServiceImpl.deleteAccount(UUID.randomUUID());
    }

    @Test
    public void test_deposit_money() {
        final UUID accountId = UUID.randomUUID();
        final String userId = "Zulqarnain";
        final Account account = getAccountInstance(accountId, userId, 60);
        given(accountRepository.getAccountById(accountId)).willReturn(account);

        accountServiceImpl.depositMoney(getDepositMoneyInstance(accountId.toString(), 20));
        verify(accountRepository, times(1)).save(getAccountInstance(accountId, userId, 80));
    }

    @Test(expected = AccountNotExistsException.class)
    public void test_deposit_money_in_non_existing_account() {
        given(accountRepository.getAccountById(any(UUID.class))).willReturn(null);
        accountServiceImpl.depositMoney(getDepositMoneyInstance(UUID.randomUUID().toString(), 10));
    }

    @Test
    public void test_withdraw_money() {
        final UUID accountId = UUID.randomUUID();
        final String userId = "Zulqarnain";
        final Account account = getAccountInstance(accountId, userId, 60);
        given(accountRepository.getAccountById(accountId)).willReturn(account);

        accountServiceImpl.withDrawMoney(getWithDrawMoneyInstance(accountId.toString(), 20));
        verify(accountRepository, times(1)).save(getAccountInstance(accountId, userId, 40));
    }

    @Test(expected = InsufficientBalanceException.class)
    public void test_withdraw_insufficient_money() {
        final UUID accountId = UUID.randomUUID();
        final Account account = getAccountInstance(accountId, "Zulqarnain", 160);
        given(accountRepository.getAccountById(accountId)).willReturn(account);

        accountServiceImpl.withDrawMoney(getWithDrawMoneyInstance(accountId.toString(), 190));
    }

    @Test(expected = AccountNotExistsException.class)
    public void test_withdraw_money_from_non_existing_account() {
        given(accountRepository.getAccountById(any(UUID.class))).willReturn(null);
        accountServiceImpl.withDrawMoney(getWithDrawMoneyInstance(UUID.randomUUID().toString(), 10));
    }

    @Test
    public void test_transfer_money() {
        final UUID fromAccountId = UUID.randomUUID();
        final String fromUserId = "Zulqarnain";
        final Account fromAccount = getAccountInstance(fromAccountId, fromUserId, 60);
        given(accountRepository.getAccountById(fromAccountId)).willReturn(fromAccount);

        final UUID toAccountId = UUID.randomUUID();
        final String toUserId = "Adnan";
        final Account toAccount = getAccountInstance(toAccountId, toUserId, 20);
        given(accountRepository.getAccountById(toAccountId)).willReturn(toAccount);

        accountServiceImpl.transferAmount(getTransferMoneyInstance(fromAccountId.toString(), toAccountId.toString(), 30));

        fromAccount.setBalance(new BigDecimal(30));
        toAccount.setBalance(new BigDecimal(50));
        verify(accountRepository, times(1)).saveAll(Arrays.asList(fromAccount, toAccount));
    }

    @Test(expected = InsufficientBalanceException.class)
    public void test_transfer_insufficient_money() {
        final UUID fromAccountId = UUID.randomUUID();
        final String fromUserId = "Zulqarnain";
        final Account fromAccount = getAccountInstance(fromAccountId, fromUserId, 60);
        given(accountRepository.getAccountById(fromAccountId)).willReturn(fromAccount);

        final UUID toAccountId = UUID.randomUUID();
        final String toUserId = "Adnan";
        final Account toAccount = getAccountInstance(toAccountId, toUserId, 20);
        given(accountRepository.getAccountById(toAccountId)).willReturn(toAccount);

        accountServiceImpl.transferAmount(getTransferMoneyInstance(fromAccountId.toString(), toAccountId.toString(), 80));
    }

    @Test(expected = AccountNotExistsException.class)
    public void test_transfer_money_in_non_existing_account() {
        final UUID fromAccountId = UUID.randomUUID();
        final String fromUserId = "Zulqarnain";
        final Account fromAccount = getAccountInstance(fromAccountId, fromUserId, 60);
        given(accountRepository.getAccountById(fromAccountId)).willReturn(fromAccount);

        final UUID toAccountId = UUID.randomUUID();
        given(accountRepository.getAccountById(toAccountId)).willReturn(null);

        accountServiceImpl.transferAmount(getTransferMoneyInstance(fromAccountId.toString(), toAccountId.toString(), 80));
    }
}
