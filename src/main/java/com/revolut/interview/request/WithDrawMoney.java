package com.revolut.interview.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode
@JsonSerialize
public class WithDrawMoney {

    private UUID accountId;
    private BigDecimal amount;
}
