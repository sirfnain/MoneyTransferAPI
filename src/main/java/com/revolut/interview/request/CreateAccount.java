package com.revolut.interview.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.util.Currency;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@Data
@EqualsAndHashCode
@ToString
@JsonSerialize
public class CreateAccount {

    private String     userId;
    private BigDecimal balance;
    private Currency   currency;
}
