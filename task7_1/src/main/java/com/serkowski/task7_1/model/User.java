package com.serkowski.task7_1.model;

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
