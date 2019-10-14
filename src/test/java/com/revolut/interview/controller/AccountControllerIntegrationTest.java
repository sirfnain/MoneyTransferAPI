package com.revolut.interview.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import com.revolut.interview.MoneyTransferAPI;
import com.revolut.interview.response.AccountResponse;
import com.revolut.interview.util.CommonUtil;
import com.revolut.interview.util.Response;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static com.revolut.interview.data.MoneyTransferData.getAccountInstance;
import static com.revolut.interview.data.MoneyTransferData.getDepositMoneyInstance;
import static com.revolut.interview.data.MoneyTransferData.getTransferMoneyInstance;
import static com.revolut.interview.data.MoneyTransferData.getWithDrawMoneyInstance;
import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

public class AccountControllerIntegrationTest {

    private final Gson gson = new Gson();

    @Before
    public void before() throws InterruptedException {
        MoneyTransferAPI.main(null);
        sleep(3000);
    }

    @Test
    public void test_new_account_creation() {
        final String createAccountRequest = gson.toJson(getAccountInstance("Zulqarnain", 500, "GBP"));
        final Response createAccountResponse = CommonUtil.request("POST", "account", createAccountRequest);
        assertEquals(HttpStatus.OK_200, createAccountResponse.getStatus());
    }

    @Test
    public void test_duplicate_account() {
        final String createAccountRequest = gson.toJson(getAccountInstance("Zulqarnain", 500, "GBP"));
        final Response createAccountResponse = CommonUtil.request("POST", "account", createAccountRequest);
        assertEquals(HttpStatus.OK_200, createAccountResponse.getStatus());

        final String createAccountRequestDuplicate = gson.toJson(getAccountInstance("Zulqarnain", 500, "GBP"));
        final Response createAccountResponseDuplicate = CommonUtil.request("POST", "account", createAccountRequestDuplicate);
        assertEquals(HttpStatus.CONFLICT_409, createAccountResponseDuplicate.getStatus());
    }

    @Test
    public void test_get_account() {
        final String createAccountRequest = gson.toJson(getAccountInstance("Zulqarnain", 250, "GBP"));
        final Response createAccountResponse = CommonUtil.request("POST", "account", createAccountRequest);
        assertEquals(HttpStatus.OK_200, createAccountResponse.getStatus());

        final String accountId = createAccountResponse.getData().getAsString();
        final Response getAccountResponse = CommonUtil.request("GET", "account/"+accountId);
        assertEquals(HttpStatus.OK_200, getAccountResponse.getStatus());

        final AccountResponse accountResponse = gson.fromJson(getAccountResponse.getData(), AccountResponse.class);
        assertEquals(accountId, accountResponse.getAccountId().toString());
    }

    @Test
    public void test_non_existing_account() {
        final String accountId = UUID.randomUUID().toString();
        final Response getAccountResponse = CommonUtil.request("GET", "account/"+accountId);
        assertEquals(HttpStatus.NOT_FOUND_404, getAccountResponse.getStatus());
    }

    @Test
    public void test_retrieve_all_accounts() {
        final String createAccountRequest_1 = gson.toJson(getAccountInstance("Zulqarnain", 200, "GBP"));
        final Response createAccountResponse_1 = CommonUtil.request("POST", "account", createAccountRequest_1);
        assertEquals(HttpStatus.OK_200, createAccountResponse_1.getStatus());

        final String createAccountRequest_2 = gson.toJson(getAccountInstance("Hafeez", 50, "PLN"));
        final Response createAccountResponse_2 = CommonUtil.request("POST", "account", createAccountRequest_2);
        assertEquals(HttpStatus.OK_200, createAccountResponse_2.getStatus());

        final Response response = CommonUtil.request("GET", "accounts");
        final JsonElement json = response.jsonElement();
        assertEquals(HttpStatus.OK_200, response.getStatus());
        assertEquals(2, json.getAsJsonObject().size());
    }

    @Test
    public void test_account_deletion() {
        final String createAccountRequest = gson.toJson(getAccountInstance("Zulqarnain", 40, "GBP"));
        final Response createAccountResponse = CommonUtil.request("POST", "account", createAccountRequest);
        assertEquals(HttpStatus.OK_200, createAccountResponse.getStatus());

        final String accountId = createAccountResponse.getData().getAsString();
        final Response getAccountResponse = CommonUtil.request("DELETE", "account/"+accountId);
        assertEquals(HttpStatus.OK_200, getAccountResponse.getStatus());
    }

    @Test
    public void test_non_existing_account_deletion() {
        final String accountId = UUID.randomUUID().toString();
        final Response getAccountResponse = CommonUtil.request("DELETE", "account/"+accountId);
        assertEquals(HttpStatus.NOT_FOUND_404, getAccountResponse.getStatus());
    }

    @Test
    public void test_deposit_money() {
        final String createAccountRequest = gson.toJson(getAccountInstance("Zulqarnain", 20, "GBP"));
        final Response createAccountResponse = CommonUtil.request("POST", "account", createAccountRequest);
        assertEquals(HttpStatus.OK_200, createAccountResponse.getStatus());

        final String accountId = createAccountResponse.getData().getAsString();
        final String depositMoneyRequest = gson.toJson(getDepositMoneyInstance(accountId, 100));
        final Response depositMoneyResponse = CommonUtil.request("PUT", "account/deposit", depositMoneyRequest);
        assertEquals(HttpStatus.OK_200, depositMoneyResponse.getStatus());

        final Response getAccountResponse = CommonUtil.request("GET", "account/"+accountId);
        assertEquals(HttpStatus.OK_200, getAccountResponse.getStatus());

        final AccountResponse accountResponse = gson.fromJson(getAccountResponse.getData(), AccountResponse.class);
        assertEquals(accountId, accountResponse.getAccountId().toString());
        assertEquals(new BigDecimal(120), accountResponse.getBalance());
    }

    @Test
    public void test_deposit_money_in_non_existing_account() {
        final String accountId = UUID.randomUUID().toString();
        final String depositMoneyRequest = gson.toJson(getDepositMoneyInstance(accountId, 100));
        final Response depositMoneyResponse = CommonUtil.request("PUT", "account/deposit", depositMoneyRequest);
        assertEquals(HttpStatus.NOT_FOUND_404, depositMoneyResponse.getStatus());
    }

    @Test
    public void test_withdraw_money() {
        final String createAccountRequest = gson.toJson(getAccountInstance("Zulqarnain", 50, "GBP"));
        final Response createAccountResponse = CommonUtil.request("POST", "account", createAccountRequest);
        assertEquals(HttpStatus.OK_200, createAccountResponse.getStatus());

        final String accountId = createAccountResponse.getData().getAsString();
        final String withDrawMoneyRequest = gson.toJson(getWithDrawMoneyInstance(accountId, 30));
        final Response withDrawMoneyResponse = CommonUtil.request("PUT", "account/withdraw", withDrawMoneyRequest);
        assertEquals(HttpStatus.OK_200, withDrawMoneyResponse.getStatus());

        final Response getAccountResponse = CommonUtil.request("GET", "account/"+accountId);
        assertEquals(HttpStatus.OK_200, getAccountResponse.getStatus());

        AccountResponse accountResponse = gson.fromJson(getAccountResponse.getData(), AccountResponse.class);
        assertEquals(accountId, accountResponse.getAccountId().toString());
        assertEquals(new BigDecimal(20), accountResponse.getBalance());
    }

    @Test
    public void test_withdraw_insufficient_money() {
        final String createAccountRequest = gson.toJson(getAccountInstance("Zulqarnain", 50, "GBP"));
        final Response createAccountResponse = CommonUtil.request("POST", "account", createAccountRequest);
        assertEquals(HttpStatus.OK_200, createAccountResponse.getStatus());

        final String accountId = createAccountResponse.getData().getAsString();
        final String withDrawMoneyRequest = gson.toJson(getWithDrawMoneyInstance(accountId, 60));
        final Response withDrawMoneyResponse = CommonUtil.request("PUT", "account/withdraw", withDrawMoneyRequest);
        assertEquals(HttpStatus.BAD_REQUEST_400, withDrawMoneyResponse.getStatus());
    }

    @Test
    public void test_withdraw_money_from_non_existing_account() {
        final String accountId = UUID.randomUUID().toString();
        final String withDrawMoneyRequest = gson.toJson(getWithDrawMoneyInstance(accountId, 100));
        final Response withDrawMoneyResponse = CommonUtil.request("PUT", "account/withdraw", withDrawMoneyRequest);
        assertEquals(HttpStatus.NOT_FOUND_404, withDrawMoneyResponse.getStatus());
    }

    @Test
    public void test_transfer_money() {
        final String createAccountRequest_1 = gson.toJson(getAccountInstance("Zulqarnain", 70, "GBP"));
        final Response createAccountResponse_1 = CommonUtil.request("POST", "account", createAccountRequest_1);
        assertEquals(HttpStatus.OK_200, createAccountResponse_1.getStatus());

        final String createAccountRequest_2 = gson.toJson(getAccountInstance("Adnan", 20, "GBP"));
        final Response createAccountResponse_2 = CommonUtil.request("POST", "account", createAccountRequest_2);
        assertEquals(HttpStatus.OK_200, createAccountResponse_2.getStatus());

        final String fromAccount = createAccountResponse_1.getData().getAsString();
        final String toAccount = createAccountResponse_2.getData().getAsString();

        final String transferMoneyRequest = gson.toJson(getTransferMoneyInstance(fromAccount, toAccount, 30));
        final Response transferMoneyResponse = CommonUtil.request("POST", "account/transfer", transferMoneyRequest);
        assertEquals(HttpStatus.OK_200, transferMoneyResponse.getStatus());

        final Response getFromAccountResponse = CommonUtil.request("GET", "account/"+fromAccount);
        assertEquals(HttpStatus.OK_200, getFromAccountResponse.getStatus());

        final AccountResponse fromAccountResponse = gson.fromJson(getFromAccountResponse.getData(), AccountResponse.class);
        assertEquals(fromAccount, fromAccountResponse.getAccountId().toString());
        assertEquals(new BigDecimal(40), fromAccountResponse.getBalance());

        final Response getToAccountResponse = CommonUtil.request("GET", "account/"+toAccount);
        assertEquals(HttpStatus.OK_200, getToAccountResponse.getStatus());

        final AccountResponse toAccountResponse = gson.fromJson(getToAccountResponse.getData(), AccountResponse.class);
        assertEquals(toAccount, toAccountResponse.getAccountId().toString());
        assertEquals(new BigDecimal(50), toAccountResponse.getBalance());
    }

    @Test
    public void test_transfer_insufficient_money() {
        final String createAccountRequest_1 = gson.toJson(getAccountInstance("Zulqarnain", 60, "GBP"));
        final Response createAccountResponse_1 = CommonUtil.request("POST", "account", createAccountRequest_1);
        assertEquals(HttpStatus.OK_200, createAccountResponse_1.getStatus());

        final String createAccountRequest_2 = gson.toJson(getAccountInstance("Adnan", 30, "GBP"));
        final Response createAccountResponse_2 = CommonUtil.request("POST", "account", createAccountRequest_2);
        assertEquals(HttpStatus.OK_200, createAccountResponse_2.getStatus());

        final String fromAccount = createAccountResponse_1.getData().getAsString();
        final String toAccount = createAccountResponse_2.getData().getAsString();

        final String transferMoneyRequest = gson.toJson(getTransferMoneyInstance(fromAccount, toAccount, 80));
        final Response transferMoneyResponse = CommonUtil.request("POST", "account/transfer", transferMoneyRequest);
        assertEquals(HttpStatus.BAD_REQUEST_400, transferMoneyResponse.getStatus());
    }

    @Test
    public void test_transfer_money_in_non_existing_account() {
        final String createAccountRequest_1 = gson.toJson(getAccountInstance("Zulqarnain", 90, "GBP"));
        final Response createAccountResponse_1 = CommonUtil.request("POST", "account", createAccountRequest_1);
        assertEquals(HttpStatus.OK_200, createAccountResponse_1.getStatus());

        final String fromAccount = createAccountResponse_1.getData().getAsString();
        final String toAccount = UUID.randomUUID().toString();

        final String transferMoneyRequest = gson.toJson(getTransferMoneyInstance(fromAccount, toAccount, 30));
        final Response transferMoneyResponse = CommonUtil.request("POST", "account/transfer", transferMoneyRequest);
        assertEquals(HttpStatus.NOT_FOUND_404, transferMoneyResponse.getStatus());
    }

    @After
    public void after() {
        CommonUtil.request("DELETE", "accounts");
    }
}
