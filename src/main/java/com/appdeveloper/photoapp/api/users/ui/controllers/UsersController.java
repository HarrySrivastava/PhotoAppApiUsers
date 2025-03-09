package com.appdeveloper.photoapp.api.users.ui.controllers;

import com.appdeveloper.photoapp.api.users.service.UsersService;
import com.appdeveloper.photoapp.api.users.shared.UserDto;
import com.appdeveloper.photoapp.api.users.ui.model.CreateUserRequestModel;
import com.appdeveloper.photoapp.api.users.ui.model.CreateUserResponseModel;
import com.appdeveloper.photoapp.api.users.ui.model.UserResponseModel;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private Environment env;
    @Autowired
    UsersService usersService;

    @GetMapping("/status/check")
    public String status() {
        return "Working on Port" + " " + env.getProperty("local.server.port") + ",with token =" + env.getProperty("token.secret");
    }

    @PostMapping
    public ResponseEntity<CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel userDetails) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);
        UserDto createdUser = usersService.createUser(userDto);
        CreateUserResponseModel returnValue = modelMapper.map(createdUser, CreateUserResponseModel.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }
    // @PostAuthorize("principal ==returnObject.body.userId")
   //  @PreAuthorize("principal== #userId")
    @PreAuthorize("hasRole('ADMIN') or principal== #userId")
    @GetMapping(value = "/{userId}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserResponseModel> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = usersService.getUserByUserId(userId);
        UserResponseModel returnValue = new ModelMapper().map(userDto, UserResponseModel.class);
        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFILE_DELETE') or principal== #userId")
    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable("userId") String userId)
    {
        //Delete  User Logic here
        return "Deleting User with Id:" + userId;

    }
}
