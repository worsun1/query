package com.example.query.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class CustomerSearchRequest {

    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    @Email(message = "email must be a valid email address")
    @Size(max = 255, message = "email must be at most 255 characters")
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
