package com.revolut.interview.controller;

import com.google.gson.Gson;
import com.google.inject.Inject;

import com.revolut.interview.request.CreateAccount;
import com.revolut.interview.request.DepositMoney;
import com.revolut.interview.request.TransferMoney;
import com.revolut.interview.request.WithDrawMoney;
import com.revolut.interview.response.JsonResponse;
import com.revolut.interview.service.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;

import static com.revolut.interview.response.Status.SUCCESS;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

public class AccountController {

    private AccountService accountService;

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Inject
    public AccountController(final AccountService accountService) {this.accountService = accountService;}

    public void registerAccountApiRoutes() {

        get("/account/:accountId", (request, response) -> {
            final UUID accountId = UUID.fromString(Objects.requireNonNull(request.params(":accountId")));
            logger.info(String.format("Received request for account lookup: %s", accountId));
            return new Gson().toJson(
                    new JsonResponse(SUCCESS, new Gson()
                            .toJsonTree(accountService.getAccount(accountId))));
        });

        post("/account", (request, response) -> {
            final CreateAccount createAccount = new Gson().fromJson(request.body(), CreateAccount.class);
            logger.info(String.format("Received request for creating new account for: %s", createAccount));
            return new Gson().toJson(
                    new JsonResponse(SUCCESS, "New account has been created",
                                     new Gson().toJsonTree(accountService.createAccount(createAccount))));
        });

        get("/accounts", (request, response) -> {
            logger.info("Received request for getting all existing accounts");
            return new Gson().toJson(
                    new JsonResponse(SUCCESS, new Gson()
                            .toJsonTree(accountService.getAllAccounts())));
        });

        delete("/account/:accountId", (request, response) -> {
            final UUID accountId = UUID.fromString(Objects.requireNonNull(request.params(":accountId")));
            logger.info(String.format("Received request for deleting account: %s", accountId));
            accountService.deleteAccount(accountId);
            return new Gson().toJson(
                    new JsonResponse(SUCCESS,
                                     String.format("Account [%s] has been deleted", accountId)));
        });

        delete("/accounts", (request, response) -> {
            logger.info("Received request for deleting all accounts");
            accountService.deleteAllAccounts();
            return new Gson().toJson(
                    new JsonResponse(SUCCESS,
                                     String.format("Deleting all accounts")));
        });

        post("/account/transfer", (request, response) -> {
            final TransferMoney transferMoney = new Gson().fromJson(request.body(), TransferMoney.class);
            logger.info(String.format("Received request for transfer money for: %s", transferMoney));
            accountService.transferAmount(transferMoney);
            return new Gson().toJson(
                    new JsonResponse(SUCCESS,
                                     "Money has been transferred successfully"));
        });

        put("/account/withdraw", (request, response) -> {
            final WithDrawMoney withDrawMoney = new Gson().fromJson(request.body(), WithDrawMoney.class);
            logger.info(String.format("Received request for withdraw money from account: %s", withDrawMoney.getAccountId()));
            accountService.withDrawMoney(withDrawMoney);
            return new Gson().toJson(
                    new JsonResponse(SUCCESS,
                                     String.format("Amount has been withdrawn from account: %s", withDrawMoney.getAccountId())));
        });

        put("/account/deposit", (request, response) -> {
            final DepositMoney depositMoney = new Gson().fromJson(request.body(), DepositMoney.class);
            logger.info(String.format("Received request for deposit money to account: %s", depositMoney.getAccountId()));
            accountService.depositMoney(depositMoney);
            return new Gson().toJson(
                    new JsonResponse(SUCCESS,
                                     String.format("Amount has been deposit to account: %s", depositMoney.getAccountId())));
        });
    }
}
