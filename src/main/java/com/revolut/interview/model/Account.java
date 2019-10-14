package com.revolut.interview.model;

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
public class Account {
    private UUID       id;
    private String     userId;
    private BigDecimal balance;
    private Currency   currencyCode; // Assuming currencyCode is same for all accounts for speciality

}
