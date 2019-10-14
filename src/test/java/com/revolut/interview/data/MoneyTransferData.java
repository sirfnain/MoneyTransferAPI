package com.revolut.interview.data;

import com.revolut.interview.model.Account;
import com.revolut.interview.request.CreateAccount;
import com.revolut.interview.request.DepositMoney;
import com.revolut.interview.request.TransferMoney;
import com.revolut.interview.request.WithDrawMoney;
import com.revolut.interview.response.AccountResponse;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class MoneyTransferData {
    public static CreateAccount getAccountInstance(final String userId, final double balance, final String currencyCode){
        return CreateAccount.builder().userId(userId)
                            .balance(new BigDecimal(balance))
                            .currency(Currency.getInstance(currencyCode))
                            .build();
    }

    public static DepositMoney getDepositMoneyInstance(final String accountId, final double amount){
        return DepositMoney.builder()
                           .accountId(UUID.fromString(accountId))
                           .amount(new BigDecimal(amount))
                           .build();
    }

    public static WithDrawMoney getWithDrawMoneyInstance(final String accountId, final double amount){
        return WithDrawMoney.builder()
                            .accountId(UUID.fromString(accountId))
                            .amount(new BigDecimal(amount))
                            .build();
    }

    public static TransferMoney getTransferMoneyInstance(final String fromAccount, final String toAccount, final double amount){
        return TransferMoney.builder()
                            .fromAccount(UUID.fromString(fromAccount))
                            .toAccount(UUID.fromString(toAccount))
                            .amount(new BigDecimal(amount))
                            .build();
    }

    public static Account getAccountInstance(final UUID accountId, final String userId, final double amount){
        return Account.builder()
                              .id(accountId)
                              .userId(userId)
                              .balance(new BigDecimal(amount))
                              .currencyCode(Currency.getInstance("GBP"))
                              .build();
    }

    public static AccountResponse getAccountResponseInstance(final UUID accountId, final String userId, final double amount){
        return AccountResponse.builder()
                            .accountId(accountId)
                            .userId(userId)
                            .balance(new BigDecimal(amount))
                            .currency(Currency.getInstance("GBP"))
                            .build();
    }
}
