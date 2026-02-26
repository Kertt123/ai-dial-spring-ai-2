package com.serkowski.task9.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.ai.tool.annotation.ToolParam;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record User(int id,
                   String name,
                   String surname,
                   String email,
                   String phone,
                   String date_of_birth,
                   @ToolParam(description = "User address. Set null if not provided.")
                   UserAddress address,
                   String gender,
                   String company,
                   Double salary,
                   String about_me,
                   @ToolParam(description = "Credit card info. Set null if not provided.")
                   CreditCard credit_card,
                   String created_at) {
}
