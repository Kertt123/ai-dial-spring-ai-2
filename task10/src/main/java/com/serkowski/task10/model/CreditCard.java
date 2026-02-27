package com.serkowski.task10.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CreditCard(String num, String cvv, String exp_date) {
}
