package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUserByUserId(String userId);

    //가공하지 않고 가져오기만 하는 경우 Entity를 바로 사용해도 된다.
    Iterable<UserEntity> getUserByAll();
}
