package com.serkowski.task11.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CreditCard(String num, String cvv, String exp_date) {
}
