package com.serkowski.task6_1.model;

import java.time.LocalDateTime;

public record User(int id,
                   String name,
                   String surname,
                   String email,
                   String phone,
                   String date_of_birth,
                   UserAddress address,
                   String gender,
                   String company,
                   Double salary,
                   String about_me,
                   CreditCard credit_card,
                   String created_at) {
}
