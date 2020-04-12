package com.twb.poker.domain;

import java.util.List;

import lombok.Data;

@Data
public class BetAmountRequest {
    private List<BetType> betTypes;
    private Double amount;
}
