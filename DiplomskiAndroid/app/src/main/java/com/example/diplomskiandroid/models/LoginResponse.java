package com.example.diplomskiandroid.models;

public class LoginResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String role;
    private String token;
    private String expiresAt;


    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getToken() { return token; }
    public String getExpiresAt() { return expiresAt; }
}
