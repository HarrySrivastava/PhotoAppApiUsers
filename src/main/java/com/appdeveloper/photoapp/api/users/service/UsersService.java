package com.appdeveloper.photoapp.api.users.service;

import com.appdeveloper.photoapp.api.users.shared.UserDto;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService  extends UserDetailsService {
    UserDto createUser(UserDto userDetails);
    UserDto getUserDetailsByEmail(String email);
}
