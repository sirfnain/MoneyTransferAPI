package com.revolut.interview.request;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
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
public class TransferMoney {

    private UUID fromAccount;
    private UUID toAccount;
    private BigDecimal amount;
}
