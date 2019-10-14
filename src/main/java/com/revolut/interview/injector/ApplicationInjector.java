package com.revolut.interview.injector;

import com.google.inject.AbstractModule;

import com.revolut.interview.repository.AccountRepository;
import com.revolut.interview.repository.AccountRepositoryImpl;
import com.revolut.interview.service.AccountService;
import com.revolut.interview.service.AccountServiceImpl;

public class ApplicationInjector extends AbstractModule {
    @Override
    protected void configure() {
        bind(AccountRepository.class).to(AccountRepositoryImpl.class);
        bind(AccountService.class).to(AccountServiceImpl.class);
    }
}
