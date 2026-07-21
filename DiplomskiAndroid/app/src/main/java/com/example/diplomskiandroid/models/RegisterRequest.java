package com.example.diplomskiandroid.models;

public class RegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String address;

    public RegisterRequest(
            String firstName,
            String lastName,
            String email,
            String password,
            String phone,
            String address
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }
}