package com.serkowski.task9_server.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CreditCard(String num, String cvv, String exp_date) {
}
