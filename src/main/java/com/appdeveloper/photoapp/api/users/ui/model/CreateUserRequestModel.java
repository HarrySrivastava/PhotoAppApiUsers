package com.appdeveloper.photoapp.api.users.ui.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateUserRequestModel {
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @NotNull(message = "First name can not be null")
    @Size(min = 2 ,message = "First name can not be less than 2 characters")
    private String firstName;
    @NotNull(message = "last name can not be null")
    @Size(min = 2 ,message = "last name can not be less than 2 characters")
    private String lastName;
    @NotNull(message = "password can not be null")
    @Size(min = 8,max = 16,message = "password should be between 8 to 16 characters")
    private String password;
    @NotNull(message = "Email can not be null")
    @Email
    private String email;

}
