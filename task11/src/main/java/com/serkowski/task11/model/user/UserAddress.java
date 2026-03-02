package com.serkowski.task11.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UserAddress(
        String country,
        String city,
        String street,
        String flat_house) {
}
