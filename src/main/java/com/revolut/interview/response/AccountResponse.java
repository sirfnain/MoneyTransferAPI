package com.revolut.interview.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@Data
@EqualsAndHashCode
@ToString
@JsonSerialize
public class AccountResponse {

    private UUID       accountId;
    private String     userId;
    private BigDecimal balance;
    private Currency   currency;
}
