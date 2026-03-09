package com.serkowski.task14_ums.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CreditCard(String num, String cvv, String exp_date) {
}
