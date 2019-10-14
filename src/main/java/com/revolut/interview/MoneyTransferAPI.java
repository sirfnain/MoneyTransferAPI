package com.revolut.interview;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

import com.revolut.interview.controller.AccountController;
import com.revolut.interview.execption.AccountAlreadyExistException;
import com.revolut.interview.execption.AccountNotExistsException;
import com.revolut.interview.execption.InsufficientBalanceException;
import com.revolut.interview.injector.ApplicationInjector;
import com.revolut.interview.response.JsonResponse;
import com.revolut.interview.response.Status;

import org.eclipse.jetty.http.HttpStatus;

import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

import static spark.Spark.exception;

@SwaggerDefinition(host = "localhost:4567",//
                   info = @Info(description = "Money Transfer API",//
                                version = "V1.0",//
                                title = "Money transfer API",//
                                contact = @Contact(name = "Zulqarnain Hafeez") ) ,//
                   schemes = { SwaggerDefinition.Scheme.HTTP },//
                   consumes = { "application/json" },//
                   produces = { "application/json" },//
                   tags = { @Tag(name = "swagger") })
public class MoneyTransferAPI {

    public static void main(String[] args) {
        setExceptionHandlers();
        startApplication();
    }

    private static void startApplication() {
        Injector injector = Guice.createInjector(new ApplicationInjector());
        AccountController accountController = injector.getInstance(AccountController.class);
        accountController.registerAccountApiRoutes();
    }

    private static void setExceptionHandlers() {
        exception(AccountAlreadyExistException.class, (ex, request, response) -> {
            response.status(HttpStatus.CONFLICT_409);
            response.body(new Gson().toJson(
                    new JsonResponse(Status.ERROR, ex.getMessage())));
        });

        exception(AccountNotExistsException.class, (ex, request, response) -> {
            response.status(HttpStatus.NOT_FOUND_404);
            response.body(new Gson().toJson(
                    new JsonResponse(Status.ERROR, ex.getMessage())));
        });

        exception(InsufficientBalanceException.class, (ex, request, response) -> {
            response.status(HttpStatus.BAD_REQUEST_400);
            response.body(new Gson().toJson(
                    new JsonResponse(Status.ERROR, ex.getMessage())));
        });

        exception(Exception.class, (ex, request, response) -> {
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            response.body(new Gson().toJson(
                    new JsonResponse(Status.ERROR, ex.getMessage())));
        });
    }
}
